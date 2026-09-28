package io.github.electricindigo.block.researchdesk;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class PartSlot extends DeskSlot
{
    private final int partIndex;

    public PartSlot(ResearchDeskMenu menu, Container container, int partIndex, int x, int y)
    {
        super(menu, container, ResearchDeskMenu.FIRST_PART_SLOT + partIndex, x, y);
        this.partIndex = partIndex;
    }

    @Override
    public boolean isActive()
    {
        return super.isActive() && menu.isPartSlotUsed(partIndex);
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return menu.acceptsPart(partIndex, stack);
    }
}
