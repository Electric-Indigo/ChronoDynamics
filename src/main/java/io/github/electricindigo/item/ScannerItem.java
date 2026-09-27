package io.github.electricindigo.item;

import io.github.electricindigo.registry.ModAttachments;
import io.github.electricindigo.registry.ModBlocks;
import io.github.electricindigo.registry.ModDataComponents;
import io.github.electricindigo.research.ResearchData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class ScannerItem extends Item
{
    private static final int RADIUS = 12;
    private static final int MIN_Y = -64;
    private static final int MAX_Y = -40;
    private static final int FUEL_PER_REDSTONE = 600;
    private static final int SCAN_EVERY = 4;

    public ScannerItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        if (!(owner instanceof Player player)) return;
        if (!itemStack.getOrDefault(ModDataComponents.SCANNER_ACTIVE.get(), false)) return;

        if (player.getMainHandItem() != itemStack && player.getOffhandItem() != itemStack) return;

        long time = level.getGameTime();
        if (time % SCAN_EVERY != 0) return;

        int fuel = itemStack.getOrDefault(ModDataComponents.SCANNER_FUEL.get(), 0);
        if (fuel <= 0)
        {
            if (!player.isCreative() && !useRedstone(player))
            {
                itemStack.set(ModDataComponents.SCANNER_ACTIVE.get(), false);
                player.sendOverlayMessage(Component.literal("Scanner needs redstone!"));
                return;
            }
            fuel = FUEL_PER_REDSTONE;
        }
        itemStack.set(ModDataComponents.SCANNER_FUEL.get(), fuel - SCAN_EVERY);

        double nearest = findNearestCluster(level, player);
        if (nearest < 0) return;

        player.sendOverlayMessage(Component.literal("Drift signal: " + Math.round(nearest)));

        int interval = SCAN_EVERY * (1 + (int) Math.round(4 * nearest / RADIUS));
        if (time % interval == 0)
        {
            float closeness = 1.0F - (float) (nearest / RADIUS);
            float pitch = 0.5F + 1.5F * Math.max(0.0F, closeness);
            level.playSound(null, player, SoundEvents.NOTE_BLOCK_PLING, SoundSource.PLAYERS, 1.0F, pitch);
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || !newStack.is(oldStack.getItem());
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand)
    {
        if (level.isClientSide())
        {
            return InteractionResult.SUCCESS;
        }

        ResearchData research = player.getData(ModAttachments.RESEARCH);
        if (!research.has("chronometry"))
        {
            player.sendOverlayMessage(Component.literal("Readings unintelligible..."));
            return InteractionResult.CONSUME;
        }

        ItemStack stack = player.getItemInHand(hand);
        boolean active = stack.getOrDefault(ModDataComponents.SCANNER_ACTIVE.get(), false);
        stack.set(ModDataComponents.SCANNER_ACTIVE.get(), !active);

        player.sendOverlayMessage(Component.literal(active ? "Scanner off" : "Scanner on"));
        return InteractionResult.CONSUME;
    }

    private static boolean useRedstone(Player player)
    {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++)
        {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(Items.REDSTONE))
            {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }

    private static double findNearestCluster(Level level, Player player)
    {
        BlockPos center = player.blockPosition();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        double best = -1;

        for (int dx = -RADIUS; dx <= RADIUS; dx++)
        {
            for (int dz = -RADIUS; dz <= RADIUS; dz++)
            {
                if (dx * dx + dz * dz > RADIUS * RADIUS) continue;

                for (int y = MIN_Y; y <= MAX_Y; y++)
                {
                    pos.set(center.getX() + dx, y, center.getZ() + dz);
                    if (level.getBlockState(pos).is(ModBlocks.CHRONITE_CLUSTER.get()))
                    {
                        double distX = (pos.getX() + 0.5) - player.getX();
                        double distZ = (pos.getZ() + 0.5) - player.getZ();
                        double dist = Math.sqrt(distX * distX + distZ * distZ);
                        if (best < 0 || dist < best)
                        {
                            best = dist;
                        }
                    }
                }
            }
        }
        return best;
    }
}
