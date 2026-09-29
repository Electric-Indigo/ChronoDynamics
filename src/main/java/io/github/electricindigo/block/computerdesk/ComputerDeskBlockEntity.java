package io.github.electricindigo.block.computerdesk;

import io.github.electricindigo.block.researchdesk.ResearchDeskMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class ComputerDeskBlockEntity extends BlockEntity implements MenuProvider
{
    public ComputerDeskBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState)
    {
        super(type, worldPosition, blockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.chronodynamics.computer_desk");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player)
    {
        return new ComputerDeskMenu(i, inventory);
    }
}
