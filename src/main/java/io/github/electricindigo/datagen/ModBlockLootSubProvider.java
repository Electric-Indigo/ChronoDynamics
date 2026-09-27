package io.github.electricindigo.datagen;

import io.github.electricindigo.registry.ModBlocks;
import io.github.electricindigo.block.researchdesk.DeskPart;
import io.github.electricindigo.block.researchdesk.ResearchDeskBlock;

import io.github.electricindigo.registry.ModItems;
import net.minecraft.data.loot.BlockLootSubProvider;

import net.minecraft.data.loot.LootTableSubProvider;

import net.minecraft.world.flag.FeatureFlags;

import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.Set;

public class ModBlockLootSubProvider extends BlockLootSubProvider
{

    public ModBlockLootSubProvider(LootTableSubProvider.Context context)
    {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, context);
    }

    @Override
    protected Iterable<Block> getKnownBlocks()
    {
        return ModBlocks.BLOCKS.getEntries()
                .stream()
                .map(e -> (Block) e.value())
                .toList();
    }

    @Override
    protected void generate()
    {
        this.add(ModBlocks.RESEARCH_DESK.get(),
                createSinglePropConditionTable(ModBlocks.RESEARCH_DESK.get(), ResearchDeskBlock.PART, DeskPart.PRIMARY)
               );

        this.add(ModBlocks.CHRONITE_CLUSTER.get(),
                block -> this.createSilkTouchDispatchTable(block,
                        this.applyExplosionDecay(block,
                                LootItem.lootTableItem(ModItems.CHRONITE_CRYSTAL.get())
                                        .apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 2)))
                                        .apply(ApplyBonusCount.addOreBonusCount(this.enchantments.getOrThrow(Enchantments.FORTUNE)))))
        );

        this.dropSelf(ModBlocks.CHEMISTRY_BENCH.get());
    }
}
