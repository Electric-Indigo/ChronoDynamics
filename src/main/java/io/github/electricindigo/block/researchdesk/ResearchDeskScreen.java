package io.github.electricindigo.block.researchdesk;

import io.github.electricindigo.ChronoDynamics;
import io.github.electricindigo.network.AssembleBlueprintPayload;
import io.github.electricindigo.network.DraftBlueprintPayload;
import io.github.electricindigo.network.UnlockResearchPayload;
import io.github.electricindigo.registry.ModAttachments;
import io.github.electricindigo.registry.ModItems;
import io.github.electricindigo.research.blueprint.Blueprint;
import io.github.electricindigo.research.blueprint.BlueprintManager;
import io.github.electricindigo.research.blueprint.Blueprints;
import io.github.electricindigo.research.tree.NodeState;
import io.github.electricindigo.research.tree.ResearchNode;
import io.github.electricindigo.research.tree.ResearchTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import javax.annotation.Nullable;
import java.util.*;

public class ResearchDeskScreen extends AbstractContainerScreen<ResearchDeskMenu>
{
    private static final Identifier TAB_ICONS = Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, "textures/gui/tab_icons.png");

    private static final int TREE_X = 19;
    private static final int TREE_Y = 8;
    private static final int TREE_W = 192;
    private static final int TREE_H = 71;
    private static final int INFO_H = 24;

    private static final int NODE_W = 72;
    private static final int NODE_H = 18;

    private static final String BACK_LABEL = "< Back";

    private enum Tab
    {
        DESK(Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, "textures/gui/desk_gui.png"), 0, 0),
        COMPUTER(Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, "textures/gui/computer_gui.png"), 22, 0),
        TEST(Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, "textures/gui/test_gui.png"), 44, 0);

        final Identifier background;
        final int iconU;
        final int iconV;

        Tab(Identifier background, int iconU, int iconV)
        {
            this.background = background;
            this.iconU = iconU;
            this.iconV = iconV;
        }
    }

    private Tab activeTab = Tab.DESK;
    private final Map<Tab, TabButton> tabButtons = new EnumMap<>(Tab.class);

    private double panX = 10;
    private double panY = 0;
    private boolean panning = false;
    private @Nullable ResearchNode selectedNode = null;

    private @Nullable ResearchNode infoPageNode = null;
    private int infoScroll = 0;
    private int infoMaxScroll = 0;

    private Set<String> unlockedResearch()
    {
        var player = Minecraft.getInstance().player;
        return player == null ? Set.of() : player.getData(ModAttachments.RESEARCH).unlocked();
    }

    public ResearchDeskScreen(ResearchDeskMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, 230, 219);
    }

    @Override
    protected void init()
    {
        super.init();
        tabButtons.clear();

        int index = 0;
        for (Tab tab : Tab.values())
        {
            TabButton button = new TabButton(leftPos + 230, topPos + 8 + index * 24, 22, 22,
                    TAB_ICONS, tab.iconU, tab.iconV, 256, 256,
                    () -> setTab(tab));
            addRenderableWidget(button);
            tabButtons.put(tab, button);
            index++;
        }

        updateTabButtonVisibility();
        menu.setDeskSlotsVisible(activeTab == Tab.DESK);

        draftButton = addRenderableWidget(Button.builder(Component.literal("Draft"), b -> draftSelected())
                .bounds(leftPos + 148, topPos + 94, 64, 20)
                .build());

        assembleButton = addRenderableWidget(Button.builder(Component.literal("Assemble"),
                b -> ClientPacketDistributor.sendToServer(AssembleBlueprintPayload.INSTANCE))
                .bounds(leftPos + 148, topPos + 94, 64, 20)
                .build());
    }

    private void setTab(Tab tab)
    {
        this.activeTab = tab;
        this.selectedNode = null;
        this.infoPageNode = null;
        this.panning = false;
        updateTabButtonVisibility();
        menu.setDeskSlotsVisible(tab == Tab.DESK);
    }

    private void updateTabButtonVisibility()
    {
        tabButtons.forEach((tab, button) -> button.visible = tab != activeTab);
    }

    private NodeState stateOf(ResearchNode node)
    {
        return ResearchTree.stateOf(node, unlockedResearch());
    }

    private static final int SLOT_FILL = 0xDC14376E;
    private static final int SLOT_OUTLINE = 0xFFCFE3FA;

    private static final int BP_TEXT = 0xFFE8F2FF;
    private static final int BP_DIM = 0xFF7F9CC0;
    private static final int BP_HAVE = 0xFF8CF08C;
    private static final int BP_MISSING = 0xFFFF8A80;

    private static final int TILE_X = 40, TILE_Y = 28, TILE_SIZE = 20, TILE_GAP = 2, TILES_PER_ROW = 7;

    private @Nullable Blueprint selectedDraft = null;
    private Button draftButton;
    private Button assembleButton;

    private void drawDeskSlots(GuiGraphicsExtractor graphics)
    {
        for (int i = 0; i < ResearchDeskMenu.WORKBENCH_SIZE; i++)
        {
            var slot = menu.slots.get(i);
            if (!slot.isActive()) continue;

            if (i == ResearchDeskMenu.OUTPUT_SLOT)
            {
                drawSlotBox(graphics, leftPos + slot.x - 5, topPos + slot.y - 5, 26); // bigger output box
            }
            else
            {
                drawSlotBox(graphics, leftPos + slot.x - 1, topPos + slot.y - 1, 18);
            }
        }
    }

    private void drawSlotBox(GuiGraphicsExtractor graphics, int x, int y, int size)
    {
        graphics.fill(x, y, x + size, y + size, SLOT_FILL);
        graphics.fill(x, y, x + size, y + 1, SLOT_OUTLINE);                // top
        graphics.fill(x, y + size - 1, x + size, y + size, SLOT_OUTLINE);  // bottom
        graphics.fill(x, y, x + 1, y + size, SLOT_OUTLINE);                // left
        graphics.fill(x + size - 1, y, x + size, y + size, SLOT_OUTLINE);  // right
    }

    private ItemStack blueprintSlotItem()
    {
        return menu.getWorkbench().getItem(ResearchDeskMenu.BLUEPRINT_SLOT);
    }

    private void drawDeskContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY)
    {
        ItemStack inSlot = blueprintSlotItem();
        int x = leftPos + 40;

        if (inSlot.isEmpty())
        {
            graphics.text(font, "Place a blueprint here,", x, topPos + 16, BP_TEXT, false);
            graphics.text(font, "or paper to draft one.", x, topPos + 27, BP_TEXT, false);
        }
        else if (inSlot.is(Items.PAPER))
        {
            drawDrafting(graphics, mouseX, mouseY);
        }
        else if (inSlot.is(ModItems.BLUEPRINT.get()))
        {
            drawSchematic(graphics, mouseX, mouseY);
        }
    }

    private void drawDrafting(GuiGraphicsExtractor graphics, int mouseX, int mouseY)
    {
        var player = minecraft.player;
        if (player == null) return;

        graphics.text(font, "Draft a blueprint:", leftPos + 40, topPos + 14, BP_TEXT, false);

        int i = 0;
        for (Blueprint bp : Blueprints.all())
        {
            int tx = leftPos + TILE_X + (i % TILES_PER_ROW) * (TILE_SIZE + TILE_GAP);
            int ty = topPos + TILE_Y + (i / TILES_PER_ROW) * (TILE_SIZE + TILE_GAP);
            boolean unlocked = BlueprintManager.isUnlocked(player, bp);
            boolean hovered = mouseX >= tx && mouseX < tx + TILE_SIZE &&  mouseY >= ty && mouseY < ty + TILE_SIZE;

            int bg = bp == selectedDraft ? 0x46FFFFFF : hovered ? 0x2CFFFFFF : 0x1EFFFFFF;
            graphics.fill(tx, ty, tx + TILE_SIZE, ty + TILE_SIZE, bg);

            if (unlocked)
            {
                graphics.item(bp.result().get(), tx + 2, ty + 2);
            }
            else
            {
                graphics.text(font, "?", tx + 8, ty + 6, BP_DIM, false);
            }

            if (hovered)
            {
                Component tip = unlocked ? bp.result().get().getHoverName() : Component.literal("Not researched yet");
                graphics.setTooltipForNextFrame(tip, mouseX, mouseY);
            }
            i++;
        }

        if (selectedDraft == null) return;

        int x = leftPos + 40;
        int y = topPos + 54;
        if (!BlueprintManager.isUnlocked(player, selectedDraft))
        {
            ResearchNode node = ResearchTree.get(selectedDraft.research());
            String researchName = node != null ? node.title() : selectedDraft.research();
            graphics.text(font, "Requires: " + researchName, x, y, BP_MISSING, false);
            return;
        }

        graphics.text(font, selectedDraft.result().get().getHoverName(), x, y, BP_TEXT, false);
        y += 12;
        graphics.text(font, "Uses 1 paper, plus:", x, y, BP_DIM, false);
        y += 12;
        drawDraftCost(graphics, new ItemStack(ModItems.DRAFTING_INK.get()), x, y, mouseX, mouseY);
        drawDraftCost(graphics, new ItemStack(selectedDraft.draftItem().get()), x + 44, y, mouseX, mouseY);
    }

    private void drawDraftCost(GuiGraphicsExtractor graphics, ItemStack icon, int x, int y, int mouseX, int mouseY)
    {
        int have = BlueprintManager.count(minecraft.player, icon.getItem());
        graphics.item(icon, x, y);
        graphics.text(font, have + "/1", x + 18, y + 4, have >= 1 ? BP_HAVE : BP_MISSING, false);

        if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16)
        {
            graphics.setTooltipForNextFrame(icon.getHoverName(), mouseX, mouseY);
        }
    }

    private @Nullable Blueprint draftTileAt(double mouseX, double mouseY)
    {
        int i = 0;
        for (Blueprint bp : Blueprints.all())
        {
            int tx = leftPos + TILE_X + (i % TILES_PER_ROW) * (TILE_SIZE + TILE_GAP);
            int ty = topPos + TILE_Y + (i / TILES_PER_ROW) * (TILE_SIZE + TILE_GAP);
            if (mouseX >= tx && mouseX < tx + TILE_SIZE && mouseY >= ty && mouseY < ty + TILE_SIZE) return bp;
            i++;
        }
        return null;
    }

    private void updateDeskButtons()
    {
        var player = minecraft.player;
        boolean deskTab = activeTab == Tab.DESK;

        boolean drafting = deskTab && blueprintSlotItem().is(Items.PAPER) && selectedDraft != null;
        draftButton.visible = drafting;
        draftButton.active = drafting && player != null && BlueprintManager.canDraft(player, selectedDraft);

        boolean assembling = deskTab && menu.getReadableBlueprint() != null;
        assembleButton.visible = assembling;
        assembleButton.active = assembling && BlueprintManager.canAssemble(menu);
    }

    private void draftSelected()
    {
        if (selectedDraft != null)
        {
            ClientPacketDistributor.sendToServer(new DraftBlueprintPayload(selectedDraft.id()));
        }
    }

    private void drawSchematic(GuiGraphicsExtractor graphics, int mouseX, int mouseY)
    {
        Blueprint bp = menu.getBlueprint();
        if (bp == null)
        {
            graphics.text(font, "This blueprint is blank.", leftPos + 40, topPos + 16, BP_DIM, false);
            return;
        }

        graphics.text(font, bp.result().get().getHoverName(), leftPos + 40, topPos + 14, BP_TEXT, false);

        // Not researched: cover the page
        if (menu.getReadableBlueprint() == null)
        {
            ResearchNode node = ResearchTree.get(bp.research());
            String researchName = node != null ? node.title() : bp.research();
            graphics.fill(leftPos + 34, topPos + 30, leftPos + 166, topPos + 92, 0xEB0F2D55);
            graphics.text(font, "You don't understand", leftPos + 46, topPos + 44, BP_MISSING, false);
            graphics.text(font, "this blueprint yet.", leftPos + 46, topPos + 55, BP_MISSING, false);
            graphics.text(font, "Requires: " + researchName, leftPos + 46, topPos + 72, BP_DIM, false);
            return;
        }

        // Parts: ghost when empty, amount needed underneath
        for (int i = 0; i < bp.costs().size(); i++)
        {
            Blueprint.Cost cost = bp.costs().get(i);
            int sx = leftPos + ResearchDeskMenu.PART_POSITIONS[i][0];
            int sy = topPos + ResearchDeskMenu.PART_POSITIONS[i][1];
            ItemStack inSlot = menu.getWorkbench().getItem(ResearchDeskMenu.FIRST_PART_SLOT + i);

            if (inSlot.isEmpty())
            {
                drawGhost(graphics, cost.icon(), sx, sy);
                if (mouseX >= sx && mouseX < sx + 16 && mouseY >= sy && mouseY < sy + 16)
                {
                    graphics.setTooltipForNextFrame(cost.icon().getHoverName(), mouseX, mouseY);
                }
            }

            boolean enough = inSlot.getCount() >= cost.count(); // slots only take the right part
            graphics.text(font, "x" + cost.count(), sx + 3, sy + 19, enough ? BP_HAVE : BP_DIM, false);
        }

        // Arrow toward the output
        int ax = leftPos + 112;
        int ay = topPos + 58;
        graphics.fill(ax, ay - 1, ax + 11, ay + 1, BP_TEXT);
        graphics.fill(ax + 9, ay - 3, ax + 10, ay + 3, BP_TEXT);
        graphics.fill(ax + 10, ay - 2, ax + 11, ay + 2, BP_TEXT);
        graphics.fill(ax + 11, ay - 1, ax + 12, ay + 1, BP_TEXT);

        // Ghost of the result
        if (menu.getWorkbench().getItem(ResearchDeskMenu.OUTPUT_SLOT).isEmpty())
        {
            drawGhost(graphics, bp.result().get(), leftPos + ResearchDeskMenu.OUTPUT_X, topPos + ResearchDeskMenu.OUTPUT_Y);
        }
    }

    // Faded item: draw it, then tint it with the paper color (same trick vanilla's recipe book uses)
    private void drawGhost(GuiGraphicsExtractor graphics, ItemStack stack, int x, int y)
    {
        graphics.fakeItem(stack, x, y);
        graphics.fill(x, y, x + 16, y + 16, 0xA014376E);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        graphics.blit(RenderPipelines.GUI_TEXTURED, activeTab.background, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        if (activeTab == Tab.COMPUTER)
        {
            if (infoPageNode != null)
            {
                drawInfoPage(graphics, mouseX, mouseY);
            }
            else
            {
                drawResearchTree(graphics, mouseX, mouseY);
                drawInfoStrip(graphics);
            }
        }
        if (activeTab == Tab.DESK)
        {
            drawDeskSlots(graphics);
            drawDeskContents(graphics, mouseX, mouseY);
        }
        updateDeskButtons();
    }

    // ---------- Tree ----------

    private void drawResearchTree(GuiGraphicsExtractor graphics, int mouseX, int mouseY)
    {
        int left = leftPos + TREE_X;
        int top = topPos + TREE_Y;
        int right = left + TREE_W;
        int bottom = top + TREE_H;

        graphics.fill(left, top, right, bottom, 0xFF0A1A14);
        graphics.enableScissor(left, top, right, bottom);

        int originX = left + (int) panX;
        int originY = top + (int) panY;

        // Connectors. If a node is visible, all of its prerequisites are too.
        for (ResearchNode node : ResearchTree.all())
        {
            NodeState state = stateOf(node);
            if (state == NodeState.HIDDEN) continue;

            int color = state == NodeState.PREVIEW ? 0xFF1F3329 : 0xFF55FFAA;
            for (String preId : node.prerequisites())
            {
                ResearchNode parent = ResearchTree.get(preId);
                if (parent == null) continue;

                drawConnector(graphics,
                        originX + parent.x() + NODE_W, originY + parent.y() + NODE_H / 2,
                        originX + node.x(), originY + node.y() + NODE_H / 2,
                        color);
            }
        }

        ResearchNode hovered = nodeAt(mouseX, mouseY);

        for (ResearchNode node : ResearchTree.all())
        {
            NodeState state = stateOf(node);
            if (state == NodeState.HIDDEN) continue;

            int nx = originX + node.x();
            int ny = originY + node.y();

            // Border always shows the node's state
            int border;
            if (state == NodeState.UNLOCKED) border = 0xFF55FFAA;
            else if (state == NodeState.AVAILABLE) border = 0xFFFFD755;
            else border = 0xFF2A3F35;

            int bg;
            if (state == NodeState.PREVIEW) bg = 0xFF0E1E18;
            else if (node == hovered) bg = 0xFF1E3A30;
            else bg = 0xFF12261E;

            int textColor = state == NodeState.PREVIEW ? 0xFF4A6A5A : 0xFFFFFFFF;

            // Selection: thin white frame outside the colored border
            if (node == selectedNode)
            {
                graphics.fill(nx - 2, ny - 2, nx + NODE_W + 2, ny + NODE_H + 2, 0xFFFFFFFF);
            }
            graphics.fill(nx - 1, ny - 1, nx + NODE_W + 1, ny + NODE_H + 1, border);
            graphics.fill(nx, ny, nx + NODE_W, ny + NODE_H, bg);

            List<FormattedCharSequence> lines = font.split(Component.literal(node.title()), NODE_W - 6);
            if (!lines.isEmpty())
            {
                graphics.text(font, lines.get(0), nx + 3, ny + 5, textColor);
            }
        }

        graphics.disableScissor();
    }

    private void drawInfoStrip(GuiGraphicsExtractor graphics)
    {
        int left = leftPos + TREE_X;
        int right = left + TREE_W;
        int top = topPos + TREE_Y + TREE_H;
        int bottom = top + INFO_H;

        graphics.fill(left, top, right, top + 1, 0xFF3A5A4A);
        graphics.fill(left, top + 1, right, bottom, 0xFF081410);

        if (selectedNode == null)
        {
            graphics.text(font, "Select a research node", left + 4, top + 8, 0xFF6A8A7A);
            return;
        }

        ResearchNode node = selectedNode;
        NodeState state = stateOf(node);

        String status;
        int statusColor;
        switch (state)
        {
            case UNLOCKED -> { status = "Researched"; statusColor = 0xFF55FFAA; }
            case AVAILABLE -> { status = "Click to research"; statusColor = 0xFFFFD755; }
            default -> { status = "Locked"; statusColor = 0xFFFF5555; }
        }

        graphics.text(font, node.title(), left + 4, top + 3, 0xFFFFFFFF);
        graphics.text(font, status, right - 4 - font.width(status), top + 3, statusColor);

        String secondLine;
        if (state == NodeState.PREVIEW)
        {
            List<String> missing = new ArrayList<>();
            for (String preId : node.prerequisites())
            {
                ResearchNode pre = ResearchTree.get(preId);
                if (pre != null && !unlockedResearch().contains(preId)) missing.add(pre.title());
            }
            secondLine = "Requires: " + String.join(", ", missing);
        }
        else
        {
            secondLine = node.description();
        }

        List<FormattedCharSequence> lines = font.split(Component.literal(secondLine), right - left - 8);
        if (!lines.isEmpty())
        {
            graphics.text(font, lines.get(0), left + 4, top + 13, 0xFFAAAAAA);
        }
    }

    private void drawConnector(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int color)
    {
        int midX = (x1 + x2) / 2;
        hLine(graphics, x1, midX, y1, color);
        vLine(graphics, midX, y1, y2, color);
        hLine(graphics, midX, x2, y2, color);
    }

    private static void hLine(GuiGraphicsExtractor graphics, int xa, int xb, int y, int color)
    {
        graphics.fill(Math.min(xa, xb), y, Math.max(xa, xb) + 1, y + 1, color);
    }

    private static void vLine(GuiGraphicsExtractor graphics, int x, int ya, int yb, int color)
    {
        graphics.fill(x, Math.min(ya, yb), x + 1, Math.max(ya, yb) + 1, color);
    }

    // ---------- Info page ----------

    private void drawInfoPage(GuiGraphicsExtractor graphics, int mouseX, int mouseY)
    {
        ResearchNode node = infoPageNode;
        int left = leftPos + TREE_X;
        int top = topPos + TREE_Y;
        int right = left + TREE_W;
        int bottom = top + TREE_H + INFO_H;

        graphics.fill(left, top, right, bottom, 0xFF081410);

        graphics.text(font, node.title(), left + 4, top + 4, 0xFF55FFAA);
        int backColor = isOverBack(mouseX, mouseY) ? 0xFFFFFFFF : 0xFFAAAAAA;
        graphics.text(font, BACK_LABEL, right - 4 - font.width(BACK_LABEL), top + 4, backColor);
        graphics.fill(left + 4, top + 15, right - 4, top + 16, 0xFF3A5A4A);

        int lineHeight = 10;
        int wrapWidth = TREE_W - 14;
        int contentTop = top + 19;
        // Height rounded down to whole lines, so a line is never cut in half at the edge
        int contentBottom = contentTop + ((bottom - 4 - contentTop) / lineHeight) * lineHeight;

        // Split on \n ourselves so paragraphs work, then wrap each paragraph to the width
        List<FormattedCharSequence> lines = new ArrayList<>();
        for (String paragraph : node.infoText().split("\n"))
        {
            lines.addAll(font.split(Component.literal(paragraph), wrapWidth));
        }

        int contentHeight = lines.size() * lineHeight;
        int viewHeight = contentBottom - contentTop;
        infoMaxScroll = Math.max(0, contentHeight - viewHeight);
        infoScroll = Math.max(0, Math.min(infoScroll, infoMaxScroll));

        graphics.enableScissor(left, contentTop, right, contentBottom);
        int y = contentTop - infoScroll;
        for (FormattedCharSequence line : lines)
        {
            graphics.text(font, line, left + 4, y, 0xFFDDDDDD);
            y += lineHeight;
        }
        graphics.disableScissor();

        if (infoMaxScroll > 0)
        {
            int trackX = right - 4;
            int thumbHeight = Math.max(10, viewHeight * viewHeight / Math.max(1, contentHeight));
            int thumbY = contentTop + (viewHeight - thumbHeight) * infoScroll / Math.max(1, infoMaxScroll);
            graphics.fill(trackX, contentTop, trackX + 2, contentBottom, 0xFF222222);
            graphics.fill(trackX, thumbY, trackX + 2, thumbY + thumbHeight, 0xFFAAAAAA);
        }
    }

    private boolean isOverBack(double mx, double my)
    {
        int right = leftPos + TREE_X + TREE_W;
        int top = topPos + TREE_Y;
        int backLeft = right - 4 - font.width(BACK_LABEL);
        return mx >= backLeft - 2 && mx < right - 2 && my >= top + 2 && my < top + 14;
    }

    private void openInfoPage(ResearchNode node)
    {
        infoPageNode = node;
        infoScroll = 0;
        panning = false;
    }

    // ---------- Input ----------

    private boolean isInTree(double mx, double my)
    {
        int left = leftPos + TREE_X;
        int top = topPos + TREE_Y;
        return mx >= left && mx < left + TREE_W && my >= top && my < top + TREE_H;
    }

    private boolean isInComputerArea(double mx, double my)
    {
        int left = leftPos + TREE_X;
        int top = topPos + TREE_Y;
        return mx >= left && mx < left + TREE_W && my >= top && my < top + TREE_H + INFO_H;
    }

    private @Nullable ResearchNode nodeAt(double mx, double my)
    {
        if (!isInTree(mx, my)) return null;

        int originX = leftPos + TREE_X + (int) panX;
        int originY = topPos + TREE_Y + (int) panY;

        for (ResearchNode node : ResearchTree.all())
        {
            if (stateOf(node) == NodeState.HIDDEN) continue;

            int nx = originX + node.x();
            int ny = originY + node.y();
            if (mx >= nx && mx < nx + NODE_W && my >= ny && my < ny + NODE_H)
            {
                return node;
            }
        }
        return null;
    }

    private void handleNodeClick(ResearchNode node)
    {
        switch (stateOf(node))
        {
            case UNLOCKED ->
            {
                selectedNode = node;
                if (node.hasInfoPage()) openInfoPage(node);
            }
            case AVAILABLE ->
            {
                if (node == selectedNode) ClientPacketDistributor.sendToServer(new UnlockResearchPayload(node.id()));
                else selectedNode = node;
            }
            case PREVIEW -> selectedNode = node;
            default -> {}
        }
    }

    // Only lets you pan as far as the visible nodes go, so hidden ones can't be found by dragging
    private void clampPan()
    {
        int maxX = 0;
        int maxY = 0;
        for (ResearchNode node : ResearchTree.all())
        {
            if (stateOf(node) == NodeState.HIDDEN) continue;
            maxX = Math.max(maxX, node.x() + NODE_W);
            maxY = Math.max(maxY, node.y() + NODE_H);
        }
        int margin = 10;
        double minPanX = Math.min(margin, TREE_W - maxX - margin);
        double minPanY = Math.min(margin, TREE_H - maxY - margin);

        panX = Math.max(minPanX, Math.min(margin, panX));
        panY = Math.max(minPanY, Math.min(margin, panY));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
    {
        if (activeTab == Tab.DESK && event.button() == 1 && blueprintSlotItem().is(Items.PAPER))
        {
            Blueprint clicked = draftTileAt(event.x(), event.y());
            if (clicked != null)
            {
                selectedDraft = clicked;
                return true;
            }
        }

        if (activeTab == Tab.COMPUTER && event.button() == 1)
        {
            if (infoPageNode != null)
            {
                if (isOverBack(event.x(), event.y()))
                {
                    infoPageNode = null;
                    return true;
                }
                if (isInComputerArea(event.x(), event.y())) return true;
            }
            else if (isInTree(event.x(), event.y()))
            {
                ResearchNode clicked = nodeAt(event.x(), event.y());
                if (clicked != null) handleNodeClick(clicked);
                else panning = true;
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy)
    {
        if (panning)
        {
            panX += dx;
            panY += dy;
            clampPan();
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event)
    {
        if (panning)
        {
            panning = false;
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY)
    {
        if (activeTab == Tab.COMPUTER && infoPageNode != null && isInComputerArea(x, y))
        {
            infoScroll -= (int) (scrollY * 10);
            infoScroll = Math.max(0, Math.min(infoScroll, infoMaxScroll));
            return true;
        }
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }
}
