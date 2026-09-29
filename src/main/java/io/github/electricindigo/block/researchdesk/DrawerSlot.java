package io.github.electricindigo.block.researchdesk;

import io.github.electricindigo.registry.ModItems;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class DrawerSlot extends Slot
{
    private final ResearchDeskMenu menu;

    public DrawerSlot(ResearchDeskMenu menu, Container container, int slot, int x, int y)
    {
        super(container, slot, x, y);
        this.menu = menu;
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return stack.is(ModItems.BLUEPRINT.get());
    }

    // Hidden while the drawer is closed
    @Override
    public boolean isActive()
    {
        return menu.isDrawerOpen();
    }
}
