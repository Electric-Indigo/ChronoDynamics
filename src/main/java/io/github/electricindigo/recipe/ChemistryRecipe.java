package io.github.electricindigo.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;
import java.util.Optional;

public class ChemistryRecipe implements Recipe<ChemistryInput>
{
    public static final MapCodec<ChemistryRecipe> MAP_CODEC = RecordCodecBuilder.<ChemistryRecipe>mapCodec(i -> i.group(
            Ingredient.CODEC.listOf(1, 3).fieldOf("ingredients").forGetter(r -> r.ingredients),
            SizedFluidIngredient.CODEC.optionalFieldOf("solvent").forGetter(r -> r.solvent),
            ItemStackTemplate.CODEC.optionalFieldOf("result").forGetter(r -> r.result),
            FluidStackTemplate.CODEC.optionalFieldOf("fluid_result").forGetter(r -> r.fluidResult),
            Codec.INT.fieldOf("min_heat").forGetter(r -> r.minHeat),
            Codec.INT.fieldOf("max_heat").forGetter(r -> r.maxHeat),
            Codec.INT.optionalFieldOf("time", 200).forGetter(r -> r.time)
    ).apply(i, ChemistryRecipe::new)).validate(r -> r.result.isEmpty() && r.fluidResult.isEmpty()
            ? DataResult.error(() -> "Chemistry recipe needs a \"result\", a \"fluid_result\", or both")
            : DataResult.success(r));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChemistryRecipe> STREAM_CODEC = StreamCodec.of(
            (buf, r) -> {
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, r.ingredients);
                ByteBufCodecs.optional(SizedFluidIngredient.STREAM_CODEC).encode(buf, r.solvent);
                ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC).encode(buf, r.result);
                ByteBufCodecs.optional(FluidStackTemplate.STREAM_CODEC).encode(buf, r.fluidResult);
                ByteBufCodecs.VAR_INT.encode(buf, r.minHeat);
                ByteBufCodecs.VAR_INT.encode(buf, r.maxHeat);
                ByteBufCodecs.VAR_INT.encode(buf, r.time);
            },
            buf -> new ChemistryRecipe(
                    Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf),
                    ByteBufCodecs.optional(SizedFluidIngredient.STREAM_CODEC).decode(buf),
                    ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC).decode(buf),
                    ByteBufCodecs.optional(FluidStackTemplate.STREAM_CODEC).decode(buf),
                    ByteBufCodecs.VAR_INT.decode(buf),
                    ByteBufCodecs.VAR_INT.decode(buf),
                    ByteBufCodecs.VAR_INT.decode(buf))
    );

    public static final RecipeSerializer<ChemistryRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final List<Ingredient> ingredients;
    private final Optional<SizedFluidIngredient> solvent;
    private final Optional<ItemStackTemplate> result;
    private final Optional<FluidStackTemplate> fluidResult;
    private final int minHeat;
    private final int maxHeat;
    private final int time;

    public ChemistryRecipe(List<Ingredient> ingredients, Optional<SizedFluidIngredient> solvent,
                           Optional<ItemStackTemplate> result, Optional<FluidStackTemplate> fluidResult,
                           int minHeat, int maxHeat, int time)
    {
        this.ingredients = ingredients;
        this.solvent = solvent;
        this.result = result;
        this.fluidResult = fluidResult;
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

    public Optional<SizedFluidIngredient> solvent()
    {return solvent;}

    public FluidStack fluidResult()
    {
        return fluidResult.map(FluidStackTemplate::create).orElse(FluidStack.EMPTY);
    }

    @Override
    public boolean matches(ChemistryInput chemistryInput, Level level)
    {
        if (solvent.isPresent() && !solvent.get().test(chemistryInput.solvent())) return false;

        List<ItemStack> items = chemistryInput.nonEmptyItems();
        if (items.size() != ingredients.size()) return false;
        return RecipeMatcher.findMatches(items, ingredients) != null;
    }

    @Override
    public ItemStack assemble(ChemistryInput chemistryInput) {
        return result.map(ItemStackTemplate::create).orElse(ItemStack.EMPTY);
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
