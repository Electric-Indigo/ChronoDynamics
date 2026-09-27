package io.github.electricindigo.block.chemistrybench;

import io.github.electricindigo.recipe.ChemistryInput;
import io.github.electricindigo.recipe.ChemistryRecipe;
import io.github.electricindigo.registry.ModBlockEntities;
import io.github.electricindigo.registry.ModItems;
import io.github.electricindigo.registry.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ChemistryBenchBlockEntity extends BaseContainerBlockEntity
{
    public static final int INPUT_1 = 0;
    public static final int INPUT_2 = 1;
    public static final int INPUT_3 = 2;
    public static final int FUEL = 3;
    public static final int OUTPUT = 4;
    public static final int SLOT_COUNT = 5;

    private NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

    public static final int ROOM_TEMP = 20;
    private static final int HEAT_RATE = 2;
    private static final int COOL_RATE = 1;

    private int temperature = ROOM_TEMP;
    private int burnTime;
    private int burnTimeTotal;
    private int fuelMaxTemp;

    private int progress;       // ticks of crafting done so far
    private int progressTotal;  // ticks the current recipe needs
    private int minHeat;        // current recipe's heat window (0 when no recipe)
    private int maxHeat;

    private final RecipeManager.CachedCheck<ChemistryInput, ChemistryRecipe> quickCheck =
            RecipeManager.createCheck(ModRecipes.CHEMISTRY_TYPE.get());

    public ChemistryBenchBlockEntity(BlockPos pos, BlockState state)
    {
        super(ModBlockEntities.CHEMISTRY_BENCH.get(), pos, state);
    }

    @Override
    protected Component getDefaultName()
    {
        return Component.translatable("block.chronodynamics.chemistry_bench");
    }

    @Override
    protected NonNullList<ItemStack> getItems()
    {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> nonNullList)
    {
        this.items = items;
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inventory)
    {
        return new ChemistryBenchMenu(i, inventory, this, data);
    }

    @Override
    public int getContainerSize()
    {
        return SLOT_COUNT;
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
        temperature = input.getIntOr("temperature", ROOM_TEMP);
        burnTime = input.getIntOr("burn_time", 0);
        burnTimeTotal = input.getIntOr("burn_time_total", 0);
        fuelMaxTemp = input.getIntOr("fuel_max_temp", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
        output.putInt("temperature", temperature);
        output.putInt("burn_time", burnTime);
        output.putInt("burn_time_total", burnTimeTotal);
        output.putInt("fuel_max_temp", fuelMaxTemp);
    }

    private final ContainerData data = new ContainerData()
    {
        @Override
        public int get(int i) {
            return switch (i)
            {
                case 0 -> temperature;
                case 1 -> burnTime;
                case 2 -> burnTimeTotal;
                case 3 -> progress;
                case 4 -> progressTotal;
                case 5 -> minHeat;
                case 6 -> maxHeat;
                default -> 0;
            };
        }

        @Override
        public void set(int i, int value)
        {
            switch (i)
            {
                case 0 -> temperature = value;
                case 1 -> burnTime = value;
                case 2 -> burnTimeTotal = value;
                case 3 -> progress = value;
                case 4 -> progressTotal = value;
                case 5 -> minHeat = value;
                case 6 -> maxHeat = value;
            }
        }

        @Override
        public int getCount()
        {
            return 7;
        }
    };

    public static void serverTick(Level level, BlockPos pos, BlockState state, ChemistryBenchBlockEntity bench)
    {
        if (!(level instanceof ServerLevel serverLevel)) return;

        int oldTemp = bench.temperature;
        int oldBurn = bench.burnTime;
        int oldProgress = bench.progress;

        // 1. What recipe is loaded, and is there room for the result?
        ChemistryInput input = new ChemistryInput(bench.items.get(INPUT_1), bench.items.get(INPUT_2), bench.items.get(INPUT_3));
        ChemistryRecipe recipe = input.isEmpty() ? null
                : bench.quickCheck.getRecipeFor(input, serverLevel).map(RecipeHolder::value).orElse(null);
        ItemStack result = recipe != null ? recipe.assemble(input) : ItemStack.EMPTY;
        boolean canWork = recipe != null && bench.canOutput(result);

        // 2. Fuel: only burns while there's a valid recipe to work on
        if (bench.burnTime > 0)
        {
            bench.burnTime--;
        }
        if (bench.burnTime <= 0 && canWork)
        {
            ItemStack fuelStack = bench.items.get(FUEL);
            ChemistryBenchFuels.Fuel fuel = ChemistryBenchFuels.get(fuelStack);
            if (fuel != null)
            {
                bench.burnTime = fuel.burnTicks();
                bench.burnTimeTotal = fuel.burnTicks();
                bench.fuelMaxTemp = fuel.maxTemp();
                fuelStack.shrink(1);
            }
        }

        // 3. Temperature moves toward the fuel's max, or back to room temp
        int target = bench.burnTime > 0 ? bench.fuelMaxTemp : ROOM_TEMP;
        if (bench.temperature < target)
        {
            bench.temperature = Math.min(target, bench.temperature + HEAT_RATE);
        }
        else if (bench.temperature > target)
        {
            bench.temperature = Math.max(target, bench.temperature - COOL_RATE);
        }

        // 4. Crafting progress
        if (canWork)
        {
            bench.progressTotal = recipe.time();
            bench.minHeat = recipe.minHeat();
            bench.maxHeat = recipe.maxHeat();

            if (bench.temperature > recipe.maxHeat())
            {
                if (bench.progress > 0) bench.ruin(level, pos); // too hot mid-craft: burnt ash
            }
            else if (bench.temperature < recipe.minHeat())
            {
                bench.progress = 0; // too cold: start over
            }
            else
            {
                bench.progress++;
                if (bench.progress >= recipe.time())
                {
                    bench.finish(result);
                }
            }
        }
        else
        {
            bench.progress = 0;
            bench.progressTotal = 0;
            bench.minHeat = 0;
            bench.maxHeat = 0;
        }

        if (bench.temperature != oldTemp || bench.burnTime != oldBurn || bench.progress != oldProgress)
        {
            bench.setChanged();
        }
    }

    private boolean canOutput(ItemStack stack)
    {
        ItemStack out = items.get(OUTPUT);
        if (out.isEmpty()) return true;
        return ItemStack.isSameItemSameComponents(out, stack) && out.getCount() + stack.getCount() <= out.getMaxStackSize();
    }

    private void putInOutput(ItemStack stack)
    {
        ItemStack out = items.get(OUTPUT);
        if (out.isEmpty()) items.set(OUTPUT, stack.copy());
        else out.grow(stack.getCount());
    }

    private void useInputs()
    {
        for (int i = INPUT_1; i <= INPUT_3; i++)
        {
            items.get(i).shrink(1);
        }
    }

    private void finish(ItemStack result)
    {
        useInputs();
        putInOutput(result);
        progress = 0;
    }

    private void ruin(Level level, BlockPos pos)
    {
        useInputs();
        ItemStack ash = new ItemStack(ModItems.BURNT_ASH.get());
        if (canOutput(ash))
        {
            putInOutput(ash);
        }
        else
        {
            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, ash); // output blocked, pop it out
        }
        progress = 0;
    }
}
