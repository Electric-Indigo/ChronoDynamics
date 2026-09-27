package io.github.electricindigo.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.ArrayList;
import java.util.List;

public record ChemistryInput(ItemStack first, ItemStack second, ItemStack third) implements RecipeInput
{

    @Override
    public ItemStack getItem(int i) {
        return switch (i)
        {
            case 0 -> first;
            case 1 -> second;
            case 2 -> third;
            default -> throw new IllegalArgumentException("No item for index " + i);
        };
    }

    @Override
    public int size() {
        return 3;
    }

    public List<ItemStack> nonEmptyItems()
    {
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < size(); i++)
        {
            if (!getItem(i).isEmpty()) list.add(getItem(i));
        }
        return list;
    }
}
