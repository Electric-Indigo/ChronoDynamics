package io.github.electricindigo.client;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

final class RetroBlockPart implements BlockStateModelPart
{
    private final BlockStateModelPart original;
    private final Map<Direction, List<BakedQuad>> sided = new EnumMap<>(Direction.class);
    private final List<BakedQuad> unsided;

    RetroBlockPart(BlockStateModelPart original)
    {
        this.original = original;
        for (Direction direction : Direction.values())
        {
            sided.put(direction, RetroBlocks.retroQuads(original.getQuads(direction)));
        }
        this.unsided = RetroBlocks.retroQuads(original.getQuads(null));
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable Direction direction)
    {
        return direction == null ? unsided : sided.get(direction);
    }

    @Override
    @Deprecated
    public boolean useAmbientOcclusion()
    {
        return original.useAmbientOcclusion();
    }

    @Override
    public TriState ambientOcclusion()
    {
        return original.ambientOcclusion();
    }

    @Override
    public Material.Baked particleMaterial()
    {
        return original.particleMaterial();
    }

    @Override
    public int materialFlags()
    {
        return original.materialFlags();
    }
}
