package net.jmb19905.niftycarts.client.datagen.lang;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.jmb19905.niftycarts.NiftyCarts;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class NiftyCartsDeDeLanguageProvider extends FabricLanguageProvider {

    private static final Map<WoodType, String> NAMES = ImmutableMap.<WoodType, String>builderWithExpectedSize(11)
            .put(WoodType.ACACIA, "Akazienholz")
            .put(WoodType.DARK_OAK, "Schwarzeichenholz")
            .put(WoodType.BAMBOO, "Bambus")
            .put(WoodType.CHERRY, "Kirschholz")
            .put(WoodType.OAK, "Eichenholz")
            .put(WoodType.SPRUCE, "Fichtenholz")
            .put(WoodType.BIRCH, "Birkenholz")
            .put(WoodType.JUNGLE, "Tropenholz")
            .put(WoodType.MANGROVE, "Mangrovenholz")
            .put(WoodType.CRIMSON, "Karmesin")
            .put(WoodType.WARPED, "Wirr")
            .put(WoodType.PALE_OAK, "Blasseichen").build();

    private static final Map<EntityType<?>, String> ENTITY_NAMES = ImmutableMap.of(
            NiftyCarts.SUPPLY_CART_ENTITY, "Transportkarren",
            NiftyCarts.ANIMAL_CART_ENTITY, "Tierkarren",
            NiftyCarts.HAND_CART_ENTITY, "Handkarren",
            NiftyCarts.PLOW_ENTITY, "Pflug",
            NiftyCarts.SEED_DRILL_ENTITY, "Sämaschine",
            NiftyCarts.REAPER_ENTITY, "Mähmaschine",
            NiftyCarts.WAGON_ENTITY, "Wagen"
    );

    public NiftyCartsDeDeLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "de_de", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.@NotNull Provider provider, TranslationBuilder translationBuilder) {
        translationBuilder.add(NiftyCarts.WHEEL, "Rad");
        WoodType.values().forEach(woodType -> {
            String woodTypeName = NAMES.get(woodType);
            translationBuilder.add(NiftyCarts.SUPPLY_CART.get(woodType), woodTypeName + "transportkarren");
            translationBuilder.add(NiftyCarts.ANIMAL_CART.get(woodType), woodTypeName + "tierkarren");
            translationBuilder.add(NiftyCarts.HAND_CART.get(woodType), woodTypeName + "handkarren");
            translationBuilder.add(NiftyCarts.PLOW.get(woodType), woodTypeName + "pflug");
            translationBuilder.add(NiftyCarts.SEED_DRILL.get(woodType), woodTypeName + "sämaschine");
            translationBuilder.add(NiftyCarts.REAPER.get(woodType), woodTypeName + "mähmaschine");
            translationBuilder.add(NiftyCarts.WAGON.get(woodType), woodTypeName + "wagen");
        });

        for (EntityType<?> type : ENTITY_NAMES.keySet()) {
            translationBuilder.add(type, ENTITY_NAMES.get(type));
            translationBuilder.add("stat." + NiftyCarts.CART_PULL_CM.get(type).toLanguageKey(), "Strecke " + ENTITY_NAMES.get(type) + " gezogen");
        }
        translationBuilder.add("stat." + NiftyCarts.RIDE_CART_CM.toLanguageKey(), "Strecke auf Karren gefahren");
        translationBuilder.add("stat." + NiftyCarts.STEER_ANIMAL_CART_CM.toLanguageKey(), "Strecke Tierkarren gesteuert");
        translationBuilder.add("stat." + NiftyCarts.STEER_REAPER_CM.toLanguageKey(), "Strecke Mähmaschine gesteuert");

        try {
            Path path = dataOutput.getModContainer().findPath("assets/" + NiftyCarts.MOD_ID + "/lang/en_us.existing.json").orElseThrow();
            translationBuilder.add(path);
        } catch (Exception e) {
            throw new RuntimeException("Could not find existing lang file", e);
        }

    }
}