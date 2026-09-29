package io.github.electricindigo.block.researchdesk;

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

public class ResearchDeskMenu extends AbstractContainerMenu
{
    // Slot numbers inside the desk's workbench
    public static final int BLUEPRINT_SLOT = 0;
    public static final int FIRST_PART_SLOT = 1;
    public static final int PART_SLOTS = 6;
    public static final int OUTPUT_SLOT = FIRST_PART_SLOT + PART_SLOTS; // 7
    public static final int WORKBENCH_SIZE = OUTPUT_SLOT + 1;           // 8

    // Where each part slot sits on the blueprint paper (item position)
    public static final int[][] PART_POSITIONS = {
            {41, 35}, {63, 35}, {85, 35},
            {41, 65}, {63, 65}, {85, 65}
    };
    public static final int OUTPUT_X = 137, OUTPUT_Y = 51;

    // Drawer slots come right after the workbench. The size itself lives in ResearchDeskBlockEntity.DRAWER_SIZE.
    public static final int DRAWER_START = WORKBENCH_SIZE;                                   // 8
    public static final int DRAWER_END = DRAWER_START + ResearchDeskBlockEntity.DRAWER_SIZE; // 17 with 9 slots

    // Drawer panel layout, relative to the desk GUI's top-left. It sticks out to the left.
    public static final int DRAWER_COLUMNS = 3;
    public static final int DRAWER_ROWS = (ResearchDeskBlockEntity.DRAWER_SIZE + DRAWER_COLUMNS - 1) / DRAWER_COLUMNS;
    public static final int DRAWER_PADDING = 7;
    public static final int DRAWER_WIDTH = DRAWER_PADDING * 2 + DRAWER_COLUMNS * 18;
    public static final int DRAWER_HEIGHT = DRAWER_PADDING * 2 + DRAWER_ROWS * 18;
    public static final int DRAWER_TOP = 130;

    private static final int INV_START = DRAWER_END;
    private static final int HOTBAR_START = INV_START + 27;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final Player player;

    // Temporary storage, like a crafting table. Emptied back to the player when the desk closes.
    private final Container workbench = new SimpleContainer(WORKBENCH_SIZE)
    {
        @Override
        public void setChanged()
        {
            super.setChanged();
            ResearchDeskMenu.this.slotsChanged(this);
        }
    };

    // The desk's drawer. On the server this is the real block entity; on the client it's a stand-in that gets synced.
    private final Container drawer;

    // Only the client's screen changes these
    private boolean deskSlotsVisible = true;
    private boolean drawerOpen = false;

    // Client side (called by the MenuType)
    public ResearchDeskMenu(int containerId, Inventory playerInventory)
    {
        this(containerId, playerInventory, new SimpleContainer(ResearchDeskBlockEntity.DRAWER_SIZE));
    }

    // Server side (called by the block entity)
    public ResearchDeskMenu(int containerId, Inventory playerInventory, Container drawer)
    {
        super(ModMenuTypes.RESEARCH_DESK_MENU.get(), containerId);
        checkContainerSize(drawer, ResearchDeskBlockEntity.DRAWER_SIZE);
        this.player = playerInventory.player;
        this.drawer = drawer;

        // Blueprint (or paper) slot, top-left corner of the paper
        addSlot(new DeskSlot(this, workbench, BLUEPRINT_SLOT, 16, 13)
        {
            @Override
            public boolean mayPlace(ItemStack stack)
            {
                return stack.is(ModItems.BLUEPRINT.get()) || stack.is(Items.PAPER);
            }
        });

        // Part slots
        for (int i = 0; i < PART_SLOTS; i++)
        {
            addSlot(new PartSlot(this, workbench, i, PART_POSITIONS[i][0], PART_POSITIONS[i][1]));
        }

        // Output, take-only. Stays visible while it holds something, even if the blueprint is pulled.
        addSlot(new DeskSlot(this, workbench, OUTPUT_SLOT, OUTPUT_X, OUTPUT_Y)
        {
            @Override
            public boolean isActive()
            {
                return super.isActive() && (getReadableBlueprint() != null || hasItem());
            }

            @Override
            public boolean mayPlace(ItemStack stack)
            {
                return false;
            }
        });

        // Drawer slots, laid out in a grid on the pull-out panel
        for (int i = 0; i < ResearchDeskBlockEntity.DRAWER_SIZE; i++)
        {
            int x = -DRAWER_WIDTH + DRAWER_PADDING + 1 + (i % DRAWER_COLUMNS) * 18;
            int y = DRAWER_TOP + DRAWER_PADDING + 1 + (i / DRAWER_COLUMNS) * 18;
            addSlot(new DrawerSlot(this, drawer, i, x, y));
        }

        this.addStandardInventorySlots(playerInventory, 36, 137);
    }

