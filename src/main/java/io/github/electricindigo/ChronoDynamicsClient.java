package io.github.electricindigo;

import io.github.electricindigo.client.RetroBlocks;
import io.github.electricindigo.client.RetroPack;
import io.github.electricindigo.client.RiftRetroProperty;
import io.github.electricindigo.client.TimeDistortion;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;
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
    }

    private static void onRegisterItemConditions(RegisterConditionalItemModelPropertyEvent event)
    {
        event.register(Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, "rift_retro"), RiftRetroProperty.MAP_CODEC);
    }
}
