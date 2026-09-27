package io.github.electricindigo.registry;

import io.github.electricindigo.ChronoDynamics;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems
{
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ChronoDynamics.MODID);


    public static final DeferredItem<Item> EFD_ITEM = ITEMS.registerSimpleItem("e_for_d",
            properties -> properties.stacksTo(1));

    public static final DeferredItem<Item> COMPUTER_UPGRADE = ITEMS.registerSimpleItem("computer_upgrade",
            properties -> properties.stacksTo(1));

    public static final DeferredItem<Item> CHRONITE_CRYSTAL = ITEMS.registerSimpleItem("chronite_crystal",
            properties -> properties.rarity(Rarity.RARE));

    public static final DeferredItem<BlockItem> CHRONITE_CLUSTER_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.CHRONITE_CLUSTER);

    public static final DeferredItem<BlockItem> RESEARCH_DESK_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.RESEARCH_DESK);


    public static void register(IEventBus modEventBus)
    {
        ITEMS.register(modEventBus);
    }
}
