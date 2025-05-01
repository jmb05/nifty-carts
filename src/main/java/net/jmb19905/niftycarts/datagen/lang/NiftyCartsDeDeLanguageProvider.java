package net.jmb19905.niftycarts.datagen.lang;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.jmb19905.niftycarts.NiftyCarts;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.state.properties.WoodType;

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
            .put(WoodType.WARPED, "Wirr").build();

    public NiftyCartsDeDeLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "de_de", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider provider, TranslationBuilder translationBuilder) {
        translationBuilder.add(NiftyCarts.WHEEL, "Rad");
        WoodType.values().forEach(woodType -> {
            String woodTypeName = NAMES.get(woodType);
            translationBuilder.add(NiftyCarts.SUPPLY_CART.get(woodType), woodTypeName + "transportkarren");
            translationBuilder.add(NiftyCarts.ANIMAL_CART.get(woodType), woodTypeName + "tierkarren");
            translationBuilder.add(NiftyCarts.HAND_CART.get(woodType), woodTypeName + "handkarren");
            translationBuilder.add(NiftyCarts.PLOW.get(woodType), woodTypeName + "pflug");
            translationBuilder.add(NiftyCarts.SEED_DRILL.get(woodType), woodTypeName + "sämaschine");
            translationBuilder.add(NiftyCarts.REAPER.get(woodType), woodTypeName + "mähmaschine");
        });
        translationBuilder.add(NiftyCarts.SUPPLY_CART_ENTITY, "Transportkarren");
        translationBuilder.add(NiftyCarts.ANIMAL_CART_ENTITY, "Tierkarren");
        translationBuilder.add(NiftyCarts.HAND_CART_ENTITY, "Handkarren");
        translationBuilder.add(NiftyCarts.PLOW_ENTITY, "Pflug");
        translationBuilder.add(NiftyCarts.SEED_DRILL_ENTITY, "Sämaschine");
        translationBuilder.add(NiftyCarts.REAPER_ENTITY, "Mähmaschine");
        translationBuilder.add(NiftyCarts.CART_ONE_CM, "Strecke auf Karren gefahren");
        translationBuilder.add("key.categories.niftycarts", "NiftyCarts");
        translationBuilder.add("key.niftycarts.action", "Karren an-/abhängen");
        translationBuilder.add("key.niftycarts.slow", "Langsammodus an-/ausschalten");
        translationBuilder.add("subtitles.niftycarts.cart.attached", "Karren wird angehängt");
        translationBuilder.add("subtitles.niftycarts.cart.detached", "Karren wird abgehängt");
        translationBuilder.add("subtitles.niftycarts.cart.placed", "Karren wird platziert");
        translationBuilder.add("tutorial.slow.message", "Drücke %1$s um den Langsammodus zu aktivieren");
        translationBuilder.add("item.supply_cart.tooltip1", "Dieser Karren kann bis zu 54 Stapel lagern");
        translationBuilder.add("item.supply_cart.tooltip2", "Er hat einen Sitzplatz und kann mit einem Banner dekoriert werden");
        translationBuilder.add("item.hand_cart.tooltip1", "Dieser Karren kann bis zu 27 Stapel lagern");
        translationBuilder.add("item.hand_cart.tooltip2", "Er kann nur vom Spieler gezogen werden");
        translationBuilder.add("item.animal_cart.tooltip1", "Dieser Karren hat zwei Sitzplätze für Tiere oder Spieler, und kann mit einem Banner dekoriert werden");
        translationBuilder.add("item.animal_cart.tooltip2", "Er kann auch vom vorderen Sitz aus gesteuert werden");
        translationBuilder.add("item.plow.tooltip1", "Diese Maschine kann den Boden pflügen, Wege planieren oder Rinde abschaben");
        translationBuilder.add("item.plow.tooltip2", "Sie braucht das jeweilige Werkzeug und kann mit Rechtsklick aktiviert werden");
        translationBuilder.add("item.seed_drill.tooltip1", "Diese Maschine kann Samen pflanzen");
        translationBuilder.add("item.seed_drill.tooltip2", "Sie hat Platz für 9 Stapel Samen");
        translationBuilder.add("item.reaper.tooltip1", "Diese Maschine kann Felder abernten");
        translationBuilder.add("item.reaper.tooltip2", "Sie funktioniert nur, wenn ein Spieler sie bedient");
    }
}
