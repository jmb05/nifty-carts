package net.jmb19905.niftycarts.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsWoodType;

import java.nio.file.Path;

public class NiftyCartsEnUsLanguageProvider extends FabricLanguageProvider {

    public NiftyCartsEnUsLanguageProvider(FabricDataOutput dataOutput) {
        super(dataOutput, "en_us");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(NiftyCarts.WHEEL, "Wheel");
        for (NiftyCartsWoodType woodType : NiftyCartsWoodType.values()) {
            translationBuilder.add(NiftyCarts.SUPPLY_CART.get(woodType), woodType.getName() + " Supply Cart");
            translationBuilder.add(NiftyCarts.ANIMAL_CART.get(woodType), woodType.getName() + " Animal Cart");
            translationBuilder.add(NiftyCarts.HAND_CART.get(woodType), woodType.getName() + " Hand Cart");
            translationBuilder.add(NiftyCarts.PLOW.get(woodType), woodType.getName() + " Plow");
            translationBuilder.add(NiftyCarts.SEED_DRILL.get(woodType), woodType.getName() + " Seed Drill");
            translationBuilder.add(NiftyCarts.REAPER.get(woodType), woodType.getName() + " Reaper");
            translationBuilder.add(NiftyCarts.WAGON.get(woodType), woodType.getName() + " Wagon");
        }
        translationBuilder.add(NiftyCarts.SUPPLY_CART_ENTITY, "Supply Cart");
        translationBuilder.add(NiftyCarts.ANIMAL_CART_ENTITY, "Animal Cart");
        translationBuilder.add(NiftyCarts.HAND_CART_ENTITY, "Hand Cart");
        translationBuilder.add(NiftyCarts.PLOW_ENTITY, "Plow");
        translationBuilder.add(NiftyCarts.SEED_DRILL_ENTITY, "Seed Drill");
        translationBuilder.add(NiftyCarts.REAPER_ENTITY, "Reaper");
        translationBuilder.add(NiftyCarts.WAGON_ENTITY, "Wagon");
        translationBuilder.add(NiftyCarts.CART_ONE_CM, "Distance by Cart");

        try {
            Path path = dataOutput.getModContainer().findPath("assets/" + NiftyCarts.MOD_ID + "/lang/en_us.existing.json").orElseThrow();
            translationBuilder.add(path);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load existing lang file", e);
        }
    }
}
