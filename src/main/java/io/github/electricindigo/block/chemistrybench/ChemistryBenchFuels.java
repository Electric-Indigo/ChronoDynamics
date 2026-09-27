package io.github.electricindigo.block.chemistrybench;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

public final class ChemistryBenchFuels
{
    public record Fuel(int maxTemp, int burnTicks){}

    private ChemistryBenchFuels(){}

    public static @Nullable Fuel get(ItemStack stack)
    {
        if (stack.is(Items.BLAZE_POWDER)) return new Fuel(900, 1200);
        if (stack.is(ItemTags.COALS)) return new Fuel(500, 1600);
        if (stack.is(ItemTags.LOGS_THAT_BURN)) return new Fuel(300, 300);
        if (stack.is(ItemTags.PLANKS)) return new Fuel(200, 300);
        if (stack.is(Items.STICK)) return new Fuel(200, 100);
        return null;
    }

    public static boolean isFuel(ItemStack stack)
    {
        return get(stack) != null;
    }
}
