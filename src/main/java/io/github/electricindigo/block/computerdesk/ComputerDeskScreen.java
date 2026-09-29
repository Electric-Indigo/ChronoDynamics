package io.github.electricindigo.block.computerdesk;

import io.github.electricindigo.ChronoDynamics;
import io.github.electricindigo.block.researchdesk.ResearchDeskMenu;
import io.github.electricindigo.block.researchdesk.TabButton;
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

public class ComputerDeskScreen extends AbstractContainerScreen<ComputerDeskMenu>
{
    private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, "textures/gui/computer_gui.png");

    private static final int TREE_X = 19;
    private static final int TREE_Y = 8;
    private static final int TREE_W = 192;
    private static final int TREE_H = 71;
    private static final int INFO_H = 24;

    private static final int NODE_W = 72;
    private static final int NODE_H = 18;

    private static final String BACK_LABEL = "< Back";

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

    public ComputerDeskScreen(ComputerDeskMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, 230, 219);
    }

    private NodeState stateOf(ResearchNode node)
    {
        return ResearchTree.stateOf(node, unlockedResearch());
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

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
        if (event.button() == 1)
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
        if (infoPageNode != null && isInComputerArea(x, y))
        {
            infoScroll -= (int) (scrollY * 10);
            infoScroll = Math.max(0, Math.min(infoScroll, infoMaxScroll));
            return true;
        }
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }
}
