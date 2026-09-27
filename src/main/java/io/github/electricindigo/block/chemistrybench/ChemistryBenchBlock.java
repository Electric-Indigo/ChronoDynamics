package io.github.electricindigo.block.chemistrybench;

import io.github.electricindigo.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class ChemistryBenchBlock extends Block implements EntityBlock
{

    public ChemistryBenchBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        return new ChemistryBenchBlockEntity(blockPos, blockState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide())
        {
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof ChemistryBenchBlockEntity bench)
        {
            player.openMenu(bench);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        if (level.isClientSide() || type != ModBlockEntities.CHEMISTRY_BENCH.get())
        {
            return null;
        }
        return (lvl, pos, st, be) -> ChemistryBenchBlockEntity.serverTick(lvl, pos, st, (ChemistryBenchBlockEntity) be);
    }
}
