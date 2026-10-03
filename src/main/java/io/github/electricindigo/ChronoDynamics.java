package io.github.electricindigo;

import io.github.electricindigo.block.chemistrybench.ChemistryBenchScreen;
import io.github.electricindigo.block.computerdesk.ComputerDeskScreen;
import io.github.electricindigo.distortion.SicknessManager;
import io.github.electricindigo.registry.*;
import io.github.electricindigo.block.researchdesk.ResearchDeskScreen;
import io.github.electricindigo.command.DebugPuzzleCommand;
import io.github.electricindigo.command.DebugWaveformCommand;
import io.github.electricindigo.datagen.*;
import io.github.electricindigo.network.ModNetworking;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

import java.util.List;
import java.util.Set;

@Mod(ChronoDynamics.MODID)
public class ChronoDynamics
{
    public static final String MODID = "chronodynamics";

    public ChronoDynamics(IEventBus modEventBus, ModContainer modContainer)
    {
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(SicknessManager::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(SicknessManager::onCanContinueSleeping);
        modEventBus.addListener(this::onGatherDataClient);
        modEventBus.addListener(this::onRegisterMenuScreens);

        ModNetworking.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModFluids.register(modEventBus);
        ModFluidTypes.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModAttachments.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModRecipes.register(modEventBus);
    }

    public void onRegisterMenuScreens(RegisterMenuScreensEvent event)
    {
        event.register(ModMenuTypes.RESEARCH_DESK_MENU.get(), ResearchDeskScreen::new);
        event.register(ModMenuTypes.CHEMISTRY_BENCH_MENU.get(), ChemistryBenchScreen::new);
        event.register(ModMenuTypes.COMPUTER_DESK_MENU.get(), ComputerDeskScreen::new);
    }

    private void onRegisterCommands(RegisterCommandsEvent event)
    {
        DebugPuzzleCommand.register(event.getDispatcher());
        DebugWaveformCommand.register(event.getDispatcher());
    }

    private void onGatherDataClient(GatherDataEvent.Client event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        generator.addProvider(true, new ModLanguageProvider(output));
        generator.addProvider(true, new ModModelProvider(output));

      //  event.createProvider(ModFluidTagsProvider::new);

        event.createBlockAndItemTags(ModBlockTagsProvider::new, ((output1, lookupProvider, contentsGetter) -> new ModItemTagsProvider(output1, lookupProvider)));

        event.createWorldRegistryObjects(new RegistrySetBuilder().add(Registries.VILLAGER_TRADE, ModVillagerTrades::bootstrap),
                Set.of(MODID)
        );
        event.createProvider((output2, lookupProvider) -> new ModVillagerTradeTagsProvider(output2, event.getWorldLookupProvider()));

        event.createReloadableRegistryObjects(new RegistrySetBuilder().add(Registries.LOOT_TABLE, new LootTableProvider(
                Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(ModBlockLootSubProvider::new, LootContextParamSets.BLOCK))
        )));
    }
}
