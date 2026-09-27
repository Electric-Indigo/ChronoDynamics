package io.github.electricindigo.registry;

import io.github.electricindigo.ChronoDynamics;
import io.github.electricindigo.recipe.ChemistryRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes
{
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, ChronoDynamics.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, ChronoDynamics.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<ChemistryRecipe>> CHEMISTRY_TYPE =
            TYPES.register("chemistry", id -> RecipeType.simple(id));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ChemistryRecipe>> CHEMISTRY_SERIALIZER =
            SERIALIZERS.register("chemistry", () -> ChemistryRecipe.SERIALIZER);

    public static void register(IEventBus modEventBus)
    {
        TYPES.register(modEventBus);
        SERIALIZERS.register(modEventBus);
    }
}
