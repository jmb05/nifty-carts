package net.jmb19905.niftycarts.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsWoodType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

public class NiftyCartRecipeProvider extends FabricRecipeProvider {

    public NiftyCartRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        ResourceLocation wheelId = new ResourceLocation(NiftyCarts.MOD_ID, "wheel");
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, NiftyCarts.WHEEL)
                .define('p', ItemTags.PLANKS)
                .define('s', Items.STICK)
                .unlockedBy("has_stick", FabricRecipeProvider.has(Items.STICK))
                .pattern("sss")
                .pattern("sps")
                .pattern("sss")
                .save(exporter, wheelId);

        for (NiftyCartsWoodType woodType : NiftyCartsWoodType.values()) {
            ResourceLocation supplyCartId = new ResourceLocation(NiftyCarts.MOD_ID, woodType.getId() + "_supply_cart");
            Item supplyCart = BuiltInRegistries.ITEM.get(supplyCartId);
            ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, supplyCart)
                    .define('p', BuiltInRegistries.ITEM.get(new ResourceLocation(woodType.getId() + "_planks")))
                    .define('w', NiftyCarts.WHEEL)
                    .define('c', Blocks.CHEST)
                    .unlockedBy("has_wheel", FabricRecipeProvider.has(NiftyCarts.WHEEL))
                    .pattern("pcp")
                    .pattern("pcp")
                    .pattern("wpw")
                    .save(exporter, supplyCartId);

            ResourceLocation animalCartId = new ResourceLocation(NiftyCarts.MOD_ID, woodType.getId() + "_animal_cart");
            Item animalCart = BuiltInRegistries.ITEM.get(animalCartId);
            ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, animalCart)
                    .define('p', BuiltInRegistries.ITEM.get(new ResourceLocation(woodType.getId() + "_planks")))
                    .define('w', NiftyCarts.WHEEL)
                    .unlockedBy("has_wheel", FabricRecipeProvider.has(NiftyCarts.WHEEL))
                    .pattern("ppp")
                    .pattern("ppp")
                    .pattern("wpw")
                    .save(exporter, animalCartId);

            ResourceLocation handCartId = new ResourceLocation(NiftyCarts.MOD_ID, woodType.getId() + "_hand_cart");
            Item handCart = BuiltInRegistries.ITEM.get(handCartId);
            ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, handCart)
                    .define('p', BuiltInRegistries.ITEM.get(new ResourceLocation(woodType.getId() + "_planks")))
                    .define('w', NiftyCarts.WHEEL)
                    .define('c', Blocks.CHEST)
                    .unlockedBy("has_wheel", FabricRecipeProvider.has(NiftyCarts.WHEEL))
                    .pattern("pcp")
                    .pattern("wpw")
                    .save(exporter, handCartId);

            ResourceLocation plowId = new ResourceLocation(NiftyCarts.MOD_ID, woodType.getId() + "_plow");
            Item plow = BuiltInRegistries.ITEM.get(plowId);
            ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, plow)
                    .define('p', BuiltInRegistries.ITEM.get(new ResourceLocation(woodType.getId() + "_planks")))
                    .define('w', NiftyCarts.WHEEL)
                    .define('s', Items.STICK)
                    .unlockedBy("has_wheel", FabricRecipeProvider.has(NiftyCarts.WHEEL))
                    .pattern("sss")
                    .pattern("psp")
                    .pattern("wpw")
                    .save(exporter, plowId);

            ResourceLocation reaperId = new ResourceLocation(NiftyCarts.MOD_ID, woodType.getId() + "_reaper");
            Item reaper = BuiltInRegistries.ITEM.get(reaperId);
            ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, reaper)
                    .define('p', BuiltInRegistries.ITEM.get(new ResourceLocation(woodType.getId() + "_planks")))
                    .define('l', BuiltInRegistries.ITEM.get(new ResourceLocation(woodType.getId() + "_slab")))
                    .define('w', NiftyCarts.WHEEL)
                    .define('s', Items.STICK)
                    .define('i', Items.IRON_INGOT)
                    .unlockedBy("has_wheel", FabricRecipeProvider.has(NiftyCarts.WHEEL))
                    .pattern(" sl")
                    .pattern("spp")
                    .pattern("iww")
                    .save(exporter, reaperId);

            ResourceLocation seedDrillId = new ResourceLocation(NiftyCarts.MOD_ID, woodType.getId() + "_seed_drill");
            Item seedDrill = BuiltInRegistries.ITEM.get(seedDrillId);
            ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, seedDrill)
                    .define('p', BuiltInRegistries.ITEM.get(new ResourceLocation(woodType.getId() + "_planks")))
                    .define('w', NiftyCarts.WHEEL)
                    .define('c', Blocks.CHEST)
                    .define('h', Blocks.HOPPER)
                    .unlockedBy("has_wheel", FabricRecipeProvider.has(NiftyCarts.WHEEL))
                    .pattern("pcp")
                    .pattern("php")
                    .pattern("wpw")
                    .save(exporter, seedDrillId);

            ResourceLocation wagonId = new ResourceLocation(NiftyCarts.MOD_ID, woodType.getId() + "_wagon");
            Item wagon = BuiltInRegistries.ITEM.get(wagonId);
            ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, wagon)
                    .define('p', BuiltInRegistries.ITEM.get(new ResourceLocation(woodType.getId() + "_planks")))
                    .define('w', NiftyCarts.WHEEL)
                    .define('l', BuiltInRegistries.ITEM.get(new ResourceLocation("stripped_" + woodType.getId() + "_" + woodType.getLogName())))
                    .unlockedBy("has_wheel", FabricRecipeProvider.has(NiftyCarts.WHEEL))
                    .pattern("lll")
                    .pattern("wpw")
                    .pattern("wpw")
                    .save(exporter, wagonId);
        }
    }
}
