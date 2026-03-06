package net.jmb19905.niftycarts.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.client.NiftyCartsClient;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class NiftyCartRecipeProvider extends FabricRecipeProvider {

    public NiftyCartRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.@NotNull Provider provider, @NotNull RecipeOutput recipeOutput) {
        return new RecipeProvider(provider, recipeOutput) {
            @SuppressWarnings("OptionalGetWithoutIsPresent")
            @Override
            public void buildRecipes() {
                var items = provider.lookupOrThrow(Registries.ITEM);
                ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, NiftyCarts.WHEEL)
                        .define('p', ItemTags.PLANKS)
                        .define('s', Items.STICK)
                        .unlockedBy(RecipeProvider.getHasName(Items.STICK), RecipeProvider.inventoryTrigger(ItemPredicate.Builder.item().of(items, Items.STICK)))
                        .pattern("sss")
                        .pattern("sps")
                        .pattern("sss")
                        .save(recipeOutput);

                WoodType.values().forEach(woodType -> {
                    Identifier supplyCartId = NiftyCarts.resLoc(woodType.name() + "_supply_cart");
                    Optional<Holder.Reference<@NotNull Item>> supplyCart = BuiltInRegistries.ITEM.get(supplyCartId);
                    Item planks = BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace(woodType.name() + "_planks"));
                    Item strippedLogs = BuiltInRegistries.ITEM.getValue(
                            Identifier.withDefaultNamespace(
                                    "stripped_"
                                            + woodType.name()
                                            + "_" + (NiftyCartsClient.LOG_NAME_OVERRIDE.getOrDefault(woodType, "log"))));
                    var recipeTrigger = RecipeProvider.inventoryTrigger(ItemPredicate.Builder.item().of(items, NiftyCarts.WHEEL), ItemPredicate.Builder.item().of(items, planks));
                    var wagonRecipeTrigger = RecipeProvider.inventoryTrigger(ItemPredicate.Builder.item().of(items, NiftyCarts.WHEEL), ItemPredicate.Builder.item().of(items, planks), ItemPredicate.Builder.item().of(items, ItemTags.WOOL));
                    ShapedRecipeBuilder.shaped(items, RecipeCategory.TRANSPORTATION, supplyCart.get().value())
                            .define('p', planks)
                            .define('w', NiftyCarts.WHEEL)
                            .define('c', Blocks.CHEST)
                            .unlockedBy("has_wheel_and_planks", recipeTrigger)
                            .pattern("pcp")
                            .pattern("pcp")
                            .pattern("wpw")
                            .save(recipeOutput);

                    Identifier animalCartId = NiftyCarts.resLoc(woodType.name() + "_animal_cart");
                    Optional<Holder.Reference<@NotNull Item>>  animalCart = BuiltInRegistries.ITEM.get(animalCartId);
                    ShapedRecipeBuilder.shaped(items, RecipeCategory.TRANSPORTATION, animalCart.get().value())
                            .define('p', planks)
                            .define('w', NiftyCarts.WHEEL)
                            .unlockedBy("has_wheel_and_planks", recipeTrigger)
                            .pattern("ppp")
                            .pattern("ppp")
                            .pattern("wpw")
                            .save(recipeOutput);

                    Identifier handCartId = NiftyCarts.resLoc(woodType.name() + "_hand_cart");
                    Optional<Holder.Reference<@NotNull Item>>  handCart = BuiltInRegistries.ITEM.get(handCartId);
                    ShapedRecipeBuilder.shaped(items, RecipeCategory.TRANSPORTATION, handCart.get().value())
                            .define('p', planks)
                            .define('w', NiftyCarts.WHEEL)
                            .define('c', Blocks.CHEST)
                            .unlockedBy("has_wheel_and_planks", recipeTrigger)
                            .pattern("pcp")
                            .pattern("wpw")
                            .save(recipeOutput);

                    Identifier plowId = NiftyCarts.resLoc(woodType.name() + "_plow");
                    Optional<Holder.Reference<@NotNull Item>>  plow = BuiltInRegistries.ITEM.get(plowId);
                    ShapedRecipeBuilder.shaped(items, RecipeCategory.TRANSPORTATION, plow.get().value())
                            .define('p', planks)
                            .define('w', NiftyCarts.WHEEL)
                            .define('s', Items.STICK)
                            .unlockedBy("has_wheel_and_planks", recipeTrigger)
                            .pattern("sss")
                            .pattern("psp")
                            .pattern("wpw")
                            .save(recipeOutput);

                    Identifier reaperId = NiftyCarts.resLoc(woodType.name() + "_reaper");
                    Optional<Holder.Reference<@NotNull Item>>  reaper = BuiltInRegistries.ITEM.get(reaperId);
                    ShapedRecipeBuilder.shaped(items, RecipeCategory.TRANSPORTATION, reaper.get().value())
                            .define('p', planks)
                            .define('l', BuiltInRegistries.ITEM.get(Identifier.withDefaultNamespace(woodType.name() + "_slab")).get().value())
                            .define('w', NiftyCarts.WHEEL)
                            .define('s', Items.STICK)
                            .define('i', Items.IRON_INGOT)
                            .unlockedBy("has_wheel_and_planks", recipeTrigger)
                            .pattern(" sl")
                            .pattern("spp")
                            .pattern("iww")
                            .save(recipeOutput);

                    Identifier seedDrillId = NiftyCarts.resLoc(woodType.name() + "_seed_drill");
                    Optional<Holder.Reference<@NotNull Item>>  seedDrill = BuiltInRegistries.ITEM.get(seedDrillId);
                    ShapedRecipeBuilder.shaped(items, RecipeCategory.TRANSPORTATION, seedDrill.get().value())
                            .define('p', planks)
                            .define('w', NiftyCarts.WHEEL)
                            .define('c', Blocks.CHEST)
                            .define('h', Blocks.HOPPER)
                            .unlockedBy("has_wheel_and_planks", recipeTrigger)
                            .pattern("pcp")
                            .pattern("php")
                            .pattern("wpw")
                            .save(recipeOutput);

                    Identifier wagonId = NiftyCarts.resLoc(woodType.name() + "_wagon");
                    Optional<Holder.Reference<@NotNull Item>> wagon = BuiltInRegistries.ITEM.get(wagonId);
                    ShapedRecipeBuilder.shaped(items, RecipeCategory.TRANSPORTATION, wagon.get().value())
                            .define('p', planks)
                            .define('w', NiftyCarts.WHEEL)
                            .define('l', strippedLogs)
                            .unlockedBy("has_wheel_planks_wool", wagonRecipeTrigger)
                            .pattern("lll")
                            .pattern("wpw")
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