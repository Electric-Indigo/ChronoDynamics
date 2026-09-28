package io.github.electricindigo.network;

import io.github.electricindigo.ChronoDynamics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record DraftBlueprintPayload(String blueprintId) implements CustomPacketPayload
{
    public static final Type<DraftBlueprintPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, "draft_blueprint"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DraftBlueprintPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, DraftBlueprintPayload::blueprintId,
                    DraftBlueprintPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
