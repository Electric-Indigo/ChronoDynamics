package io.github.electricindigo;

import io.github.electricindigo.client.RetroBlocks;
import io.github.electricindigo.client.RetroPack;
import io.github.electricindigo.client.RiftRetroProperty;
import io.github.electricindigo.client.TimeDistortion;
import io.github.electricindigo.registry.ModFluidTypes;
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
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
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
        modEventBus.addListener(ChronoDynamicsClient::onRegisterClientExtensions);
    }

    private static void onRegisterItemConditions(RegisterConditionalItemModelPropertyEvent event)
    {
        event.register(Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, "rift_retro"), RiftRetroProperty.MAP_CODEC);
    }

    private static void onRegisterFluidModels(RegisterFluidModelsEvent event)
    {
        Identifier slurryStill = Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, "block/fluid/chronite_slurry_still");
        Identifier slurryFlow = Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, "block/fluid/chronite_slurry_flow");

        event.register(new FluidModel.Unbaked(
                        new Material(slurryStill, true),
                        new Material(slurryFlow, true),
                        new Material(slurryStill, true),
                        FluidTintSources.constant(0xFFFFFFFF)),
                ModFluids.CHRONITE_SLURRY_SOURCE.get(), ModFluids.CHRONITE_SLURRY_FLOWING.get());

        event.register(new FluidModel.Unbaked(
                        new Material(Identifier.withDefaultNamespace("block/water_still"), true),
                        new Material(Identifier.withDefaultNamespace("block/water_flow"), true),
                        new Material(Identifier.withDefaultNamespace("block/water_overlay"), true),
                        FluidTintSources.constant(0xD0D8E04A)),
                ModFluids.SULFURIC_ACID_SOURCE.get(), ModFluids.SULFURIC_ACID_FLOWING.get());
    }

    private static void onRegisterClientExtensions(RegisterClientExtensionsEvent event)
    {
        event.registerFluidType(ModFluidTypes.CHRONITE_SLURRY_EXTENSION, ModFluidTypes.CHRONITE_SLURRY_FLUID_TYPE.get());
    }
}
