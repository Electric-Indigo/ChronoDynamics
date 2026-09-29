package io.github.electricindigo.client;

import io.github.electricindigo.ChronoDynamics;
import io.github.electricindigo.ModClientConfig;
import io.github.electricindigo.distortion.SicknessStage;
import io.github.electricindigo.registry.ModAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RetroBlocks
{
    private RetroBlocks() {}

    // How far past the scar's edge placed blocks are affected
    private static final int ZONE_RADIUS = 16;

    // Chunk rebuilds take a moment, so once blocks go retro they stay that way at least this long
    private static final int MIN_RETRO_TICKS = 10;

    // Programmer Art gives these blocks different shapes, so a texture swap would look wrong on them
    private static final Set<String> SKIP_BLOCKS = Set.of("repeater", "comparator", "brewing_stand", "dragon_egg", "cocoa");

    // Written on the main thread, read on chunk-meshing threads
    private static volatile @Nullable BoundingBox scarZone = null;
    private static volatile @Nullable BoundingBox bubble = null;
    private static volatile boolean retro = false;
    private static long holdRetroUntil = 0;

    // Caches, rebuilt after every resource reload
    private static final Map<TextureAtlasSprite, TextureAtlasSprite> RETRO_SPRITES = new ConcurrentHashMap<>();
    private static final Map<BlockStateModelPart, BlockStateModelPart> RETRO_PARTS = new ConcurrentHashMap<>();

    // ---------- Setup ----------

    // Wraps every vanilla block model after baking so it can switch to retro textures
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event)
    {
        RETRO_SPRITES.clear();
        RETRO_PARTS.clear();

        event.getBakingResult().blockStateModels().replaceAll((state, model) ->
                shouldWrap(state) ? new RetroBlockModel(model) : model);
    }

    private static boolean shouldWrap(BlockState state)
    {
        Identifier id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (!id.getNamespace().equals("minecraft")) return false;
        String path = id.getPath();
        return !path.contains("torch") && !SKIP_BLOCKS.contains(path);
    }

    // ---------- Each tick: where is the zone, and is it retro right now? ----------

    public static void onClientTick(ClientTickEvent.Post event)
    {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (mc.player == null || level == null)
        {
            scarZone = null;
            bubble = null;
            retro = false;
            return;
        }

        // The area around the nearest scar
        BoundingBox newScarZone = mc.player.getData(ModAttachments.NEAREST_SCAR)
                .map(scar -> scar.inflatedBy(ZONE_RADIUS))
                .orElse(null);

        // At Strong sickness, a bubble around the player as well
        SicknessStage stage = mc.player.getData(ModAttachments.TEMPORAL_SICKNESS).stage();
        int bubbleRadius = ModClientConfig.RETRO_BUBBLE_RADIUS.get();
        BoundingBox newBubble = stage == SicknessStage.STRONG && bubbleRadius > 0
                ? new BoundingBox(mc.player.blockPosition()).inflatedBy(bubbleRadius)
                : null;

        boolean anyZone = newScarZone != null || newBubble != null;
        long now = level.getGameTime();
        if (anyZone && RetroFlicker.showRetro())
        {
            holdRetroUntil = Math.max(holdRetroUntil, now + MIN_RETRO_TICKS);
        }
        boolean newRetro = anyZone && now < holdRetroUntil;

        BoundingBox oldScarZone = scarZone;
        BoundingBox oldBubble = bubble;
        boolean wasRetro = retro;
        if (newRetro == wasRetro && Objects.equals(newScarZone, oldScarZone) && Objects.equals(newBubble, oldBubble)) return;

        scarZone = newScarZone;
        bubble = newBubble;
        retro = newRetro;

        // Redraw the chunks that are changing look
        if (wasRetro)
        {
            markDirty(level, oldScarZone);
            markDirty(level, oldBubble);
        }
        if (newRetro)
        {
            markDirty(level, newScarZone);
            markDirty(level, newBubble);
        }
    }

    private static void markDirty(ClientLevel level, @Nullable BoundingBox box)
    {
        if (box == null) return;
        level.setSectionRangeDirty(
                SectionPos.blockToSectionCoord(box.minX()), SectionPos.blockToSectionCoord(box.minY()), SectionPos.blockToSectionCoord(box.minZ()),
                SectionPos.blockToSectionCoord(box.maxX()), SectionPos.blockToSectionCoord(box.maxY()), SectionPos.blockToSectionCoord(box.maxZ()));
    }

    // ---------- Used by RetroBlockModel on meshing threads ----------

    public static boolean isRetroAt(BlockPos pos)
    {
        if (!retro) return false;
        BoundingBox scar = scarZone;
        BoundingBox around = bubble;
        return (scar != null && scar.isInside(pos)) || (around != null && around.isInside(pos));
    }

    public static BlockStateModelPart retroPart(BlockStateModelPart part)
    {
        return RETRO_PARTS.computeIfAbsent(part, RetroBlockPart::new);
    }

    static List<BakedQuad> retroQuads(List<BakedQuad> quads)
    {
        if (quads.isEmpty()) return quads;
        List<BakedQuad> result = new ArrayList<>(quads.size());
        for (BakedQuad quad : quads) result.add(retroQuad(quad));
        return List.copyOf(result);
    }

    // Same quad, same shape, but pointing at the retro sprite's spot on the block atlas
    private static BakedQuad retroQuad(BakedQuad quad)
    {
        BakedQuad.MaterialInfo info = quad.materialInfo();
        TextureAtlasSprite from = info.sprite();
        TextureAtlasSprite to = retroSprite(from);
        if (to == from) return quad;

        BakedQuad.MaterialInfo retroInfo = new BakedQuad.MaterialInfo(
                to, info.layer(), info.itemRenderType(), info.itemGlintRenderType(), info.itemGlintSpecialRenderType(),
                info.tintIndex(), info.shadeDirectionOverride(), info.lightEmission(), info.ambientOcclusion());

        return new BakedQuad(
                quad.position0(), quad.position1(), quad.position2(), quad.position3(),
                moveUV(quad.packedUV0(), from, to), moveUV(quad.packedUV1(), from, to),
                moveUV(quad.packedUV2(), from, to), moveUV(quad.packedUV3(), from, to),
                quad.direction(), retroInfo, quad.bakedNormals(), quad.bakedColors());
    }

    // A UV inside the old sprite -> the same relative spot inside the retro sprite
    private static long moveUV(long packedUV, TextureAtlasSprite from, TextureAtlasSprite to)
    {
        float u = UVPair.unpackU(packedUV);
        float v = UVPair.unpackV(packedUV);
        float relU = (u - from.getU0()) / (from.getU1() - from.getU0());
        float relV = (v - from.getV0()) / (from.getV1() - from.getV0());
        return UVPair.pack(
                to.getU0() + relU * (to.getU1() - to.getU0()),
                to.getV0() + relV * (to.getV1() - to.getV0()));
    }

    // minecraft:block/stone -> chronodynamics:retro/block/stone, if that texture exists (Programmer Art or hand-drawn)
    private static TextureAtlasSprite retroSprite(TextureAtlasSprite sprite)
    {
        return RETRO_SPRITES.computeIfAbsent(sprite, s ->
        {
            Identifier id = s.contents().name();
            if (!id.getNamespace().equals("minecraft") || !id.getPath().startsWith("block/")) return s;

            TextureAtlas atlas = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS);
            TextureAtlasSprite retroSprite = atlas.getSprite(Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, "retro/" + id.getPath()));
            return retroSprite == atlas.missingSprite() ? s : retroSprite;
        });
    }
}
