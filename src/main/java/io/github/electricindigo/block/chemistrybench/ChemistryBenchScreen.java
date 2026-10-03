package io.github.electricindigo.block.chemistrybench;

import io.github.electricindigo.ChronoDynamics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.fluids.FluidStack;

public class ChemistryBenchScreen extends AbstractContainerScreen<ChemistryBenchMenu>
{
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, "textures/gui/chemistry_bench.png");
    private static final Identifier SPRITES =
            Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, "textures/gui/chemistry_bench_sprites.png");

    private static final int SPRITES_W = 64, SPRITES_H = 128;

    // Where things are on the GUI
    private static final int FLAME_X = 74, FLAME_Y = 77, FLAME_W = 6, FLAME_H = 9;
    private static final int ARROW_X = 116, ARROW_Y = 48, ARROW_W = 15, ARROW_H = 10;
    private static final int BUBBLES_X = 57, BUBBLES_Y = 28, BUBBLES_W = 45, BUBBLES_H = 13;
    private static final int TUBE_X = 42, TUBE_W = 3, TUBE_TOP = 16, TUBE_BOTTOM = 88;
    private static final int MAX_DISPLAY_TEMP = 600;

    private static final int SOLVENT_X = 18, SOLVENT_Y = 16, SOLVENT_W = 14, SOLVENT_H = 68;
    private static final int PRODUCT_X = 174, PRODUCT_Y = 16, PRODUCT_W = 26, PRODUCT_H = 68;

    private static final int SOLVENT_OVERLAY_U = 0, PRODUCT_OVERLAY_U = 14;
    private static final int FLAME_U = 40, FLAME_V = 0;
    private static final int ARROW_U = 40, ARROW_V = 9;
    private static final int BUBBLES_U = 0, BUBBLES_V = 68;

    public ChemistryBenchScreen(ChemistryBenchMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, 230, 219);

        titleLabelX = 8;
        titleLabelY = 5;
        inventoryLabelX = 34;
        inventoryLabelY = 125;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int x = leftPos;
        int y = topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        drawTank(graphics, menu.getSolvent(), SOLVENT_X, SOLVENT_Y, SOLVENT_W, SOLVENT_H, SOLVENT_OVERLAY_U, mouseX, mouseY);
        drawTank(graphics, menu.getProduct(), PRODUCT_X, PRODUCT_Y, PRODUCT_W, PRODUCT_H, PRODUCT_OVERLAY_U, mouseX, mouseY);

        if (menu.isBurning())
        {
            int h = Mth.ceil(menu.getBurnLeft() * FLAME_H);
            int cut = FLAME_H - h;
            graphics.blit(RenderPipelines.GUI_TEXTURED, SPRITES, x + FLAME_X, y + FLAME_Y + cut,
                    FLAME_U, FLAME_V + cut, FLAME_W, h, SPRITES_W, SPRITES_H);
        }

        int arrowW = Mth.ceil(menu.getCraftProgress() * ARROW_W);
        if (arrowW > 0)
        {
            graphics.blit(RenderPipelines.GUI_TEXTURED, SPRITES, x + ARROW_X, y + ARROW_Y,
                    ARROW_U, ARROW_V, arrowW, ARROW_H, SPRITES_W, SPRITES_H);
        }

        if (menu.getCraftProgress() > 0 && minecraft.level != null)
        {
            int frame = (int) ((minecraft.level.getGameTime() / 4) % 4);
            graphics.blit(RenderPipelines.GUI_TEXTURED, SPRITES, x + BUBBLES_X, y + BUBBLES_Y,
                    BUBBLES_U, BUBBLES_V + frame * BUBBLES_H, BUBBLES_W, BUBBLES_H, SPRITES_W, SPRITES_H);
        }

        drawThermometer(graphics, x, y, mouseX, mouseY);
    }

    private void drawTank(GuiGraphicsExtractor graphics, FluidStack fluid, int tx, int ty, int tw, int th,
                          int overlayU, int mouseX, int mouseY)
    {
        int left = leftPos + tx;
        int top = topPos + ty;
        int bottom = top + th;
        int capacity = ChemistryBenchBlockEntity.TANK_CAPACITY;

        if (!fluid.isEmpty())
        {
            int fillH = Mth.clamp(Mth.ceil((float) fluid.getAmount() / capacity * th), 1, th);

            FluidModel model = Minecraft.getInstance().getModelManager()
                    .getFluidStateModelSet().get(fluid.getFluid().defaultFluidState());
            TextureAtlasSprite sprite = model.stillMaterial().sprite();
            FluidTintSource tint = model.fluidTintSource();
            int color = tint != null ? tint.colorAsStack(fluid) | 0xFF000000 : 0xFFFFFFFF;

            // Stack 16x16 tiles up from the bottom, cropped to the fill level
            graphics.enableScissor(left, bottom - fillH, left + tw, bottom);
            for (int tileY = bottom - 16; tileY > bottom - fillH - 16; tileY -= 16)
            {
                for (int tileX = left; tileX < left + tw; tileX += 16)
                {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, tileX, tileY, 16, 16, color);
                }
            }
            graphics.disableScissor();
        }

        // Tick marks and glass shine go on top
        graphics.blit(RenderPipelines.GUI_TEXTURED, SPRITES, left, top, overlayU, 0, tw, th, SPRITES_W, SPRITES_H);

        if (isHovering(tx, ty, tw, th, mouseX, mouseY))
        {
            Component text = fluid.isEmpty()
                    ? Component.literal("Empty")
                    : Component.empty().append(fluid.getHoverName())
                    .append(": " + fluid.getAmount() + " / " + capacity + " mB");
            graphics.setTooltipForNextFrame(text, mouseX, mouseY);
        }
    }

    private void drawThermometer(GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY)
    {
        int temp = menu.getTemperature();
        int color = colorFor(temp);
        int tubeH = TUBE_BOTTOM - TUBE_TOP;

        // Tube fills from the bottom up
        int fillH = Mth.clamp(temp * tubeH / MAX_DISPLAY_TEMP, 0, tubeH);
        graphics.fill(x + TUBE_X, y + TUBE_BOTTOM - fillH, x + TUBE_X + TUBE_W, y + TUBE_BOTTOM, color);
        graphics.fill(x + 41, y + 88, x + 46, y + 93, color);

        // White lines for the current recipe's heat window
        int minHeat = menu.getMinHeat();
        int maxHeat = menu.getMaxHeat();
        if (maxHeat > 0)
        {
            for (int heat : new int[] {minHeat, maxHeat})
            {
                int lineY = y + TUBE_BOTTOM - Mth.clamp(heat * tubeH / MAX_DISPLAY_TEMP, 0, tubeH);
                graphics.fill(x + TUBE_X - 2, lineY, x + TUBE_X + TUBE_W + 2, lineY + 1, 0xFFFFFFFF);
            }
        }

        if (isHovering(39, 14, 10, 82, mouseX, mouseY))
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
