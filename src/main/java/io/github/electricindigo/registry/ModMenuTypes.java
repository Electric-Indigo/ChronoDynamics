package io.github.electricindigo.registry;

import io.github.electricindigo.ChronoDynamics;
import io.github.electricindigo.block.chemistrybench.ChemistryBenchMenu;
import io.github.electricindigo.block.researchdesk.ResearchDeskMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes
{
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(BuiltInRegistries.MENU, ChronoDynamics.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<ResearchDeskMenu>> RESEARCH_DESK_MENU =
            MENU_TYPES.register("research_desk_menu",
                    () -> new MenuType(ResearchDeskMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<ChemistryBenchMenu>> CHEMISTRY_BENCH_MENU =
            MENU_TYPES.register("chemistry_bench_menu",
                    () -> new MenuType<>(ChemistryBenchMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static void register(IEventBus modEventBus)
    {
        MENU_TYPES.register(modEventBus);
    }
}
