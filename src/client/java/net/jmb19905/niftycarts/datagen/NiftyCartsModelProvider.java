package net.jmb19905.niftycarts.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.jmb19905.niftycarts.NiftyCarts;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.world.level.block.state.properties.WoodType;

public class NiftyCartsModelProvider extends FabricModelProvider {

    public NiftyCartsModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {

    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.generateFlatItem(NiftyCarts.WHEEL, ModelTemplates.FLAT_ITEM);
        WoodType.values().forEach(woodType -> {
            itemModelGenerators.generateFlatItem(NiftyCarts.SUPPLY_CART.get(woodType), ModelTemplates.FLAT_ITEM);
            itemModelGenerators.generateFlatItem(NiftyCarts.HAND_CART.get(woodType), ModelTemplates.FLAT_ITEM);
            itemModelGenerators.generateFlatItem(NiftyCarts.ANIMAL_CART.get(woodType), ModelTemplates.FLAT_ITEM);
            itemModelGenerators.generateFlatItem(NiftyCarts.PLOW.get(woodType), ModelTemplates.FLAT_ITEM);
            itemModelGenerators.generateFlatItem(NiftyCarts.SEED_DRILL.get(woodType), ModelTemplates.FLAT_ITEM);
            itemModelGenerators.generateFlatItem(NiftyCarts.REAPER.get(woodType), ModelTemplates.FLAT_ITEM);
            itemModelGenerators.generateFlatItem(NiftyCarts.WAGON.get(woodType), ModelTemplates.FLAT_ITEM);
        });
    }
}