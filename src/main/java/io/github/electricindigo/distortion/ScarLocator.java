package io.github.electricindigo.distortion;

import io.github.electricindigo.ChronoDynamics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jspecify.annotations.Nullable;

public final class ScarLocator
{
    private ScarLocator(){}

    public static final ResourceKey<Structure> RIFT_SCAR =
            ResourceKey.create(Registries.STRUCTURE, Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, "rift_scar"));

    private static final int CHUNK_RANGE = 2;

    public static @Nullable BoundingBox findNearestScar(ServerLevel level, BlockPos pos)
    {
        Structure scar = level.registryAccess().lookupOrThrow(Registries.STRUCTURE).getValue(RIFT_SCAR);
        if (scar == null) return null;

        StructureManager structures = level.structureManager();
        int chunkX = SectionPos.blockToSectionCoord(pos.getX());
        int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());

        BoundingBox nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (int dx = -CHUNK_RANGE; dx <= CHUNK_RANGE; dx++)
        {
            for (int dz = -CHUNK_RANGE; dz <= CHUNK_RANGE; dz++)
            {
                for (StructureStart start : structures.startsForStructure(chunkX + dx, chunkZ + dz, scar))
                {
                    BoundingBox box = start.getBoundingBox();
                    double distance = distanceToBox(box, pos);
                    if (distance < nearestDistance)
                    {
                        nearestDistance = distance;
                        nearest = box;
                    }
                }
            }
        }
        return nearest;
    }

    public static double distanceToBox(BoundingBox box, BlockPos pos)
    {
        int dx = Math.max(0, Math.max(box.minX() - pos.getX(), pos.getX() - box.maxX()));
        int dy = Math.max(0, Math.max(box.minY() - pos.getY(), pos.getY() - box.maxY()));
        int dz = Math.max(0, Math.max(box.minZ() - pos.getZ(), pos.getZ() - box.maxZ()));
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
