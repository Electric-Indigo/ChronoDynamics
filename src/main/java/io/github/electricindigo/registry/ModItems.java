package io.github.electricindigo.registry;

import io.github.electricindigo.ChronoDynamics;
import io.github.electricindigo.item.BlueprintItem;
import io.github.electricindigo.item.ScannerItem;
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

    public static final DeferredItem<ScannerItem> SCANNER_ITEM = ITEMS.registerItem("scanner",
            ScannerItem::new, properties -> properties.stacksTo(1));

    public static final DeferredItem<BlueprintItem> BLUEPRINT = ITEMS.registerItem("blueprint",
            BlueprintItem::new, properties -> properties.stacksTo(1));

    public static final DeferredItem<Item> CONDUCTIVE_REDSTONE_PASTE = ITEMS.registerSimpleItem("conductive_redstone_paste");
    public static final DeferredItem<Item> BURNT_ASH = ITEMS.registerSimpleItem("burnt_ash");
    public static final DeferredItem<Item> DRAFTING_INK = ITEMS.registerSimpleItem("drafting_ink");
    public static final DeferredItem<Item> PURIFIED_QUARTZ = ITEMS.registerSimpleItem("purified_quartz");
    public static final DeferredItem<Item> RESONANT_AMETHYST = ITEMS.registerSimpleItem("resonant_amethyst");

    public static final DeferredItem<Item> QUARTZ_OSCILLATOR = ITEMS.registerSimpleItem("quartz_oscillator");
    public static final DeferredItem<Item> DRIFT_SENSOR = ITEMS.registerSimpleItem("drift_sensor");
    public static final DeferredItem<Item> CIRCUIT_BOARD = ITEMS.registerSimpleItem("circuit_board");



    public static final DeferredItem<BlockItem> CHRONITE_CLUSTER_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.CHRONITE_CLUSTER);
    public static final DeferredItem<BlockItem> RESEARCH_DESK_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.RESEARCH_DESK);
    public static final DeferredItem<BlockItem> CHEMISTRY_BENCH_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.CHEMISTRY_BENCH);


    public static void register(IEventBus modEventBus)
    {
        ITEMS.register(modEventBus);
    }
}
