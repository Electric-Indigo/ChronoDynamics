package io.github.electricindigo.block.computerdesk;

import io.github.electricindigo.block.researchdesk.DeskSlot;
import io.github.electricindigo.block.researchdesk.PartSlot;
import io.github.electricindigo.registry.ModItems;
import io.github.electricindigo.registry.ModMenuTypes;
import io.github.electricindigo.research.blueprint.Blueprint;
import io.github.electricindigo.research.blueprint.BlueprintManager;
import net.minecraft.util.Prediction;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;

public class ComputerDeskMenu extends AbstractContainerMenu
{
    private static final int INV_START = 0;
    private static final int HOTBAR_START = INV_START + 27;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    public ComputerDeskMenu(int containerId, Inventory playerInventory)
    {
        super(ModMenuTypes.COMPUTER_DESK_MENU.get(), containerId);
        this.addStandardInventorySlots(playerInventory, 36, 137);
    }

    // Shift-click just swaps between inventory and hotbar
    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < HOTBAR_START)
        {
            if (!moveItemStackTo(stack, HOTBAR_START, HOTBAR_END, false)) return ItemStack.EMPTY;
        }
        else if (!moveItemStackTo(stack, INV_START, HOTBAR_START, false)) return ItemStack.EMPTY;

        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();

        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public boolean stillValid(Player player)
    {
        return true;
    }
}
