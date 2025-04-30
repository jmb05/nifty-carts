package net.jmb19905.niftycarts.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.jmb19905.niftycarts.client.datagen.lang.NiftyCartsDeDeLanguageProvider;
import net.jmb19905.niftycarts.client.datagen.lang.NiftyCartsEnUsLanguageProvider;

public class NiftyCartsDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(NiftyCartRecipeProvider::new);
        pack.addProvider(NiftyCartsModelProvider::new);
        pack.addProvider(NiftyCartsEnUsLanguageProvider::new);
        pack.addProvider(NiftyCartsDeDeLanguageProvider::new);
    }
}