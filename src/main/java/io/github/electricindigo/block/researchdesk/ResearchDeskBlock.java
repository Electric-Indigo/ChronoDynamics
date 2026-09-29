package io.github.electricindigo.block.researchdesk;

import io.github.electricindigo.block.TwoPartDeskBlock;
import io.github.electricindigo.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class ResearchDeskBlock extends TwoPartDeskBlock
{
    public ResearchDeskBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    protected BlockEntity createPrimaryEntity(BlockPos pos, BlockState state)
    {
        return new ResearchDeskBlockEntity(ModBlockEntities.RESEARCH_DESK.get(), pos, state);
    }
}
