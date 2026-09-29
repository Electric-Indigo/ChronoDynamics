package io.github.electricindigo.distortion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TemporalSickness(int value)
{
    public static final int MAX = 100;
    public static final TemporalSickness NONE = new TemporalSickness(0);

    public static final MapCodec<TemporalSickness> MAP_CODEC = Codec.INT.fieldOf("value")
            .xmap(TemporalSickness::new, TemporalSickness::value);

    public static final StreamCodec<ByteBuf, TemporalSickness> STREAM_CODEC =
            ByteBufCodecs.VAR_INT.map(TemporalSickness::new, TemporalSickness::value);

    public TemporalSickness
    {
        value = Math.max(0, Math.min(MAX, value));
    }

    public SicknessStage stage()
    {
        return SicknessStage.of(value);
    }
}
