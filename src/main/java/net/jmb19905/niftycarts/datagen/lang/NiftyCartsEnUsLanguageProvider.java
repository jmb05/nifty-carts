package net.jmb19905.niftycarts.datagen.lang;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.jmb19905.niftycarts.NiftyCarts;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class NiftyCartsEnUsLanguageProvider extends FabricLanguageProvider {

    private static final Map<EntityType<?>, String> ENTITY_NAMES = ImmutableMap.of(
            NiftyCarts.SUPPLY_CART_ENTITY, "Supply Cart",
            NiftyCarts.ANIMAL_CART_ENTITY, "Animal Cart",
            NiftyCarts.HAND_CART_ENTITY, "Hand Cart",
            NiftyCarts.PLOW_ENTITY, "Plow",
            NiftyCarts.SEED_DRILL_ENTITY, "Seed Drill",
            NiftyCarts.REAPER_ENTITY, "Reaper",
            NiftyCarts.WAGON_ENTITY, "Wagon"
    );

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
            translationBuilder.add(NiftyCarts.WAGON.get(woodType), capitalizeWordStart(woodType.name()) + "Wagon");
        });

        translationBuilder.add("stat." + NiftyCarts.RIDE_CART_CM.toLanguageKey(), "Distance by Cart");
        translationBuilder.add("stat." + NiftyCarts.STEER_ANIMAL_CART_CM.toLanguageKey(), "Distance steering Animal Cart");
        translationBuilder.add("stat." + NiftyCarts.STEER_REAPER_CM.toLanguageKey(), "Distance steering Reaper");

        for (EntityType<?> type : ENTITY_NAMES.keySet()) {
            translationBuilder.add("stat." + NiftyCarts.CART_PULL_CM.get(type).toLanguageKey(), "Distance pulling " + ENTITY_NAMES.get(type));
            translationBuilder.add(type, ENTITY_NAMES.get(type));
        }

        try {
            Path path = dataOutput.getModContainer().findPath("assets/" + NiftyCarts.MOD_ID + "/lang/en_us.existing.json").orElseThrow();
            translationBuilder.add(path);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load existing lang file", e);
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
