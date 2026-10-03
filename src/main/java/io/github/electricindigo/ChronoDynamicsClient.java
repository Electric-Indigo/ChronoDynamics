package io.github.electricindigo;

import io.github.electricindigo.client.RetroBlocks;
import io.github.electricindigo.client.RetroPack;
import io.github.electricindigo.client.RiftRetroProperty;
import io.github.electricindigo.client.TimeDistortion;
import io.github.electricindigo.registry.ModFluids;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.fluid.FluidTintSources;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = ChronoDynamics.MODID, dist = Dist.CLIENT)
public class ChronoDynamicsClient
{
    public ChronoDynamicsClient(IEventBus modEventBus, ModContainer modContainer)
    {
        modContainer.registerConfig(ModConfig.Type.CLIENT, ModClientConfig.SPEC);
        modEventBus.addListener(ChronoDynamicsClient::onRegisterItemConditions);
        modEventBus.addListener(RetroPack::onAddPackFinders);
        modEventBus.addListener(RetroBlocks::onModifyBakingResult);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        NeoForge.EVENT_BUS.addListener(TimeDistortion::onClientTick);
        NeoForge.EVENT_BUS.addListener(RetroBlocks::onClientTick);
        modEventBus.addListener(ChronoDynamicsClient::onRegisterFluidModels);
    }

    private static void onRegisterItemConditions(RegisterConditionalItemModelPropertyEvent event)
    {
        event.register(Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, "rift_retro"), RiftRetroProperty.MAP_CODEC);
    }

    private static void onRegisterFluidModels(RegisterFluidModelsEvent event)
    {
        for (ModFluids.ModFluid fluid : ModFluids.ALL)
        {
            Identifier still = fluid.customTextures()
                    ? Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, "block/fluid/" + fluid.name() + "_still")
                    : Identifier.withDefaultNamespace("block/water_still");
            Identifier flow = fluid.customTextures()
                    ? Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, "block/fluid/" + fluid.name() + "_flow")
                    : Identifier.withDefaultNamespace("block/water_flow");
            Identifier overlay = fluid.customTextures() ? still : Identifier.withDefaultNamespace("block/water_overlay");

            event.register(new FluidModel.Unbaked(
                            new Material(still, true),
                            new Material(flow, true),
                            new Material(overlay, true),
                            FluidTintSources.constant(fluid.color())),
                    fluid.source().value(), fluid.flowing().value());
        }
    }
}
