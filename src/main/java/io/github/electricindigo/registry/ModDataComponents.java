package io.github.electricindigo.registry;

import com.mojang.serialization.Codec;
import io.github.electricindigo.ChronoDynamics;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents
{
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ChronoDynamics.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SCANNER_ACTIVE =
            COMPONENTS.registerComponentType("scanner_active",
                    builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SCANNER_FUEL =
            COMPONENTS.registerComponentType("scanner_fuel",
                    builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    public static void register(IEventBus modEventBus)
    {
        COMPONENTS.register(modEventBus);
    }
}
