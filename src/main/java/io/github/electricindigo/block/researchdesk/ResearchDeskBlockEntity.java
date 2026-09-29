package io.github.electricindigo.block.researchdesk;

import io.github.electricindigo.registry.ModBlockEntities;
import io.github.electricindigo.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class ResearchDeskBlockEntity extends BaseContainerBlockEntity
{
    // Change this one number to resize the drawer
    public static final int DRAWER_SIZE = 9;

    private NonNullList<ItemStack> drawer = NonNullList.withSize(DRAWER_SIZE, ItemStack.EMPTY);

    public ResearchDeskBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState)
    {
        super(type, worldPosition, blockState);
    }

    @Override
    protected Component getDefaultName()
    {
        return Component.translatable("block.chronodynamics.research_desk");
    }

    @Override
    protected NonNullList<ItemStack> getItems()
    {
        return drawer;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items)
    {
        this.drawer = items;
    }

    @Override
    public int getContainerSize()
    {
        return DRAWER_SIZE;
    }

    // Blueprints only, including from hoppers
    @Override
    public boolean canPlaceItem(int slot, ItemStack stack)
    {
        return stack.is(ModItems.BLUEPRINT.get());
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory)
    {
        return new ResearchDeskMenu(containerId, inventory, this);
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        drawer = NonNullList.withSize(DRAWER_SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, drawer);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, drawer);
    }
}
