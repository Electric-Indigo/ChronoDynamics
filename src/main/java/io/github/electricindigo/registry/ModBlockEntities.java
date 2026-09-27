package io.github.electricindigo.registry;

import io.github.electricindigo.ChronoDynamics;
import io.github.electricindigo.block.researchdesk.ResearchDeskBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
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

    public static void register(IEventBus modEventBus)
    {
        BLOCK_ENTITIES.register(modEventBus);
    }
}
