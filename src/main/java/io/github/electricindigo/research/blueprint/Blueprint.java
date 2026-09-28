package io.github.electricindigo.research.blueprint;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public record Blueprint(String id, Supplier<ItemStack> result, List<Cost> costs,
                        @Nullable String research, Supplier<? extends Item> draftItem)
{
    public record Cost(Supplier<? extends Item> item, int count)
    {
        public ItemStack icon()
        {
            return new ItemStack(item.get());
        }
    }
}
