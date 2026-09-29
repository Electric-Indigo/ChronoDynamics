package io.github.electricindigo.distortion;

import io.github.electricindigo.registry.ModAttachments;
import io.github.electricindigo.registry.ModBlocks;
import io.github.electricindigo.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.CanContinueSleepingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public final class SicknessManager
{
    private SicknessManager(){}

    // Gain per second from each source. Starting numbers, tune in play.
    private static final int GAIN_SCAR_EDGE = 1;
    private static final int GAIN_HOLD_CRYSTAL = 1;
    private static final int GAIN_SCAR_INSIDE = 3;
    private static final int GAIN_NEAR_CLUSTER = 4;
    private static final int GAIN_HOLD_CLUSTER = 5;
    private static final int DRAIN = 2; // per second when there are no sources

    private static final int CLUSTER_RADIUS = 6; // blocks, in a cube around the player

    private static final double SCAR_INSIDE_RANGE = 12;
    private static final double SCAR_EDGE_RANGE = 16;

    private static final Component TIME_FLUX =
            Component.literal("Time is in flux...");

    public static void onPlayerTick(PlayerTickEvent.Post event)
    {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.tickCount % 20 != 0) return; // once a second

        int gain = 0;

        if (isCarrying(player, ModItems.CHRONITE_CRYSTAL.get())) gain += GAIN_HOLD_CRYSTAL;
        if (isCarrying(player, ModItems.CHRONITE_CLUSTER_ITEM.get())) gain += GAIN_HOLD_CLUSTER;
        if (isNearCluster(player.level(), player.blockPosition())) gain += GAIN_NEAR_CLUSTER;

        BoundingBox scar = player.level() instanceof ServerLevel level ? ScarLocator.findNearestScar(level, player.blockPosition()) : null;
        Optional<BoundingBox> nearestScar = Optional.ofNullable(scar);
        if (!nearestScar.equals(player.getData(ModAttachments.NEAREST_SCAR)))
        {
            player.setData(ModAttachments.NEAREST_SCAR, nearestScar); // only syncs when it changes
        }
        gain += scarGain(scar, player.blockPosition());

        TemporalSickness current = player.getData(ModAttachments.TEMPORAL_SICKNESS);
        int next = gain > 0 ? current.value() + gain : current.value() - DRAIN;

        TemporalSickness updated = new TemporalSickness(next); // clamps to 0-100
        if (updated.value() != current.value())
        {
            player.setData(ModAttachments.TEMPORAL_SICKNESS, updated); // syncs to the client
            player.sendOverlayMessage(Component.literal("Sickness: " + updated.value() + " (" + updated.stage() + ")"));
        }
    }

    private static boolean isCarrying(ServerPlayer player, Item item)
    {
        return player.getOffhandItem().is(item) || player.getInventory().contains(stack -> stack.is(item));
    }

    private static boolean isNearCluster(Level level, BlockPos center)
    {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -CLUSTER_RADIUS; dx <= CLUSTER_RADIUS; dx++)
        {
            for (int dy = -CLUSTER_RADIUS; dy <= CLUSTER_RADIUS; dy++)
            {
                for (int dz = -CLUSTER_RADIUS; dz <= CLUSTER_RADIUS; dz++)
                {
                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    if (level.getBlockState(pos).is(ModBlocks.CHRONITE_CLUSTER.get())) return true;
                }
            }
        }
        return false;
    }

    public static void onCanContinueSleeping(CanContinueSleepingEvent event)
    {
        if (!(event.getEntity() instanceof ServerPlayer player)) return; // villagers sleep too
        if (!event.mayContinueSleeping()) return;                         // vanilla already woke them

        SicknessStage stage = player.getData(ModAttachments.TEMPORAL_SICKNESS).stage();
        if (stage != SicknessStage.NONE)
        {
            event.setContinueSleeping(false);
            player.sendOverlayMessage(TIME_FLUX);
        }
    }

    private static int scarGain(@Nullable BoundingBox scar, BlockPos pos)
    {
        if (scar == null) return 0;

        double distance = ScarLocator.distanceToBox(scar, pos);
        if (distance <= SCAR_INSIDE_RANGE) return GAIN_SCAR_INSIDE;
        if (distance <= SCAR_EDGE_RANGE) return GAIN_SCAR_EDGE;
        return 0;
    }
}
