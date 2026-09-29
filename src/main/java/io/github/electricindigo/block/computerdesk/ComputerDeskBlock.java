package io.github.electricindigo.block.computerdesk;

import io.github.electricindigo.block.TwoPartDeskBlock;
import io.github.electricindigo.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ComputerDeskBlock extends TwoPartDeskBlock
{

    public ComputerDeskBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    protected BlockEntity createPrimaryEntity(BlockPos pos, BlockState state) {
        return new ComputerDeskBlockEntity(ModBlockEntities.COMPUTER_DESK.get(), pos, state);
    }
}
