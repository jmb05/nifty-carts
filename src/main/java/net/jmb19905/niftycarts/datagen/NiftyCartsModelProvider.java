package net.jmb19905.niftycarts.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.jmb19905.niftycarts.NiftyCarts;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.world.level.block.state.properties.WoodType;

public class NiftyCartsModelProvider extends FabricModelProvider {

    public NiftyCartsModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {}

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        WoodType.values().forEach(woodType -> {
            itemModelGenerator.generateFlatItem(NiftyCarts.SUPPLY_CART.get(woodType), ModelTemplates.FLAT_ITEM);
            itemModelGenerator.generateFlatItem(NiftyCarts.HAND_CART.get(woodType), ModelTemplates.FLAT_ITEM);
            itemModelGenerator.generateFlatItem(NiftyCarts.ANIMAL_CART.get(woodType), ModelTemplates.FLAT_ITEM);
            itemModelGenerator.generateFlatItem(NiftyCarts.PLOW.get(woodType), ModelTemplates.FLAT_ITEM);
            itemModelGenerator.generateFlatItem(NiftyCarts.SEED_DRILL.get(woodType), ModelTemplates.FLAT_ITEM);
            itemModelGenerator.generateFlatItem(NiftyCarts.REAPER.get(woodType), ModelTemplates.FLAT_ITEM);
        });
    }
}
