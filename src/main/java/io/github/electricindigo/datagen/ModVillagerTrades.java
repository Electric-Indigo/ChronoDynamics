package io.github.electricindigo.datagen;

import io.github.electricindigo.ChronoDynamics;
import io.github.electricindigo.registry.ModItems;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;

public class ModVillagerTrades
{
    public static final ResourceKey<VillagerTrade> LIBRARIAN_EFD =
            ResourceKey.create(Registries.VILLAGER_TRADE, Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, "librarian_efd"));

    public static void bootstrap(BootstrapContext<VillagerTrade> context)
    {
        context.register(LIBRARIAN_EFD,
                VillagerTrade.builder(
                        new TradeCost(Items.EMERALD, 12),
                        new ItemStackTemplate(ModItems.EFD_ITEM.get()),
                        4, 10, 0.05F)
                        .build());
    }

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.VILLAGER_TRADE, ModVillagerTrades::bootstrap);
}
