package net.jmb19905.niftycarts.datagen.lang;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsWoodType;

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
        translationBuilder.add(NiftyCarts.SUPPLY_CART_ENTITY, "Transportkarren");
        translationBuilder.add(NiftyCarts.ANIMAL_CART_ENTITY, "Tierkarren");
        translationBuilder.add(NiftyCarts.HAND_CART_ENTITY, "Handkarren");
        translationBuilder.add(NiftyCarts.PLOW_ENTITY, "Pflug");
        translationBuilder.add(NiftyCarts.SEED_DRILL_ENTITY, "Sämaschine");
        translationBuilder.add(NiftyCarts.REAPER_ENTITY, "Mähmaschine");
        translationBuilder.add(NiftyCarts.WAGON_ENTITY, "Wagen");
        translationBuilder.add(NiftyCarts.CART_ONE_CM, "Strecke auf Karren gefahren");

        try {
            Path path = dataOutput.getModContainer().findPath("assets/" + NiftyCarts.MOD_ID + "/lang/de_de.existing.json").orElseThrow();
            translationBuilder.add(path);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load existing lang file", e);
        }
    }
}
