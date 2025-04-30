package net.jmb19905.niftycarts.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.jmb19905.niftycarts.NiftyCarts;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class NiftyCartRecipeProvider extends FabricRecipeProvider {

    private static Criterion<?> hasWheel() {
        return RecipeUnlockedTrigger.unlocked(ResourceKey.create(Registries.RECIPE, BuiltInRegistries.ITEM.getKey(NiftyCarts.WHEEL)));
    }

    public NiftyCartRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
        return new RecipeProvider(provider, recipeOutput) {
            @SuppressWarnings("OptionalGetWithoutIsPresent")
            @Override
            public void buildRecipes() {
                var items = provider.lookupOrThrow(Registries.ITEM);
                WoodType.values().forEach(woodType -> {
                    ResourceLocation supplyCartId = NiftyCarts.resLoc(woodType.name() + "_supply_cart");
                    Optional<Holder.Reference<Item>> supplyCart = BuiltInRegistries.ITEM.get(supplyCartId);
                    ShapedRecipeBuilder.shaped(items, RecipeCategory.TRANSPORTATION, supplyCart.get().value())
                            .define('p', BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(woodType.name() + "_planks")).get().value())
                            .define('w', NiftyCarts.WHEEL)
                            .define('c', Blocks.CHEST)
                            .unlockedBy("has_wheel", hasWheel())
                            .pattern("pcp")
                            .pattern("pcp")
                            .pattern("wpw")
                            .save(recipeOutput);

                    ResourceLocation animalCartId = NiftyCarts.resLoc(woodType.name() + "_animal_cart");
                    Optional<Holder.Reference<Item>>  animalCart = BuiltInRegistries.ITEM.get(animalCartId);
                    ShapedRecipeBuilder.shaped(items, RecipeCategory.TRANSPORTATION, animalCart.get().value())
                            .define('p', BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(woodType.name() + "_planks")).get().value())
                            .define('w', NiftyCarts.WHEEL)
                            .unlockedBy("has_wheel", hasWheel())
                            .pattern("ppp")
                            .pattern("ppp")
                            .pattern("wpw")
                            .save(recipeOutput);

                    ResourceLocation handCartId = NiftyCarts.resLoc(woodType.name() + "_hand_cart");
                    Optional<Holder.Reference<Item>>  handCart = BuiltInRegistries.ITEM.get(handCartId);
                    ShapedRecipeBuilder.shaped(items, RecipeCategory.TRANSPORTATION, handCart.get().value())
                            .define('p', BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(woodType.name() + "_planks")).get().value())
                            .define('w', NiftyCarts.WHEEL)
                            .define('c', Blocks.CHEST)
                            .unlockedBy("has_wheel", hasWheel())
                            .pattern("pcp")
                            .pattern("wpw")
                            .save(recipeOutput);

                    ResourceLocation plowId = NiftyCarts.resLoc(woodType.name() + "_plow");
                    Optional<Holder.Reference<Item>>  plow = BuiltInRegistries.ITEM.get(plowId);
                    ShapedRecipeBuilder.shaped(items, RecipeCategory.TRANSPORTATION, plow.get().value())
                            .define('p', BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(woodType.name() + "_planks")).get().value())
                            .define('w', NiftyCarts.WHEEL)
                            .define('s', Items.STICK)
                            .unlockedBy("has_wheel", hasWheel())
                            .pattern("sss")
                            .pattern("psp")
                            .pattern("wpw")
                            .save(recipeOutput);

                    ResourceLocation reaperId = NiftyCarts.resLoc(woodType.name() + "_reaper");
                    Optional<Holder.Reference<Item>>  reaper = BuiltInRegistries.ITEM.get(reaperId);
                    ShapedRecipeBuilder.shaped(items, RecipeCategory.TRANSPORTATION, reaper.get().value())
                            .define('p', BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(woodType.name() + "_planks")).get().value())
                            .define('l', BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(woodType.name() + "_slab")).get().value())
                            .define('w', NiftyCarts.WHEEL)
                            .define('s', Items.STICK)
                            .define('i', Items.IRON_INGOT)
                            .unlockedBy("has_wheel", hasWheel())
                            .pattern(" sl")
                            .pattern("spp")
                            .pattern("iww")
                            .save(recipeOutput);

                    ResourceLocation seedDrillId = NiftyCarts.resLoc(woodType.name() + "_seed_drill");
                    Optional<Holder.Reference<Item>>  seedDrill = BuiltInRegistries.ITEM.get(seedDrillId);
                    ShapedRecipeBuilder.shaped(items, RecipeCategory.TRANSPORTATION, seedDrill.get().value())
                            .define('p', BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(woodType.name() + "_planks")).get().value())
                            .define('w', NiftyCarts.WHEEL)
                            .define('c', Blocks.CHEST)
                            .define('h', Blocks.HOPPER)
                            .unlockedBy("has_wheel", hasWheel())
                            .pattern("pcp")
                            .pattern("php")
                            .pattern("wpw")
                            .save(recipeOutput);
                });
            }
        };
    }

    @Override
    public @NotNull String getName() {
        return "NiftyCarts Recipes";
    }
}