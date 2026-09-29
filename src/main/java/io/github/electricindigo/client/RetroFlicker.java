package io.github.electricindigo.client;

import io.github.electricindigo.ModClientConfig;
import io.github.electricindigo.distortion.SicknessStage;
import io.github.electricindigo.registry.ModAttachments;
import net.minecraft.client.Minecraft;

import java.util.Random;

public final class RetroFlicker
{
    private RetroFlicker() {}

    private static final Random RANDOM = new Random();

    private static SicknessStage lastStage = SicknessStage.NONE;
    private static long nextFlickerAt = 0;
    private static long flickerEndsAt = 0;

    public static boolean showRetro()
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return false;

        SicknessStage stage = mc.player.getData(ModAttachments.TEMPORAL_SICKNESS).stage();
        if (stage == SicknessStage.NONE)
        {
            lastStage = SicknessStage.NONE;
            return false;
        }

        // Accessibility: no flashing, just stay retro while sick
        if (ModClientConfig.REDUCE_FLICKER.get()) return true;

        long now = mc.level.getGameTime();

        // Stage changed: start a fresh wait so it doesn't flicker instantly
        if (stage != lastStage)
        {
            lastStage = stage;
            flickerEndsAt = 0;
            nextFlickerAt = now + randomGap(stage);
        }

        if (now >= nextFlickerAt)
        {
            flickerEndsAt = now + duration(stage);
            nextFlickerAt = flickerEndsAt + randomGap(stage);
        }

        return now < flickerEndsAt;
    }

    // Ticks between flickers (20 ticks = 1 second)
    private static int randomGap(SicknessStage stage)
    {
        return stage == SicknessStage.STRONG
                ? 40 + RANDOM.nextInt(61)    // 2-5 seconds
                : 100 + RANDOM.nextInt(101); // 5-10 seconds
    }

    // How long each flicker lasts, in ticks
    private static int duration(SicknessStage stage)
    {
        return stage == SicknessStage.STRONG ? 7 : 3;
    }
}
