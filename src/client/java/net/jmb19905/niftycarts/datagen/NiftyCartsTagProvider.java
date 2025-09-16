package net.jmb19905.niftycarts.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class NiftyCartsTagProvider extends FabricTagProvider<Item> {

    public NiftyCartsTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, BuiltInRegistries.ITEM.key(), registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
    }
}
