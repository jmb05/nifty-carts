package net.jmb19905.niftycarts.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.advancement.*;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class NiftyCartsAdvancementProvider extends FabricAdvancementProvider {
    
    protected NiftyCartsAdvancementProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        AdvancementHolder root = Advancement.Builder.advancement()
                .display(
                        NiftyCarts.WHEEL,
                        Component.translatable("advancements.niftycarts.place_cart.title"),
                        Component.translatable("advancements.niftycarts.place_cart.description"),
                        ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/husbandry.png"),
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("place_cart", PlaceCartItemCriterion.TriggerInstance.placeCart())
                .save(consumer, NiftyCarts.MOD_ID + "/root");

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        NiftyCarts.SEED_DRILL.get(WoodType.OAK),
                        Component.translatable("advancements.niftycarts.seed_drill_plant.title"),
                        Component.translatable("advancements.niftycarts.seed_drill_plant.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("seed_drill_plant", SeedDrillPlaceCriterion.TriggerInstance.seedDrillPlace())
                .save(consumer, NiftyCarts.MOD_ID + "/seed_drill_plant_seed");

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        NiftyCarts.PLOW.get(WoodType.OAK),
                        Component.translatable("advancements.niftycarts.plow_till_ground.title"),
                        Component.translatable("advancements.niftycarts.plow_till_ground.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "plow_till_ground",
                        UsePlowCriterion.TriggerInstance.usePlow(ItemPredicate.Builder.item().of(ItemTags.HOES)))
                .save(consumer, NiftyCarts.MOD_ID + "/plow_till_ground");

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.DIRT_PATH,
                        Component.translatable("advancements.niftycarts.plow_create_path.title"),
                        Component.translatable("advancements.niftycarts.plow_create_path.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "plow_create_path",
                        UsePlowCriterion.TriggerInstance.usePlow(ItemPredicate.Builder.item().of(ItemTags.SHOVELS)))
                .save(consumer, NiftyCarts.MOD_ID + "/plow_create_path");

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.STRIPPED_OAK_LOG,
                        Component.translatable("advancements.niftycarts.plow_strip_log.title"),
                        Component.translatable("advancements.niftycarts.plow_strip_log.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "plow_strip_log",
                        UsePlowCriterion.TriggerInstance.usePlow(ItemPredicate.Builder.item().of(ItemTags.AXES)))
                .save(consumer, NiftyCarts.MOD_ID + "/plow_strip_log");

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.RED_BANNER,
                        Component.translatable("advancements.niftycarts.attach_banner.title"),
                        Component.translatable("advancements.niftycarts.attach_banner.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("attach_banner",
                        CartAddBannerCriterion.TriggerInstance.usedBanner(ItemPredicate.Builder.item().of(ItemTags.BANNERS)))
                .save(consumer, NiftyCarts.MOD_ID + "/attach_banner");

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        NiftyCarts.REAPER.get(WoodType.OAK),
                        Component.translatable("advancements.niftycarts.reaper_harvest.title"),
                        Component.translatable("advancements.niftycarts.reaper_harvest.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "reaper_harvest",
                        ReaperHarvestCriterion.TriggerInstance.reaperHarvest(SimpleBlockPredicate.Builder.block().of(BlockTags.CROPS)))
                .save(consumer, NiftyCarts.MOD_ID + "/reaper_harvest");

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        NiftyCarts.HAND_CART.get(WoodType.OAK),
                        Component.translatable("advancements.niftycarts.hand_cart_one_k.title"),
                        Component.translatable("advancements.niftycarts.hand_cart_one_k.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("hand_cart_one_k",
                        PullCartCriterion.TriggerInstance.pullCart(EntityTypePredicate.of(NiftyCarts.HAND_CART_ENTITY), 1000_00, 0))
                .save(consumer, NiftyCarts.MOD_ID + "/hand_cart_one_k");

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        NiftyCarts.WAGON.get(WoodType.OAK),
                        Component.translatable("advancements.niftycarts.wagon_cart_full.title"),
                        Component.translatable("advancements.niftycarts.wagon_cart_full.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("wagon_cart_full",
                        PullCartCriterion.TriggerInstance.pullCart(EntityTypePredicate.of(NiftyCarts.WAGON_ENTITY), 1, 4))
                .save(consumer, NiftyCarts.MOD_ID + "/wagon_cart_full");

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        NiftyCarts.ANIMAL_CART.get(WoodType.OAK),
                        Component.translatable("advancements.niftycarts.animal_cart_steer.title"),
                        Component.translatable("advancements.niftycarts.animal_cart_steer.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("animal_cart_steer", SteerCartCriterion.TriggerInstance.steerCart(EntityTypePredicate.of(NiftyCarts.ANIMAL_CART_ENTITY), 1))
                .save(consumer, NiftyCarts.MOD_ID + "/animal_cart_steer");

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        NiftyCarts.SUPPLY_CART.get(WoodType.OAK),
                        Component.translatable("advancements.niftycarts.supply_cart_filled.title"),
                        Component.translatable("advancements.niftycarts.supply_cart_filled.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("supply_cart_filled",
                        PullCartCriterion.TriggerInstance.pullCartFill(EntityTypePredicate.of(NiftyCarts.SUPPLY_CART_ENTITY), 1.0f))
                .save(consumer, NiftyCarts.MOD_ID + "/supply_cart_filled");
    }
}
