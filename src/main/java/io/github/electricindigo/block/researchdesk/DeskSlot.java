package io.github.electricindigo.block.researchdesk;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class DeskSlot extends Slot
{
    public final ResearchDeskMenu menu;

    public DeskSlot(ResearchDeskMenu menu, Container container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.menu = menu;
    }

    @Override
    public boolean isActive() {
        return menu.areDeskSlotsVisible();
    }
}
