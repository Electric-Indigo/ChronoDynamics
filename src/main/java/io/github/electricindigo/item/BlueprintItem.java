package io.github.electricindigo.item;

import io.github.electricindigo.registry.ModDataComponents;
import io.github.electricindigo.research.blueprint.Blueprint;
import io.github.electricindigo.research.blueprint.Blueprints;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BlueprintItem extends Item
{

    public BlueprintItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack itemStack) {
        String id = itemStack.get(ModDataComponents.BLUEPRINT_ID.get());
        Blueprint blueprint = id == null ? null : Blueprints.get(id);
        if (blueprint == null)
        {
            return super.getName(itemStack);
        }
        return Component.translatable("item.chronodynamics.blueprint.named",
                blueprint.result().get().getHoverName());
    }
}
