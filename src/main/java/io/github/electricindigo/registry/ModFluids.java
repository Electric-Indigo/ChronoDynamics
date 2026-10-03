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

public final class ModFluids
{
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, ChronoDynamics.MODID);

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(BuiltInRegistries.FLUID, ChronoDynamics.MODID);

    public record ModFluid(String name,
                           DeferredHolder<FluidType, FluidType> type,
                           DeferredHolder<Fluid, FlowingFluid> source,
                           DeferredHolder<Fluid, FlowingFluid> flowing,
                           DeferredBlock<LiquidBlock> block,
                           DeferredItem<BucketItem> bucket,
                           int color,
                           boolean customTextures){}

    public static final List<ModFluid> ALL = new ArrayList<>();

    public static final ModFluid SULFURIC_ACID = register("sulfuric_acid", 0xD0D8E04A, false);
    public static final ModFluid CHRONITE_SLURRY = register("chronite_slurry", 0xFFFFFFFF, true);

    private ModFluids(){}

    private static ModFluid register(String name, int color, boolean customTextures)
    {
        BaseFlowingFluid.Properties[] props = new BaseFlowingFluid.Properties[1];

        DeferredHolder<FluidType, FluidType> type =
                FLUID_TYPES.register(name, () -> new FluidType(FluidType.Properties.create()));
        DeferredHolder<Fluid, FlowingFluid> source =
                FLUIDS.register(name, () -> new BaseFlowingFluid.Source(props[0]));
        DeferredHolder<Fluid, FlowingFluid> flowing =
                FLUIDS.register(name + "_flowing", () -> new BaseFlowingFluid.Flowing(props[0]));
        DeferredBlock<LiquidBlock> block = ModBlocks.BLOCKS.registerBlock(name,
                p -> new LiquidBlock(source.value(),
                        p.mapColor(MapColor.WATER).replaceable().noCollision().strength(100.0F).pushReaction(PushReaction.POPPED).noLootTable().liquid().sound(SoundType.EMPTY)));
        DeferredItem<BucketItem> bucket = ModItems.ITEMS.registerItem(name + "_bucket",
                p -> new BucketItem(source.value(), p.craftRemainder(Items.BUCKET).stacksTo(1)));

        props[0] = new BaseFlowingFluid.Properties(type, source, flowing).bucket(bucket).block(block);

        ModFluid fluid = new ModFluid(name, type, source, flowing, block, bucket, color, customTextures);
        ALL.add(fluid);
        return fluid;
    }

    public static void register(IEventBus modEventBus)
    {
        FLUID_TYPES.register(modEventBus);
        FLUIDS.register(modEventBus);
    }
}
