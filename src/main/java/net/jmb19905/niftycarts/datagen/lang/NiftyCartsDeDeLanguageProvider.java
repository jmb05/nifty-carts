package net.jmb19905.niftycarts.datagen.lang;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsWoodType;
import net.minecraft.world.entity.EntityType;

import java.nio.file.Path;
import java.util.Map;

public class NiftyCartsDeDeLanguageProvider extends FabricLanguageProvider {

    @SuppressWarnings("UnstableApiUsage")
    private static final Map<NiftyCartsWoodType, String> NAMES = ImmutableMap.<NiftyCartsWoodType, String>builderWithExpectedSize(11)
            .put(NiftyCartsWoodType.ACACIA, "Akazienholz")
            .put(NiftyCartsWoodType.DARK_OAK, "Schwarzeichenholz")
            .put(NiftyCartsWoodType.BAMBOO, "Bambus")
            .put(NiftyCartsWoodType.CHERRY, "Kirschholz")
            .put(NiftyCartsWoodType.OAK, "Eichenholz")
            .put(NiftyCartsWoodType.SPRUCE, "Fichtenholz")
            .put(NiftyCartsWoodType.BIRCH, "Birkenholz")
            .put(NiftyCartsWoodType.JUNGLE, "Tropenholz")
            .put(NiftyCartsWoodType.MANGROVE, "Mangrovenholz")
            .put(NiftyCartsWoodType.CRIMSON, "Karmesin")
            .put(NiftyCartsWoodType.WARPED, "Wirr").build();

    private static final Map<EntityType<?>, String> ENTITY_NAMES = ImmutableMap.of(
            NiftyCarts.SUPPLY_CART_ENTITY, "Transportkarren",
            NiftyCarts.ANIMAL_CART_ENTITY, "Tierkarren",
            NiftyCarts.HAND_CART_ENTITY, "Handkarren",
            NiftyCarts.PLOW_ENTITY, "Pflug",
            NiftyCarts.SEED_DRILL_ENTITY, "Sämaschine",
            NiftyCarts.REAPER_ENTITY, "Mähmaschine",
            NiftyCarts.WAGON_ENTITY, "Wagen"
    );

    public NiftyCartsDeDeLanguageProvider(FabricDataOutput dataOutput) {
        super(dataOutput, "de_de");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(NiftyCarts.WHEEL, "Rad");
        for (NiftyCartsWoodType woodType : NiftyCartsWoodType.values()) {
            String woodTypeName = NAMES.get(woodType);
            translationBuilder.add(NiftyCarts.SUPPLY_CART.get(woodType), woodTypeName + "transportkarren");
            translationBuilder.add(NiftyCarts.ANIMAL_CART.get(woodType), woodTypeName + "tierkarren");
            translationBuilder.add(NiftyCarts.HAND_CART.get(woodType), woodTypeName + "handkarren");
            translationBuilder.add(NiftyCarts.PLOW.get(woodType), woodTypeName + "pflug");
            translationBuilder.add(NiftyCarts.SEED_DRILL.get(woodType), woodTypeName + "sämaschine");
            translationBuilder.add(NiftyCarts.REAPER.get(woodType), woodTypeName + "mähmaschine");
            translationBuilder.add(NiftyCarts.WAGON.get(woodType), woodTypeName + "wagen");
        }
        for (EntityType<?> type : ENTITY_NAMES.keySet()) {
            translationBuilder.add(type, ENTITY_NAMES.get(type));
            translationBuilder.add("stat." + NiftyCarts.CART_PULL_CM.get(type).toLanguageKey(), "Strecke " + ENTITY_NAMES.get(type) + " gezogen");
        }

        translationBuilder.add("stat." + NiftyCarts.RIDE_CART_CM.toLanguageKey(), "Strecke auf Karren gefahren");
        translationBuilder.add("stat." + NiftyCarts.STEER_ANIMAL_CART_CM.toLanguageKey(), "Strecke Tierkarren gesteuert");
        translationBuilder.add("stat." + NiftyCarts.STEER_REAPER_CM.toLanguageKey(), "Strecke Mähmaschine gesteuert");

        try {
            Path path = dataOutput.getModContainer().findPath("assets/" + NiftyCarts.MOD_ID + "/lang/de_de.existing.json").orElseThrow();
            translationBuilder.add(path);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load existing lang file", e);
        }
    }
}
