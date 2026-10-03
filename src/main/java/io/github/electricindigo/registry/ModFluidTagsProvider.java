package io.github.electricindigo.registry;

import io.github.electricindigo.ChronoDynamics;
import io.github.electricindigo.ChronoDynamicsClient;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.FluidTags;

import java.util.concurrent.CompletableFuture;

/*public class ModFluidTagsProvider extends FluidTagsProvider
{

   public ModFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ChronoDynamics.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(FluidTags.WATER)
                .add(ModFluids.CHRONITE_SLURRY_SOURCE.getKey())
                .add(ModFluids.CHRONITE_SLURRY_FLOWING.getKey())
                .add(ModFluids.SULFURIC_ACID_SOURCE.getKey())
                .add(ModFluids.SULFURIC_ACID_FLOWING.getKey());
    }
}*/
