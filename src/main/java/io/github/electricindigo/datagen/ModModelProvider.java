package io.github.electricindigo.datagen;

import com.mojang.math.Quadrant;
import io.github.electricindigo.ChronoDynamics;
import io.github.electricindigo.registry.ModBlocks;
import io.github.electricindigo.block.researchdesk.DeskPart;
import io.github.electricindigo.block.researchdesk.ResearchDeskBlock;
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
import net.neoforged.fml.common.Mod;

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

        blockModels.createTrivialCube(ModBlocks.CHEMISTRY_BENCH.get());

        Identifier primaryModel = ModelLocationUtils.getModelLocation(ModBlocks.RESEARCH_DESK.get(), "_primary");
        Identifier secondaryModel = ModelLocationUtils.getModelLocation(ModBlocks.RESEARCH_DESK.get(), "_secondary");
        Identifier itemModel = ModelLocationUtils.getModelLocation(ModItems.RESEARCH_DESK_ITEM.get());

        itemModels.itemModelOutput.accept(ModItems.RESEARCH_DESK_ITEM.get(), ItemModelUtils.plainModel(itemModel));


        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(ModBlocks.RESEARCH_DESK.get())
                        .with(PropertyDispatch.initial(ResearchDeskBlock.PART)
                                .select(DeskPart.PRIMARY, blockModels.plainVariant(primaryModel))
                                .select(DeskPart.SECONDARY, blockModels.plainVariant(secondaryModel)))
                        .with(PropertyDispatch.modify(ResearchDeskBlock.FACING)
                                .select(Direction.NORTH, variant -> variant)
                                .select(Direction.EAST, variant -> variant.withYRot(Quadrant.R90))
                                .select(Direction.SOUTH, variant -> variant.withYRot(Quadrant.R180))
                                .select(Direction.WEST, variant -> variant.withYRot(Quadrant.R270)))
        );
    }
}
