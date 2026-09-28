package io.github.electricindigo.block.chemistrybench;

import io.github.electricindigo.ChronoDynamics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class ChemistryBenchScreen extends AbstractContainerScreen<ChemistryBenchMenu>
{
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, "textures/gui/chemistry_bench.png");

    // Where things are on the GUI
    private static final int FLAME_X = 71, FLAME_Y = 75, FLAME_W = 6, FLAME_H = 9;
    private static final int ARROW_X = 104, ARROW_Y = 42, ARROW_W = 17, ARROW_H = 11;
    private static final int BUBBLES_X = 53, BUBBLES_Y = 23, BUBBLES_W = 45, BUBBLES_H = 13;
    private static final int TUBE_X = 162, TUBE_TOP = 21, TUBE_BOTTOM = 69, TUBE_W = 6;
    private static final int MAX_DISPLAY_TEMP = 600;

    // Where the lit flame, arrow and bubble frames are stored in the texture
    private static final int SPRITE_U = 176;
    private static final int FLAME_V = 0, ARROW_V = 9, BUBBLES_V = 20;

    public ChemistryBenchScreen(ChemistryBenchMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, 176, 186);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int x = leftPos;
        int y = topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        // Flame shrinks from the top as the fuel burns down
        if (menu.isBurning())
        {
            int h = Mth.ceil(menu.getBurnLeft() * FLAME_H);
            int cut = FLAME_H - h;
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + FLAME_X, y + FLAME_Y + cut,
                    SPRITE_U, FLAME_V + cut, FLAME_W, h, 256, 256);
        }

        // Arrow fills left to right with craft progress
        int arrowW = Mth.ceil(menu.getCraftProgress() * ARROW_W);
        if (arrowW > 0)
        {
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + ARROW_X, y + ARROW_Y,
                    SPRITE_U, ARROW_V, arrowW, ARROW_H, 256, 256);
        }

        // Bubbles while a craft is in progress, new frame every 4 ticks
        if (menu.getCraftProgress() > 0 && minecraft.level != null)
        {
            int frame = (int) ((minecraft.level.getGameTime() / 4) % 4);
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x + BUBBLES_X, y + BUBBLES_Y,
                    SPRITE_U, BUBBLES_V + frame * BUBBLES_H, BUBBLES_W, BUBBLES_H, 256, 256);
        }

        drawThermometer(graphics, x, y, mouseX, mouseY);
    }

    private void drawThermometer(GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY)
    {
        int temp = menu.getTemperature();
        int color = colorFor(temp);
        int tubeH = TUBE_BOTTOM - TUBE_TOP;

        // Tube fills from the bottom up
        int fillH = Mth.clamp(temp * tubeH / MAX_DISPLAY_TEMP, 0, tubeH);
        graphics.fill(x + TUBE_X, y + TUBE_BOTTOM - fillH, x + TUBE_X + TUBE_W, y + TUBE_BOTTOM, color);

        // Bulb is always filled, matching your bulb shape
        graphics.fill(x + 161, y + 69, x + 169, y + 71, color);
        graphics.fill(x + 160, y + 71, x + 170, y + 75, color);
        graphics.fill(x + 161, y + 75, x + 169, y + 77, color);
        graphics.fill(x + 163, y + 77, x + 167, y + 78, color);

        // White lines for the current recipe's heat window
        int minHeat = menu.getMinHeat();
        int maxHeat = menu.getMaxHeat();
        if (maxHeat > 0)
        {
            for (int heat : new int[] {minHeat, maxHeat})
            {
                int lineY = y + TUBE_BOTTOM - Mth.clamp(heat * tubeH / MAX_DISPLAY_TEMP, 0, tubeH);
                graphics.fill(x + 159, lineY, x + 171, lineY + 1, 0xFFFFFFFF);
            }
        }

        if (isHovering(159, 19, 12, 60, mouseX, mouseY))
        {
            String text = temp + "°";
            if (maxHeat > 0) text += "  (needs " + minHeat + "–" + maxHeat + "°)";
            graphics.setTooltipForNextFrame(Component.literal(text), mouseX, mouseY);
        }
    }

    // Blue when cool, then yellow, orange, and red as it heats up
    private static int colorFor(int temp)
    {
        if (temp < 100) return 0xFF4A90E2;
        if (temp < 300) return 0xFFF2C94C;
        if (temp < 600) return 0xFFF2994A;
        return 0xFFEB5757;
    }
}
