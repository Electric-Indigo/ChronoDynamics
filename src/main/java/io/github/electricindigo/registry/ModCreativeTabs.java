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
                        output.accept(ModItems.BLUEPRINT.get());
                        output.accept(ModItems.EFD_ITEM.get());

                        //Components
                        output.accept(ModItems.CHRONITE_CRYSTAL.get());
                        output.accept(ModItems.DRAFTING_INK.get());
                        output.accept(ModItems.BURNT_ASH.get());
                        output.accept(ModItems.CONDUCTIVE_REDSTONE_PASTE.get());
                        output.accept(ModItems.PURIFIED_QUARTZ.get());
                        output.accept(ModItems.RESONANT_AMETHYST.get());

                        //Electronic Components
                        output.accept(ModItems.CIRCUIT_BOARD.get());
                        output.accept(ModItems.DRIFT_SENSOR.get());
                        output.accept(ModItems.QUARTZ_OSCILLATOR.get());

                        //Devices
                        output.accept(ModItems.SCANNER_ITEM.get());

                        //Upgrades
                        output.accept(ModItems.COMPUTER_UPGRADE.get());

                        //Blocks
                        output.accept(ModBlocks.CHRONITE_CLUSTER.get());

                        //Workstations
                        output.accept(ModBlocks.RESEARCH_DESK.get());
                        output.accept(ModBlocks.CHEMISTRY_BENCH.get());
                    })
                    .build()
    );

    public static void register(IEventBus modEventBus)
    {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
