package io.github.electricindigo.registry;

import io.github.electricindigo.ChronoDynamics;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ChronoDynamics.MODID);

    public static final Supplier<CreativeModeTab> CHRONODYNAMICS_TAB = CREATIVE_MODE_TABS.register(
            "chronodynamics_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.chronodynamics.chronodynamics_tab"))
                    .icon(() -> ModItems.COMPUTER_UPGRADE.get().getDefaultInstance())
                    .displayItems((params, output) ->
                    {
                        output.accept(ModItems.EFD_ITEM.get());
                        output.accept(ModItems.COMPUTER_UPGRADE.get());
                        output.accept(ModBlocks.RESEARCH_DESK.get());
                        output.accept(ModBlocks.CHRONITE_CLUSTER.get());
                        output.accept(ModItems.CHRONITE_CRYSTAL.get());
                    })
                    .build()
    );

    public static void register(IEventBus modEventBus)
    {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
