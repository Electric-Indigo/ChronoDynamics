package io.github.electricindigo.registry;

import io.github.electricindigo.ChronoDynamics;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.joml.Vector4f;

import java.util.function.Supplier;

public class ModFluidTypes
{
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, ChronoDynamics.MODID);

    public static final Supplier<FluidType> CHRONITE_SLURRY_FLUID_TYPE = FLUID_TYPES.register("chronite_slurry",
            () -> new FluidType(FluidType.Properties.create().isWaterLike(true)));

    public static final Supplier<FluidType> SULFURIC_ACID_FLUID_TYPE = FLUID_TYPES.register("sulfuric_acid",
            () -> new FluidType(FluidType.Properties.create().isWaterLike(true)));

    public static IClientFluidTypeExtensions CHRONITE_SLURRY_EXTENSION = new IClientFluidTypeExtensions()
    {
        @Override
        public void modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor)
        {
            fluidFogColor.set(0.21f, 0.004f, 0.247f);
            IClientFluidTypeExtensions.super.modifyFogColor(camera, partialTick, level, renderDistance, darkenWorldAmount, fluidFogColor);
        }
    };

    public static void register(IEventBus modEventBus) {
        FLUID_TYPES.register(modEventBus);
    }
}
