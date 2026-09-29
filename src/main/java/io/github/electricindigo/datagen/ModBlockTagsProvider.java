package io.github.electricindigo.datagen;

import io.github.electricindigo.ChronoDynamics;
import io.github.electricindigo.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider
{

    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ChronoDynamics.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.RESEARCH_DESK.getKey());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.CHRONITE_CLUSTER.getKey())
                .add(ModBlocks.CHEMISTRY_BENCH.getKey())
                .add(ModBlocks.COMPUTER_DESK.getKey());
    }
}
