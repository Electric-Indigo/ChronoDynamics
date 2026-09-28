package io.github.electricindigo.research.blueprint;

import io.github.electricindigo.block.researchdesk.ResearchDeskMenu;
import io.github.electricindigo.registry.ModAttachments;
import io.github.electricindigo.registry.ModDataComponents;
import io.github.electricindigo.registry.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Prediction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;

public final class BlueprintManager
{
    private BlueprintManager() {}

    // ---------- General ----------

    public static boolean isUnlocked(Player player, Blueprint blueprint)
    {
        return blueprint.research() == null || player.getData(ModAttachments.RESEARCH).has(blueprint.research());
    }

    // Which blueprint an item holds, or null if it isn't a filled blueprint
    public static @Nullable Blueprint fromStack(ItemStack stack)
    {
        if (!stack.is(ModItems.BLUEPRINT.get())) return null;
        String id = stack.get(ModDataComponents.BLUEPRINT_ID.get());
        return id == null ? null : Blueprints.get(id);
    }

    // Counts an item in the main inventory and hotbar (not armor or offhand)
    public static int count(Player player, Item item)
    {
        Inventory inventory = player.getInventory();
        int total = 0;
        for (int i = 0; i < Inventory.INVENTORY_SIZE; i++)
        {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(item)) total += stack.getCount();
        }
        return total;
    }

    // ---------- Drafting ----------

    // Everything drafting needs from the inventory (the paper is checked separately, it's in the desk)
    public static boolean canDraft(Player player, Blueprint blueprint)
    {
        return isUnlocked(player, blueprint)
                && count(player, ModItems.DRAFTING_INK.get()) >= 1
                && count(player, blueprint.draftItem().get()) >= 1;
    }

    public static ItemStack createBlueprint(Blueprint blueprint)
    {
        ItemStack stack = new ItemStack(ModItems.BLUEPRINT.get());
        stack.set(ModDataComponents.BLUEPRINT_ID.get(), blueprint.id());
        return stack;
    }

    public static void tryDraft(ServerPlayer player, String id)
    {
        if (!(player.containerMenu instanceof ResearchDeskMenu menu)) return;

        Blueprint blueprint = Blueprints.get(id);
        if (blueprint == null) return;

        Container workbench = menu.getWorkbench();
        ItemStack paper = workbench.getItem(ResearchDeskMenu.BLUEPRINT_SLOT);
        if (!paper.is(Items.PAPER)) return;
        if (!canDraft(player, blueprint)) return;

        remove(player, ModItems.DRAFTING_INK.get(), 1);
        remove(player, blueprint.draftItem().get(), 1);
        paper.shrink(1);

        ItemStack drafted = createBlueprint(blueprint);
        if (paper.isEmpty())
        {
            // Last sheet: the blueprint takes its place in the slot
            workbench.setItem(ResearchDeskMenu.BLUEPRINT_SLOT, drafted);
        }
        else
        {
            player.getInventory().placeItemBackInInventory(drafted, Prediction.SERVER_ONLY);
            workbench.setChanged();
        }

        player.level().playSound(null, player.blockPosition(), SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT,
                SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    // ---------- Assembling ----------

    // True when every part slot has enough of its part and the output has room
    public static boolean canAssemble(ResearchDeskMenu menu)
    {
        Blueprint blueprint = menu.getReadableBlueprint();
        if (blueprint == null) return false;

        Container workbench = menu.getWorkbench();
        for (int i = 0; i < blueprint.costs().size(); i++)
        {
            Blueprint.Cost cost = blueprint.costs().get(i);
            ItemStack inSlot = workbench.getItem(ResearchDeskMenu.FIRST_PART_SLOT + i);
            if (!inSlot.is(cost.item().get()) || inSlot.getCount() < cost.count()) return false;
        }
        return fits(workbench.getItem(ResearchDeskMenu.OUTPUT_SLOT), blueprint.result().get());
    }

    public static void tryAssemble(ServerPlayer player)
    {
        if (!(player.containerMenu instanceof ResearchDeskMenu menu)) return;
        if (!canAssemble(menu)) return;

        Blueprint blueprint = menu.getReadableBlueprint();
        Container workbench = menu.getWorkbench();

        for (int i = 0; i < blueprint.costs().size(); i++)
        {
            workbench.getItem(ResearchDeskMenu.FIRST_PART_SLOT + i).shrink(blueprint.costs().get(i).count());
        }

        ItemStack result = blueprint.result().get();
        ItemStack out = workbench.getItem(ResearchDeskMenu.OUTPUT_SLOT);
        if (out.isEmpty()) workbench.setItem(ResearchDeskMenu.OUTPUT_SLOT, result);
        else out.grow(result.getCount());
        workbench.setChanged();

        player.level().playSound(null, player.blockPosition(), SoundEvents.SMITHING_TABLE_USE,
                SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private static boolean fits(ItemStack out, ItemStack result)
    {
        if (out.isEmpty()) return true;
        return ItemStack.isSameItemSameComponents(out, result)
                && out.getCount() + result.getCount() <= out.getMaxStackSize();
    }

    private static void remove(Player player, Item item, int amount)
    {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < Inventory.INVENTORY_SIZE && amount > 0; i++)
        {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(item))
            {
                int taken = Math.min(amount, stack.getCount());
                stack.shrink(taken);
                amount -= taken;
            }
        }
    }
}
