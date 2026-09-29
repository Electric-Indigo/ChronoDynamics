package io.github.electricindigo.client;

import io.github.electricindigo.ModClientConfig;
import io.github.electricindigo.distortion.SicknessStage;
import io.github.electricindigo.registry.ModAttachments;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class TimeDistortion
{
    private TimeDistortion(){}

    private static final int DAY = 24000;

    private static final int MILD_SPEED = 10;
    private static final int STRONG_SPEED = 20;

    private static final int STRONG_FORWARD_TICKS = 100;
    private static final int STRONG_BACK_TICKS = 200;

    private static final double EASE_BACK_SPEED = 40;

    private static final double REDUCED = 0.25;

    private static double offset = 0;
    private static int cycleTick = 0;

    public static long offsetTicks()
    {
        return (long) offset;
    }

    public static void onClientTick(ClientTickEvent.Post event)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null)
        {

            offset = 0;
            cycleTick = 0;
            return;
        }

        if (ModClientConfig.DISABLE_TIME_DISTORTION.get())
        {
            offset = 0; // sky and clouds go straight back to real time
            cycleTick = 0;
            return;
        }

        if (mc.isPaused()) return;

        SicknessStage stage = mc.player.getData(ModAttachments.TEMPORAL_SICKNESS).stage();
        double scale = ModClientConfig.REDUCE_FLICKER.get() ? REDUCED : 1.0;

        switch (stage)
        {
            case NONE ->
            {
                easeBack();
                cycleTick = 0;
            }
            case MILD ->
            {
                offset += MILD_SPEED * scale;
                cycleTick = 0;
            }
            case STRONG ->
            {
                boolean forward = cycleTick < STRONG_FORWARD_TICKS;
                offset += (forward ? STRONG_SPEED : -STRONG_SPEED) * scale;
                cycleTick = (cycleTick + 1) % (STRONG_FORWARD_TICKS + STRONG_BACK_TICKS);
            }
        }
    }

    private static void easeBack()
    {
        double target = Math.round(offset / DAY) * (double) DAY;
        double distance = target - offset;

        if (Math.abs(distance) <= EASE_BACK_SPEED) offset = target;
        else offset += Math.signum(distance) * EASE_BACK_SPEED;
    }
}
