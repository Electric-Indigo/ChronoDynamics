package io.github.electricindigo.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.electricindigo.registry.ModRecipes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;

import java.util.List;

public class ChemistryRecipe implements Recipe<ChemistryInput>
{
    public static final MapCodec<ChemistryRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Ingredient.CODEC.listOf(1, 3).fieldOf("ingredients").forGetter(r -> r.ingredients),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(r -> r.result),
            Codec.INT.fieldOf("min_heat").forGetter(r -> r.minHeat),
            Codec.INT.fieldOf("max_heat").forGetter(r -> r.maxHeat),
            Codec.INT.optionalFieldOf("time", 200).forGetter(r -> r.time)
    ).apply(i, ChemistryRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChemistryRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.ingredients,
            ItemStackTemplate.STREAM_CODEC, r -> r.result,
            ByteBufCodecs.VAR_INT, r -> r.minHeat,
            ByteBufCodecs.VAR_INT, r -> r.maxHeat,
            ByteBufCodecs.VAR_INT, r -> r.time,
            ChemistryRecipe::new
    );

    public static final RecipeSerializer<ChemistryRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final List<Ingredient> ingredients;
    private final ItemStackTemplate result;
    private final int minHeat;
    private final int maxHeat;
    private final int time;

    public ChemistryRecipe(List<Ingredient> ingredients, ItemStackTemplate result, int minHeat, int maxHeat, int time)
    {
        this.ingredients = ingredients;
        this.result = result;
        this.minHeat = minHeat;
        this.maxHeat = maxHeat;
        this.time = time;
    }

    public int minHeat()
    {return minHeat;}

    public int maxHeat()
    {return maxHeat;}

    public int time()
    {return time;}

    @Override
    public boolean matches(ChemistryInput chemistryInput, Level level)
    {
        List<ItemStack> items = chemistryInput.nonEmptyItems();
        if (items.size() != ingredients.size()) return false;
        return RecipeMatcher.findMatches(items, ingredients) != null;
    }

    @Override
    public ItemStack assemble(ChemistryInput chemistryInput) {
        return result.create();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<? extends Recipe<ChemistryInput>> getSerializer() {
        return ModRecipes.CHEMISTRY_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<ChemistryInput>> getType() {
        return ModRecipes.CHEMISTRY_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }
}
