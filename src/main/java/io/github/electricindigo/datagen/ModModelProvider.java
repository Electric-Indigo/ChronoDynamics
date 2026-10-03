package io.github.electricindigo.datagen;

import com.mojang.math.Quadrant;
import io.github.electricindigo.ChronoDynamics;
import io.github.electricindigo.block.chemistrybench.ChemistryBenchBlock;
import io.github.electricindigo.block.computerdesk.ComputerDeskBlock;
import io.github.electricindigo.registry.ModBlocks;
import io.github.electricindigo.block.DeskPart;
import io.github.electricindigo.block.researchdesk.ResearchDeskBlock;
import io.github.electricindigo.registry.ModFluids;
import io.github.electricindigo.registry.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

public class ModModelProvider extends ModelProvider
{

    public ModModelProvider(PackOutput output) {
        super(output, ChronoDynamics.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels)
    {
        itemModels.generateFlatItem(ModItems.EFD_ITEM.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.COMPUTER_UPGRADE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CHRONITE_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.SCANNER_ITEM.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CONDUCTIVE_REDSTONE_PASTE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.BURNT_ASH.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.BLUEPRINT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.DRAFTING_INK.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CIRCUIT_BOARD.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.DRIFT_SENSOR.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.PURIFIED_QUARTZ.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.QUARTZ_OSCILLATOR.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.RESONANT_AMETHYST.get(), ModelTemplates.FLAT_ITEM);

        blockModels.createAmethystCluster(ModBlocks.CHRONITE_CLUSTER.get());
        itemModels.generateFlatItem(ModItems.CHRONITE_CLUSTER_ITEM.get(), ModelTemplates.FLAT_ITEM);

        for (ModFluids.ModFluid fluid : ModFluids.ALL)
        {
            itemModels.generateFlatItem(fluid.bucket().get(), ModelTemplates.FLAT_ITEM);
            blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(fluid.block().get(),
                    blockModels.plainVariant(Identifier.withDefaultNamespace("block/water"))));
        }

        //RESEARCH DESK//
        Identifier primaryResearchModel = ModelLocationUtils.getModelLocation(ModBlocks.RESEARCH_DESK.get(), "_primary");
        Identifier secondaryResearchModel = ModelLocationUtils.getModelLocation(ModBlocks.RESEARCH_DESK.get(), "_secondary");
        Identifier itemResearchModel = ModelLocationUtils.getModelLocation(ModItems.RESEARCH_DESK_ITEM.get());
        itemModels.itemModelOutput.accept(ModItems.RESEARCH_DESK_ITEM.get(), ItemModelUtils.plainModel(itemResearchModel));
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(ModBlocks.RESEARCH_DESK.get())
                        .with(PropertyDispatch.initial(ResearchDeskBlock.PART)
                                .select(DeskPart.PRIMARY, blockModels.plainVariant(primaryResearchModel))
                                .select(DeskPart.SECONDARY, blockModels.plainVariant(secondaryResearchModel)))
                        .with(PropertyDispatch.modify(ResearchDeskBlock.FACING)
                                .select(Direction.NORTH, variant -> variant)
                                .select(Direction.EAST, variant -> variant.withYRot(Quadrant.R90))
                                .select(Direction.SOUTH, variant -> variant.withYRot(Quadrant.R180))
                                .select(Direction.WEST, variant -> variant.withYRot(Quadrant.R270)))
        );

        //CHEMISTRY BENCH//
        Identifier primaryChemModel = ModelLocationUtils.getModelLocation(ModBlocks.CHEMISTRY_BENCH.get(), "_primary");
        Identifier secondaryChemModel = ModelLocationUtils.getModelLocation(ModBlocks.CHEMISTRY_BENCH.get(), "_secondary");
        Identifier itemChemModel = ModelLocationUtils.getModelLocation(ModItems.CHEMISTRY_BENCH_ITEM.get());
        itemModels.itemModelOutput.accept(ModItems.CHEMISTRY_BENCH_ITEM.get(), ItemModelUtils.plainModel(itemChemModel));
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(ModBlocks.CHEMISTRY_BENCH.get())
                        .with(PropertyDispatch.initial(ChemistryBenchBlock.PART)
                                .select(DeskPart.PRIMARY, blockModels.plainVariant(primaryChemModel))
                                .select(DeskPart.SECONDARY, blockModels.plainVariant(secondaryChemModel)))
                        .with(PropertyDispatch.modify(ChemistryBenchBlock.FACING)
                                .select(Direction.NORTH, variant -> variant)
                                .select(Direction.EAST, variant -> variant.withYRot(Quadrant.R90))
                                .select(Direction.SOUTH, variant -> variant.withYRot(Quadrant.R180))
                                .select(Direction.WEST, variant -> variant.withYRot(Quadrant.R270)))
        );

        //COMPUTER DESK//
        Identifier primaryComputerModel = ModelLocationUtils.getModelLocation(ModBlocks.COMPUTER_DESK.get(), "_primary");
        Identifier secondaryComputerModel = ModelLocationUtils.getModelLocation(ModBlocks.COMPUTER_DESK.get(), "_secondary");
        Identifier itemComputerModel = ModelLocationUtils.getModelLocation(ModItems.COMPUTER_DESK_ITEM.get());
        itemModels.itemModelOutput.accept(ModItems.COMPUTER_DESK_ITEM.get(), ItemModelUtils.plainModel(itemComputerModel));
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(ModBlocks.COMPUTER_DESK.get())
                        .with(PropertyDispatch.initial(ComputerDeskBlock.PART)
                                .select(DeskPart.PRIMARY, blockModels.plainVariant(primaryComputerModel))
                                .select(DeskPart.SECONDARY, blockModels.plainVariant(secondaryComputerModel)))
                        .with(PropertyDispatch.modify(ComputerDeskBlock.FACING)
                                .select(Direction.NORTH, variant -> variant)
                                .select(Direction.EAST, variant -> variant.withYRot(Quadrant.R90))
                                .select(Direction.SOUTH, variant -> variant.withYRot(Quadrant.R180))
                                .select(Direction.WEST, variant -> variant.withYRot(Quadrant.R270)))
        );

        RetroItemModels.generate(itemModels);
    }
}
