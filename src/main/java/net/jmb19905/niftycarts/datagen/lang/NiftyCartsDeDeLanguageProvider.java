package net.jmb19905.niftycarts.datagen.lang;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsWoodType;

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
        translationBuilder.add("key.categories.niftycarts", "NiftyCarts");
        translationBuilder.add("key.niftycarts.action", "Karren an-/abhängen");
        translationBuilder.add("key.niftycarts.slow", "Langsammodus an-/ausschalten");
        translationBuilder.add("subtitles.niftycarts.cart.attached", "Karren wird angehängt");
        translationBuilder.add("subtitles.niftycarts.cart.detached", "Karren wird abgehängt");
        translationBuilder.add("subtitles.niftycarts.cart.placed", "Karren wird platziert");
        translationBuilder.add("tutorial.slow.message", "Drücke %1$s um den Langsammodus zu aktivieren");
        translationBuilder.add("item.cart.press_shift_tooltip", "Halte die [Linke Umschalttaste] gedrückt für mehr Details");
        translationBuilder.add("item.supply_cart.tooltip1", "Dieser Karren kann bis zu 54 Stapel lagern");
        translationBuilder.add("item.supply_cart.tooltip2", "Hat einen Sitzplatz, kann mit einem Banner dekoriert werden");
        translationBuilder.add("item.hand_cart.tooltip1", "Dieser Karren kann bis zu 27 Stapel lagern");
        translationBuilder.add("item.hand_cart.tooltip2", "Er kann nur vom Spieler gezogen werden");
        translationBuilder.add("item.animal_cart.tooltip1", "Hat zwei Sitzplätze, kann mit einem Banner dekoriert werden");
        translationBuilder.add("item.animal_cart.tooltip2", "Kann auch vom vorderen Sitz aus gesteuert werden");
        translationBuilder.add("item.plow.tooltip1", "Kann den Boden pflügen, Wege planieren oder Rinde abschaben");
        translationBuilder.add("item.plow.tooltip2", "Braucht das jeweilige Werkzeug, mit Rechtsklick aktivieren");
        translationBuilder.add("item.seed_drill.tooltip1", "Diese Maschine kann Samen pflanzen");
        translationBuilder.add("item.seed_drill.tooltip2", "Sie hat Platz für 9 Stapel Samen");
        translationBuilder.add("item.reaper.tooltip1", "Diese Maschine kann Felder abernten");
        translationBuilder.add("item.reaper.tooltip2", "Sie funktioniert nur, wenn ein Spieler sie bedient");
        translationBuilder.add("item.wagon.tooltip1", "Verwende 5 Teppiche als Dach, Truhen für Lagerplatz");
        translationBuilder.add("item.wagon.tooltip2", "Hat bis zu vier Sitzplätze");
        translationBuilder.add("tag.item.niftycarts.seed_drill_plantable", "Kann mit der Sämaschine gesät werden");
    }
}
