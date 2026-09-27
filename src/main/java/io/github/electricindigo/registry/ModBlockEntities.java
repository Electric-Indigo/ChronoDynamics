package io.github.electricindigo.registry;

import io.github.electricindigo.ChronoDynamics;
import io.github.electricindigo.block.chemistrybench.ChemistryBenchBlockEntity;
import io.github.electricindigo.block.researchdesk.ResearchDeskBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ChronoDynamics.MODID);

    public static final Supplier<BlockEntityType<ResearchDeskBlockEntity>> RESEARCH_DESK =
            BLOCK_ENTITIES.register("research_desk",
                    ()-> new BlockEntityType<ResearchDeskBlockEntity>(
                            (pos, state) -> new ResearchDeskBlockEntity(ModBlockEntities.RESEARCH_DESK.get(), pos, state),
                            false,
                            ModBlocks.RESEARCH_DESK.get()
                    ));


    public static final Supplier<BlockEntityType<ChemistryBenchBlockEntity>> CHEMISTRY_BENCH =
            BLOCK_ENTITIES.register("chemistry_bench",
                    () -> new BlockEntityType<>(ChemistryBenchBlockEntity::new, false, ModBlocks.CHEMISTRY_BENCH.get())
            );

    public static void register(IEventBus modEventBus)
    {
        BLOCK_ENTITIES.register(modEventBus);
    }
}
