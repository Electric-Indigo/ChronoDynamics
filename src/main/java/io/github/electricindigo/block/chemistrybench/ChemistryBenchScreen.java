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

    private static final Identifier FLAME_SPRITE = Identifier.withDefaultNamespace("container/furnace/lit_progress");
    private static final Identifier ARROW_SPRITE = Identifier.withDefaultNamespace("container/furnace/burn_progress");

    private static final int THERMO_X = 152;
    private static final int THERMO_Y = 17;
    private static final int THERMO_W = 8;
    private static final int THERMO_H = 52;
    private static final int MAX_DISPLAY_TEMP = 1000;

    public ChemistryBenchScreen(ChemistryBenchMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int x = leftPos;
        int y = topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        // Flame shrinks as the fuel burns down (same trick as the vanilla furnace)
        if (menu.isBurning())
        {
            int h = Mth.ceil(menu.getBurnLeft() * 13.0F) + 1;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, FLAME_SPRITE, 14, 14, 0, 14 - h,
                    x + 49, y + 36 + 14 - h, 14, h);
        }

        int arrowW = Mth.ceil(menu.getCraftProgress() * 24.0F);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ARROW_SPRITE, 24, 16, 0, 0, x + 94, y + 34, arrowW, 16);

        // Thermometer: dark tube, then a colored fill from the bottom up
        int temp = menu.getTemperature();
        int left = x + THERMO_X;
        int top = y + THERMO_Y;
        int bottom = top + THERMO_H;
        graphics.fill(left, top, left + THERMO_W, bottom, 0xFF2B2B2B);

        int fillH = Mth.clamp(temp * THERMO_H / MAX_DISPLAY_TEMP, 0, THERMO_H);
        graphics.fill(left + 1, bottom - fillH, left + THERMO_W - 1, bottom, colorFor(temp));

        int minHeat = menu.getMinHeat();
        int maxHeat = menu.getMaxHeat();
        if (maxHeat > 0)
        {
            int minY = bottom - Mth.clamp(minHeat * THERMO_H / MAX_DISPLAY_TEMP, 0, THERMO_H);
            int maxY = bottom - Mth.clamp(maxHeat * THERMO_H / MAX_DISPLAY_TEMP, 0, THERMO_H);
            graphics.fill(left - 1, minY, left + THERMO_W + 1, minY + 1, 0xFFFFFFFF);
            graphics.fill(left - 1, maxY, left + THERMO_W + 1, maxY + 1, 0xFFFFFFFF);
        }

        if (isHovering(THERMO_X, THERMO_Y, THERMO_W, THERMO_H, mouseX, mouseY))
        {
            String text = temp + "°";
            if (maxHeat > 0) text += "  (needs " + minHeat + "–" + maxHeat + "°)";
            graphics.setTooltipForNextFrame(Component.literal(text), mouseX, mouseY);
        }
    }

    private static int colorFor(int temp)
    {
        if (temp < 100) return 0xFF4A90E2;
        if (temp < 300) return 0xFFF2C94C;
        if (temp < 600) return 0xFFF2994A;
        return 0xFFEB5757;
    }
}
