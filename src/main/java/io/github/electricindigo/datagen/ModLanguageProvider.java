package io.github.electricindigo.datagen;

import io.github.electricindigo.ChronoDynamics;
import io.github.electricindigo.registry.ModBlocks;
import io.github.electricindigo.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider
{

    public ModLanguageProvider(PackOutput output) {
        super(output, ChronoDynamics.MODID, "en_us");
    }

    @Override
    protected void addTranslations()
    {
        addItem(ModItems.EFD_ITEM, "Electronics for Dummies");
        addItem(ModItems.COMPUTER_UPGRADE, "Computer Upgrade");
        addItem(ModItems.CHRONITE_CRYSTAL, "Chronite Crystal");
        addItem(ModItems.SCANNER_ITEM, "Chronite Scanner");
        addItem(ModItems.CONDUCTIVE_REDSTONE_PASTE, "Conductive Redstone Paste");
        addItem(ModItems.BURNT_ASH, "Burnt Ash");
        addItem(ModItems.BLUEPRINT, "Blueprint");
        addItem(ModItems.DRAFTING_INK, "Drafting Ink");
        addItem(ModItems.CIRCUIT_BOARD, "Circuit Board");
        addItem(ModItems.DRIFT_SENSOR, "Drift Sensor");
        addItem(ModItems.PURIFIED_QUARTZ, "Purified Quartz");
        addItem(ModItems.QUARTZ_OSCILLATOR, "Quartz Oscillator");
        addItem(ModItems.RESONANT_AMETHYST, "Resonant Amethyst");

        addBlock(ModBlocks.CHRONITE_CLUSTER, "Chronite Cluster");
        addBlock(ModBlocks.RESEARCH_DESK, "Research Desk");
        addBlock(ModBlocks.CHEMISTRY_BENCH, "Chemistry Bench");
        addBlock(ModBlocks.COMPUTER_DESK, "Computer Desk");

        add("creativetab.chronodynamics.chronodynamics_tab", "Chrono Dynamics");
        add("item.chronodynamics.blueprint.named", "Blueprint: %s");

        add("chronodynamics.configuration.reduceRiftFlicker", "Reduce rift flicker");
        add("chronodynamics.configuration.disableTimeDistortion", "Disable time distortion");
        add("chronodynamics.configuration.retroBubbleRadius", "Retro bubble radius");
    }
}
