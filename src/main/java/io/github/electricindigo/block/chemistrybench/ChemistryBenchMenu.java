package io.github.electricindigo.block.chemistrybench;

import io.github.electricindigo.registry.ModMenuTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;

import static io.github.electricindigo.block.chemistrybench.ChemistryBenchBlockEntity.*;
public class ChemistryBenchMenu extends AbstractContainerMenu
{
    private static final int INV_START = SLOT_COUNT;
    private static final int HOTBAR_START = INV_START + 27;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final Container container;
    private final ContainerData data;

    public ChemistryBenchMenu(int containerId, Inventory inventory)
    {
        this(containerId, inventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(11));
    }

    public ChemistryBenchMenu(int containerId, Inventory inventory, Container container, ContainerData data)
    {
        super(ModMenuTypes.CHEMISTRY_BENCH_MENU.get(), containerId);
        checkContainerSize(container, SLOT_COUNT);
        this.container = container;
        this.data = data;

        addSlot(new Slot(container, INPUT_1, 71, 35));
        addSlot(new Slot(container, INPUT_2, 62, 57));
        addSlot(new Slot(container, INPUT_3, 80, 57));
        addSlot(new FuelSlot(container, FUEL, 69, 107));
        addSlot(new Slot(container, OUTPUT, 135, 44));
        addSlot(new Slot(container, SOLVENT_CONTAINER, 17, 95));
        addSlot(new Slot(container, PRODUCT_EMPTY, 169, 99));
        addSlot(new Slot(container, PRODUCT_FILLED, 189, 99));

        addStandardInventorySlots(inventory, 34, 137);
        addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i)
    {
        Slot slot = slots.get(i);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (i < INV_START)
        {
            if (!moveItemStackTo(stack, INV_START, HOTBAR_END, true)) return ItemStack.EMPTY;
        }
        else if (!(ChemistryBenchFuels.isFuel(stack) && moveItemStackTo(stack, FUEL, FUEL + 1, false))
        && !moveItemStackTo(stack, INPUT_1, INPUT_3 + 1, false))
        {
            if (i < HOTBAR_START)
            {
                if (!moveItemStackTo(stack, HOTBAR_START, HOTBAR_END, false)) return ItemStack.EMPTY;
            }
            else if (!moveItemStackTo(stack, INV_START, HOTBAR_START, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();

        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    public int getTemperature()
    {
        return data.get(0);
    }

    public boolean isBurning()
    {
        return data.get(1) > 0;
    }

    public float getBurnLeft()
    {
        int total = data.get(2);
        return total == 0 ? 0 : (float) data.get(1) / total;
    }

    public float getCraftProgress()
    {
        int total = data.get(4);
        return total == 0 ? 0 : (float) data.get(3) / total;
    }

    public int getMinHeat()
    {
        return data.get(5);
    }

    public int getMaxHeat()
    {
        return data.get(6);
    }

    public FluidStack getSolvent()
    {
        return fluidFrom(7, 8);
    }

    public FluidStack getProduct()
    {
        return fluidFrom(9, 10);
    }

    private FluidStack fluidFrom(int idIndex, int amountIndex)
    {
        int amount = data.get(amountIndex);
        if (amount <= 0) return FluidStack.EMPTY;
        return new FluidStack(BuiltInRegistries.FLUID.byId(data.get(idIndex)), amount);
    }
}
