package io.github.electricindigo.block.chemistrybench;

import io.github.electricindigo.recipe.ChemistryInput;
import io.github.electricindigo.recipe.ChemistryRecipe;
import io.github.electricindigo.registry.ModBlockEntities;
import io.github.electricindigo.registry.ModItems;
import io.github.electricindigo.registry.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

public class ChemistryBenchBlockEntity extends BaseContainerBlockEntity
{
    public static final int INPUT_1 = 0;
    public static final int INPUT_2 = 1;
    public static final int INPUT_3 = 2;
    public static final int FUEL = 3;
    public static final int OUTPUT = 4;
    public static final int SOLVENT_CONTAINER = 5;
    public static final int PRODUCT_EMPTY = 6;
    public static final int PRODUCT_FILLED = 7;
    public static final int SLOT_COUNT = 8;

    public static final int SOLVENT_TANK = 0;
    public static final int PRODUCT_TANK = 1;
    public static final int TANK_CAPACITY = 4000;

    private NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private final FluidStacksResourceHandler tanks = new FluidStacksResourceHandler(2, TANK_CAPACITY)
    {
        @Override
        protected void onContentsChanged(int index, FluidStack previousContents)
        {
            setChanged();
        }
    };

    private final ResourceHandler<FluidResource> solventTank = RangedResourceHandler.ofSingleIndex(tanks, SOLVENT_TANK);
    private final ResourceHandler<FluidResource> productTank = RangedResourceHandler.ofSingleIndex(tanks, PRODUCT_TANK);

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
        this.items = nonNullList;
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
        tanks.deserialize(input);
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
        tanks.serialize(output);
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
                case 7 -> BuiltInRegistries.FLUID.getId(tanks.getResource(SOLVENT_TANK).getFluid());
                case 8 -> tanks.getAmountAsInt(SOLVENT_TANK);
                case 9 -> BuiltInRegistries.FLUID.getId(tanks.getResource(PRODUCT_TANK).getFluid());
                case 10 -> tanks.getAmountAsInt(PRODUCT_TANK);
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
            return 11;
        }
    };

    public static void serverTick(Level level, BlockPos pos, BlockState state, ChemistryBenchBlockEntity bench)
    {
        if (!(level instanceof ServerLevel serverLevel)) return;

        bench.handleFluidContainers();
        int oldTemp = bench.temperature;
        int oldBurn = bench.burnTime;
        int oldProgress = bench.progress;

        // 1. What recipe is loaded, and is there room for the result?
        ChemistryInput input = new ChemistryInput(bench.items.get(INPUT_1), bench.items.get(INPUT_2),
                bench.items.get(INPUT_3), FluidUtil.getStack(bench.tanks, SOLVENT_TANK));
        ChemistryRecipe recipe = input.nonEmptyItems().isEmpty() ? null
                : bench.quickCheck.getRecipeFor(input, serverLevel).map(RecipeHolder::value).orElse(null);
        ItemStack result = recipe != null ? recipe.assemble(input) : ItemStack.EMPTY;
        boolean canWork = recipe != null && bench.canOutput(result) && bench.canTakeFluid(recipe.fluidResult());

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
                if (bench.progress > 0) bench.ruin(level, pos, recipe); // too hot mid-craft: burnt ash
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
                    bench.finish(result, recipe);
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
        if (stack.isEmpty()) return true; // fluid-only recipes don't need the output slot
        return canFit(OUTPUT, stack);
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

    private void finish(ItemStack result, ChemistryRecipe recipe)
    {
        useInputs();
        useSolvent(recipe);
        if (!result.isEmpty()) putInOutput(result);
        addFluidResult(recipe);
        progress = 0;
    }

    private void ruin(Level level, BlockPos pos, ChemistryRecipe recipe)
    {
        useInputs();
        useSolvent(recipe); // overheating wastes the solvent too
        ItemStack ash = new ItemStack(ModItems.BURNT_ASH.get());
        if (canFit(OUTPUT, ash))
        {
            putInOutput(ash);
        }
        else
        {
            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, ash);
        }
        progress = 0;
    }

    private void handleFluidContainers()
    {
        ItemStack solventStack = items.get(SOLVENT_CONTAINER);
        if (solventStack.getCount() == 1)
        {
            ItemStack emptied =  tryFluidTransfer(solventStack, true, stack -> true);
            if (emptied != null)
            {
                items.set(SOLVENT_CONTAINER, emptied);
            }
        }

        ItemStack emptyStack = items.get(PRODUCT_EMPTY);
        if (!emptyStack.isEmpty() && tanks.getAmountAsInt(PRODUCT_TANK) > 0)
        {
            ItemStack filled = tryFluidTransfer(emptyStack, false, stack -> canFit(PRODUCT_FILLED, stack));
            if (filled != null)
            {
                emptyStack.shrink(1);
                ItemStack out = items.get(PRODUCT_FILLED);
                if (out.isEmpty()) items.set(PRODUCT_FILLED, filled);
                else out.grow(filled.getCount());
                setChanged();
            }
        }
    }

    @Nullable
    private ItemStack tryFluidTransfer(ItemStack stack, boolean intoSolventTank, Predicate<ItemStack> resultFits)
    {
        SimpleContainer temp = new SimpleContainer(stack.copyWithCount(1));
        ResourceHandler<ItemResource> tempHandler = VanillaContainerWrapper.of(temp);
        ResourceHandler<FluidResource> itemFluids =
                ItemAccess.forHandlerIndexStrict(tempHandler, 0).getCapability(Capabilities.Fluid.ITEM);
        if (itemFluids == null) return null;

        try(Transaction tx = Transaction.openRoot())
        {
            int moved = intoSolventTank
                    ? ResourceHandlerUtil.move(itemFluids, solventTank, fluid -> true, TANK_CAPACITY, tx)
                    : ResourceHandlerUtil.move(productTank, itemFluids, fluid -> true, TANK_CAPACITY, tx);
            if (moved <= 0) return null;

            ItemStack result = temp.getItem(0).copy();
            if (!resultFits.test(result)) return null;
            tx.commit();
            return result;
        }
    }

    private boolean canFit(int slot, ItemStack stack)
    {
        ItemStack current = items.get(slot);
        if (current.isEmpty()) return true;
        return ItemStack.isSameItemSameComponents(current, stack)
                && current.getCount() + stack.getCount() <= current.getMaxStackSize();
    }

    private boolean canTakeFluid(FluidStack fluid)
    {
        if (fluid.isEmpty()) return true;
        try (Transaction tx = Transaction.openRoot()) // never committed, so this is just a test
        {
            return productTank.insert(FluidResource.of(fluid), fluid.getAmount(), tx) == fluid.getAmount();
        }
    }

    private void useSolvent(ChemistryRecipe recipe)
    {
        recipe.solvent().ifPresent(solvent -> {
            try (Transaction tx = Transaction.openRoot())
            {
                solventTank.extract(tanks.getResource(SOLVENT_TANK), solvent.amount(), tx);
                tx.commit();
            }
        });
    }

    private void addFluidResult(ChemistryRecipe recipe)
    {
        FluidStack fluid = recipe.fluidResult();
        if (fluid.isEmpty()) return;
        try (Transaction tx = Transaction.openRoot())
        {
            productTank.insert(FluidResource.of(fluid), fluid.getAmount(), tx);
            tx.commit();
        }
    }
}
