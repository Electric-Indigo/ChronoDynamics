package io.github.electricindigo;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ModClientConfig
{
    private ModClientConfig() {}

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue REDUCE_FLICKER = BUILDER
            .comment("Replace the rift flicker and time-lapse sky with steady, non-flashing effects.")
            .define("reduceRiftFlicker", false);

    public static final ModConfigSpec.BooleanValue DISABLE_TIME_DISTORTION = BUILDER
            .comment("Turn off the time-lapse sky and clouds entirely. Handy while developing.")
            .define("disableTimeDistortion", false);

    public static final ModConfigSpec.IntValue RETRO_BUBBLE_RADIUS = BUILDER
            .comment("How many blocks around you turn retro at Strong sickness. 0 turns the bubble off.")
            .defineInRange("retroBubbleRadius", 12, 0, 32);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
