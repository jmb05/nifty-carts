package net.jmb19905.niftycarts.client.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.jmb19905.niftycarts.NiftyCarts;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.state.properties.WoodType;

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
        translationBuilder.add("key.categories.niftycarts", "NiftyCarts");
        translationBuilder.add("key.niftycarts.action", "Attach/Detach Cart");
        translationBuilder.add("key.niftycarts.slow", "Toggle Slow");
        translationBuilder.add("subtitles.niftycarts.cart.attached", "Cart attaches");
        translationBuilder.add("subtitles.niftycarts.cart.detached", "Cart detaches");
        translationBuilder.add("subtitles.niftycarts.cart.placed", "Cart placed");
        translationBuilder.add("item.supply_cart.tooltip1", "This cart can hold up to 54 stacks of items");
        translationBuilder.add("item.supply_cart.tooltip2", "It has one seat and can be decorated with a banner");
        translationBuilder.add("item.hand_cart.tooltip1", "This cart can hold up to 27 stacks of items");
        translationBuilder.add("item.hand_cart.tooltip2", "It can only be pulled by the player");
        translationBuilder.add("item.animal_cart.tooltip1", "This cart has two seats for animals or players and can be decorated with a banner");
        translationBuilder.add("item.animal_cart.tooltip2", "It can be also controlled from the front seat");
        translationBuilder.add("item.plow.tooltip1", "This contraption can till the ground, make dirt paths or strip logs");
        translationBuilder.add("item.plow.tooltip2", "It needs the respective tools to work and can be toggled by right-clicking");
        translationBuilder.add("item.seed_drill.tooltip1", "This contraption plants seeds on farmland");
        translationBuilder.add("item.seed_drill.tooltip2", "It has room for 9 stacks of seeds");
        translationBuilder.add("item.reaper.tooltip1", "This contraption can harvest crops");
        translationBuilder.add("item.reaper.tooltip2", "Only works if the player is sitting on it");
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