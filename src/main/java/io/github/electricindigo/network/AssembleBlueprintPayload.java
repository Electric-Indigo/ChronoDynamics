package io.github.electricindigo.network;

import io.github.electricindigo.ChronoDynamics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record AssembleBlueprintPayload() implements CustomPacketPayload
{
    public static final AssembleBlueprintPayload INSTANCE = new AssembleBlueprintPayload();

    public static final Type<AssembleBlueprintPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, "assemble_blueprint"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AssembleBlueprintPayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
