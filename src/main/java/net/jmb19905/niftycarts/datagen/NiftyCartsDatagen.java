package net.jmb19905.niftycarts.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.jmb19905.niftycarts.datagen.lang.*;

public class NiftyCartsDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(NiftyCartsModelProvider::new);
        pack.addProvider(NiftyCartsRecipeProvider::new);
        pack.addProvider(NiftyCartsDeDeLanguageProvider::new);
        pack.addProvider(NiftyCartsEnUsLanguageProvider::new);
        pack.addProvider(NiftyCartsAdvancementProvider::new);
    }
}
