package io.github.electricindigo.block.chemistrybench;

import io.github.electricindigo.block.TwoPartDeskBlock;
import io.github.electricindigo.block.researchdesk.ResearchDeskBlockEntity;
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

public class ChemistryBenchBlock extends TwoPartDeskBlock
{

    public ChemistryBenchBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    protected BlockEntity createPrimaryEntity(BlockPos pos, BlockState state)
    {
        return new ChemistryBenchBlockEntity(pos, state);
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
