package io.github.electricindigo.registry;

import io.github.electricindigo.ChronoDynamics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ModFluids
{
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(BuiltInRegistries.FLUID, ChronoDynamics.MODID);

    public static final DeferredHolder<Fluid, FlowingFluid> CHRONITE_SLURRY_SOURCE = FLUIDS.register("chronite_slurry",
            () -> new BaseFlowingFluid.Source(ModFluids.CHRONITE_SLURRY_PROPS));
    public static final DeferredHolder<Fluid, FlowingFluid> CHRONITE_SLURRY_FLOWING = FLUIDS.register("chronite_slurry_flowing",
            () -> new BaseFlowingFluid.Flowing(ModFluids.CHRONITE_SLURRY_PROPS));

    public static final DeferredHolder<Fluid, FlowingFluid> SULFURIC_ACID_SOURCE = FLUIDS.register("sulfuric_acid",
            () -> new BaseFlowingFluid.Source(ModFluids.SULFURIC_ACID_PROPS));
    public static final DeferredHolder<Fluid, FlowingFluid> SULFURIC_ACID_FLOWING = FLUIDS.register("sulfuric_acid_flowing",
            () -> new BaseFlowingFluid.Flowing(ModFluids.SULFURIC_ACID_PROPS));

    private static final BaseFlowingFluid.Properties CHRONITE_SLURRY_PROPS = new BaseFlowingFluid.Properties(
            ModFluidTypes.CHRONITE_SLURRY_FLUID_TYPE, CHRONITE_SLURRY_SOURCE, CHRONITE_SLURRY_FLOWING)
            .slopeFindDistance(2).levelDecreasePerBlock(1)
            .block(ModBlocks.CHRONITE_SLURRY_LIQUID_BLOCK).bucket(ModItems.CHRONITE_SLURRY_BUCKET);

    private static final BaseFlowingFluid.Properties SULFURIC_ACID_PROPS = new BaseFlowingFluid.Properties(
            ModFluidTypes.SULFURIC_ACID_FLUID_TYPE, SULFURIC_ACID_SOURCE, SULFURIC_ACID_FLOWING)
            .slopeFindDistance(2).levelDecreasePerBlock(1)
            .block(ModBlocks.SULFURIC_ACID_LIQUID_BLOCK).bucket(ModItems.SULFURIC_ACID_BUCKET);

    public static void register(IEventBus modEventBus)
    {
        FLUIDS.register(modEventBus);
    }
}
