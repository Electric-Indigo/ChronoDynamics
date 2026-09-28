package io.github.electricindigo.network;

import io.github.electricindigo.ChronoDynamics;
import io.github.electricindigo.client.PuzzleClientHandler;
import io.github.electricindigo.research.ResearchManager;
import io.github.electricindigo.research.blueprint.BlueprintManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetworking
{
    private ModNetworking(){}

    public static void register(IEventBus modEventBus)
    {
        modEventBus.addListener(ModNetworking::onRegisterPayloadHandlers);
    }

    private static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event)
    {
        PayloadRegistrar registrar = event.registrar(ChronoDynamics.MODID).versioned("1.0.0");

        registrar.playToClient(
                OpenCausalityPuzzlePayload.TYPE,
                OpenCausalityPuzzlePayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(()->
                        PuzzleClientHandler.openCausalityPuzzle(payload.seed(), payload.difficulty()))
        );

        registrar.playToClient(
                OpenWaveformPuzzlePayload.TYPE,
                OpenWaveformPuzzlePayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(()->
                        PuzzleClientHandler.openWaveformPuzzle(payload.seed(), payload.difficulty()))
        );

        registrar.playToServer(
                UnlockResearchPayload.TYPE,
                UnlockResearchPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        ResearchManager.tryUnlock((ServerPlayer) context.player(), payload.nodeId()))
        );

        registrar.playToServer(
                DraftBlueprintPayload.TYPE,
                DraftBlueprintPayload.STREAM_CODEC,
                ((payload, context) -> context.enqueueWork(() ->
                        BlueprintManager.tryDraft((ServerPlayer) context.player(), payload.blueprintId())))
        );

        registrar.playToServer(
                AssembleBlueprintPayload.TYPE,
                AssembleBlueprintPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        BlueprintManager.tryAssemble((ServerPlayer) context.player()))
        );
    }
}
