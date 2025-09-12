package net.jmb19905.niftycarts.datagen.lang;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsWoodType;
import net.minecraft.world.entity.EntityType;

import java.nio.file.Path;
import java.util.Map;

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
}