    // ---------- Blueprint info ----------

    // The blueprint in the slot, researched or not (null if there isn't one)
    public @Nullable Blueprint getBlueprint()
    {
        return BlueprintManager.fromStack(workbench.getItem(BLUEPRINT_SLOT));
    }

    // The blueprint in the slot, but only if this player has researched it
    public @Nullable Blueprint getReadableBlueprint()
    {
        Blueprint blueprint = getBlueprint();
        return blueprint != null && BlueprintManager.isUnlocked(player, blueprint) ? blueprint : null;
    }

    public boolean isPartSlotUsed(int partIndex)
    {
        Blueprint blueprint = getReadableBlueprint();
        return blueprint != null && partIndex < blueprint.costs().size();
    }

    public boolean acceptsPart(int partIndex, ItemStack stack)
    {
        if (!isPartSlotUsed(partIndex)) return false;
        return stack.is(getReadableBlueprint().costs().get(partIndex).item().get());
    }

    // ---------- Tabs ----------

    public boolean areDeskSlotsVisible()
    {
        return deskSlotsVisible;
    }

    public void setDeskSlotsVisible(boolean visible)
    {
        this.deskSlotsVisible = visible;
    }

    public Container getWorkbench()
    {
        return workbench;
    }

    // ---------- Drawer ----------

    // The server never hides slots, only the client's screen does
    public boolean isDrawerOpen()
    {
        return drawerOpen || !player.level().isClientSide();
    }

    public void setDrawerOpen(boolean open)
    {
        this.drawerOpen = open;
    }

    // ---------- Housekeeping ----------

    // If the blueprint changes, hand back any parts that no longer belong
    @Override
    public void slotsChanged(Container container)
    {
        super.slotsChanged(container);
        if (container == workbench && !player.level().isClientSide())
        {
            returnInvalidParts();
        }
    }

    private void returnInvalidParts()
    {
        for (int i = 0; i < PART_SLOTS; i++)
        {
            ItemStack stack = workbench.getItem(FIRST_PART_SLOT + i);
            if (!stack.isEmpty() && !acceptsPart(i, stack))
            {
                workbench.setItem(FIRST_PART_SLOT + i, ItemStack.EMPTY);
                player.getInventory().placeItemBackInInventory(stack, Prediction.SERVER_ONLY);
            }
        }
    }

    // Give everything back when the desk is closed
    @Override
    public void removed(Player player)
    {
        super.removed(player);
        clearContainer(player, workbench);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < WORKBENCH_SIZE)
        {
            // Desk -> player inventory
            if (!moveItemStackTo(stack, INV_START, HOTBAR_END, true)) return ItemStack.EMPTY;
        }
        else if (index < DRAWER_END)
        {
            // Drawer -> player inventory
            if (!moveItemStackTo(stack, INV_START, HOTBAR_END, true)) return ItemStack.EMPTY;
        }
        else if (stack.is(ModItems.BLUEPRINT.get()))
        {
            // Blueprints go to the blueprint slot, or the drawer if that's taken
            if (!moveItemStackTo(stack, BLUEPRINT_SLOT, BLUEPRINT_SLOT + 1, false)
                    && !moveItemStackTo(stack, DRAWER_START, DRAWER_END, false)) return ItemStack.EMPTY;
        }
        else if (stack.is(Items.PAPER))
        {
            // Paper goes to the blueprint slot
            if (!moveItemStackTo(stack, BLUEPRINT_SLOT, BLUEPRINT_SLOT + 1, false)) return ItemStack.EMPTY;
        }
        else if (!moveItemStackTo(stack, FIRST_PART_SLOT, OUTPUT_SLOT, false))
        {
            // Not a part for this blueprint, so swap between inventory and hotbar instead
            if (index < HOTBAR_START)
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
    public boolean stillValid(Player player)
    {
        // Closes the screen if the desk is broken or you walk away, so the drawer can't be emptied twice
        return drawer.stillValid(player);
    }
}
