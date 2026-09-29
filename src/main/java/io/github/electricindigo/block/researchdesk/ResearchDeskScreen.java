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
    private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, "textures/gui/desk_gui.png");

    // Drawer look
    private static final int DRAWER_WOOD = 0xFF6B4A2E;
    private static final int DRAWER_EDGE = 0xFF3B2414;
    private static final int DRAWER_SLOT = 0xFF9C7B5E;
    private static final int DRAWER_SLOT_DARK = 0xFF4A2F1C;
    private static final int DRAWER_SLOT_LIGHT = 0xFFD8B894;
    private static final float DRAWER_SLIDE_MS = 150f; // how long the slide takes

    private boolean drawerWanted = false;   // what the player asked for
    private float drawerProgress = 0f;      // 0 = shut, 1 = fully out
    private long lastFrameMs = System.currentTimeMillis();
    private Button drawerButton;

    public ResearchDeskScreen(ResearchDeskMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, 230, 219);
    }

    @Override
    protected void init()
    {
        super.init();

        draftButton = addRenderableWidget(Button.builder(Component.literal("Draft"), b -> draftSelected())
                .bounds(leftPos + 148, topPos + 94, 64, 20)
                .build());

        assembleButton = addRenderableWidget(Button.builder(Component.literal("Assemble"),
                        b -> ClientPacketDistributor.sendToServer(AssembleBlueprintPayload.INSTANCE))
                .bounds(leftPos + 148, topPos + 94, 64, 20)
                .build());

        drawerButton = addRenderableWidget(Button.builder(Component.literal(drawerWanted ? ">" : "<"), b -> toggleDrawer())
                .bounds(leftPos + 12, topPos + 140, 16, 16)
                .build());
    }

    // ---------- Drawer ----------

    private void toggleDrawer()
    {
        drawerWanted = !drawerWanted;
        drawerButton.setMessage(Component.literal(drawerWanted ? ">" : "<"));
        if (!drawerWanted)
        {
            menu.setDrawerOpen(false); // hide the slots right away when closing
        }
    }

    private void updateDrawerSlide()
    {
        long now = System.currentTimeMillis();
        float step = (now - lastFrameMs) / DRAWER_SLIDE_MS;
        lastFrameMs = now;

        drawerProgress += drawerWanted ? step : -step;
        drawerProgress = Math.max(0f, Math.min(1f, drawerProgress));

        // Slots only work once it's all the way out
        menu.setDrawerOpen(drawerWanted && drawerProgress >= 1f);
    }

    private void drawDrawer(GuiGraphicsExtractor graphics)
    {
        if (drawerProgress <= 0f) return;

        int w = ResearchDeskMenu.DRAWER_WIDTH;
        int h = ResearchDeskMenu.DRAWER_HEIGHT;
        int top = topPos + ResearchDeskMenu.DRAWER_TOP;
        int left = leftPos - Math.round(w * drawerProgress); // slides out from behind the desk

        // Only draw the part that has come out from behind the desk
        graphics.enableScissor(leftPos - w, top, leftPos, top + h);

        graphics.fill(left, top, left + w, top + h, DRAWER_EDGE);
        graphics.fill(left + 1, top + 1, left + w, top + h - 1, DRAWER_WOOD);

        // Slot backgrounds. The slots' own positions are fixed, so draw these where the panel is right now.
        int slideOffset = w - Math.round(w * drawerProgress);
        for (int i = 0; i < ResearchDeskBlockEntity.DRAWER_SIZE; i++)
        {
            var slot = menu.slots.get(ResearchDeskMenu.DRAWER_START + i);
            int sx = leftPos + slot.x - 1 + slideOffset;
            int sy = topPos + slot.y - 1;
            graphics.fill(sx, sy, sx + 18, sy + 18, DRAWER_SLOT_DARK);
            graphics.fill(sx + 1, sy + 1, sx + 18, sy + 18, DRAWER_SLOT_LIGHT);
            graphics.fill(sx + 1, sy + 1, sx + 17, sy + 17, DRAWER_SLOT);
        }

        graphics.disableScissor();
    }

    // ---------- Desk ----------

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

        boolean drafting = blueprintSlotItem().is(Items.PAPER) && selectedDraft != null;
        draftButton.visible = drafting;
        draftButton.active = drafting && player != null && BlueprintManager.canDraft(player, selectedDraft);

        boolean assembling = menu.getReadableBlueprint() != null;
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
        updateDrawerSlide();
        drawDrawer(graphics);

        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        drawDeskSlots(graphics);
        drawDeskContents(graphics, mouseX, mouseY);
        updateDeskButtons();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
    {
        if (event.button() == 1 && blueprintSlotItem().is(Items.PAPER))
        {
            Blueprint clicked = draftTileAt(event.x(), event.y());
            if (clicked != null)
            {
                selectedDraft = clicked;
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }
}
