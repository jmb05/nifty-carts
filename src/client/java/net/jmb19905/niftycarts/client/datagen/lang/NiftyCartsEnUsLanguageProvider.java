package net.jmb19905.niftycarts.client.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.jmb19905.niftycarts.NiftyCarts;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class NiftyCartsEnUsLanguageProvider extends FabricLanguageProvider {


    public NiftyCartsEnUsLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider provider, TranslationBuilder translationBuilder) {
        translationBuilder.add(NiftyCarts.WHEEL, "Wheel");
        WoodType.values().forEach(woodType -> {
            translationBuilder.add(NiftyCarts.SUPPLY_CART.get(woodType), capitalizeWordStart(woodType.name()) + "Supply Cart");
            translationBuilder.add(NiftyCarts.ANIMAL_CART.get(woodType), capitalizeWordStart(woodType.name()) + "Animal Cart");
            translationBuilder.add(NiftyCarts.HAND_CART.get(woodType), capitalizeWordStart(woodType.name()) + "Hand Cart");
            translationBuilder.add(NiftyCarts.PLOW.get(woodType), capitalizeWordStart(woodType.name()) + "Plow");
            translationBuilder.add(NiftyCarts.SEED_DRILL.get(woodType), capitalizeWordStart(woodType.name()) + "Seed Drill");
            translationBuilder.add(NiftyCarts.REAPER.get(woodType), capitalizeWordStart(woodType.name()) + "Reaper");
        });
        translationBuilder.add(NiftyCarts.SUPPLY_CART_ENTITY, "Supply Cart");
        translationBuilder.add(NiftyCarts.ANIMAL_CART_ENTITY, "Animal Cart");
        translationBuilder.add(NiftyCarts.HAND_CART_ENTITY, "Hand Cart");
        translationBuilder.add(NiftyCarts.PLOW_ENTITY, "Plow");
        translationBuilder.add(NiftyCarts.SEED_DRILL_ENTITY, "Seed Drill");
        translationBuilder.add(NiftyCarts.REAPER_ENTITY, "Reaper");
        translationBuilder.add(NiftyCarts.CART_ONE_CM, "Distance by Cart");

        try {
            Path path = dataOutput.getModContainer().findPath("assets/" + NiftyCarts.MOD_ID + "/lang/en_us.existing.json").orElseThrow();
            translationBuilder.add(path);
        } catch (Exception e) {
            throw new RuntimeException("Could not find existing lang file", e);
        }

    }

    private static String capitalizeWordStart(String s) {
        StringBuilder builder = new StringBuilder();
        for (String sub : s.split("_")) {
            builder.append(sub.substring(0, 1).toUpperCase());
            builder.append(sub.substring(1));
            builder.append(" ");
        }
        return builder.toString();
    }
}