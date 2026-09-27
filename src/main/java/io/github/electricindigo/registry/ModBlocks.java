package io.github.electricindigo.registry;

import io.github.electricindigo.ChronoDynamics;
import io.github.electricindigo.block.chemistrybench.ChemistryBenchBlock;
import io.github.electricindigo.block.researchdesk.ResearchDeskBlock;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks
{
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ChronoDynamics.MODID);

    public static final DeferredBlock<Block> RESEARCH_DESK = BLOCKS.registerBlock(
            "research_desk",
            ResearchDeskBlock::new,
            props -> props.noOcclusion().strength(2.5f).requiresCorrectToolForDrops()
    );

    public static final DeferredBlock<Block> CHEMISTRY_BENCH = BLOCKS.registerBlock(
            "chemistry_bench",
            ChemistryBenchBlock::new,
            properties -> properties.strength(3.5f).requiresCorrectToolForDrops()
    );

    public static final DeferredBlock<Block> CHRONITE_CLUSTER = BLOCKS.registerBlock(
            "chronite_cluster",
            properties -> new AmethystClusterBlock(7.0F, 10.0F, properties),
            properties -> properties.mapColor(MapColor.COLOR_PURPLE).forceSolidOn().noOcclusion().noCollision()
                    .sound(SoundType.AMETHYST_CLUSTER).strength(1.5F)
                    .lightLevel(state -> 5).pushReaction(PushReaction.POPPED)
    );

    public static void register(IEventBus modEventBus)
    {
        BLOCKS.register(modEventBus);
    }
}
