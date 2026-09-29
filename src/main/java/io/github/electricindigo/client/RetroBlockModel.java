package io.github.electricindigo.client;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;

import java.util.List;

public class RetroBlockModel extends DelegateBlockStateModel
{
    public RetroBlockModel(BlockStateModel delegate)
    {
        super(delegate);
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts)
    {
        // Held items, falling blocks and the break overlay pass no real world; only chunk meshing does
        if (level == BlockAndTintGetter.EMPTY || !RetroBlocks.isRetroAt(pos))
        {
            super.collectParts(level, pos, state, random, parts);
            return;
        }

        int start = parts.size();
        super.collectParts(level, pos, state, random, parts);
        for (int i = start; i < parts.size(); i++)
        {
            parts.set(i, RetroBlocks.retroPart(parts.get(i)));
        }
    }
}
