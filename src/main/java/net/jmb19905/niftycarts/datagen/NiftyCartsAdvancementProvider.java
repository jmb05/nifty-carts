package net.jmb19905.niftycarts.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsWoodType;
import net.jmb19905.niftycarts.advancement.SimpleBlockPredicate;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class NiftyCartsAdvancementProvider extends FabricAdvancementProvider {

    protected NiftyCartsAdvancementProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {
        Advancement root = Advancement.Builder.advancement()
                .display(
                        NiftyCarts.WHEEL,
                        Component.translatable("advancements.niftycarts.place_cart.title"),
                        Component.translatable("advancements.niftycarts.place_cart.description"),
                        new ResourceLocation("textures/gui/advancements/backgrounds/husbandry.png"),
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("place_cart", NiftyCarts.PLACE_CART_CRITERION.create(ItemPredicate.ANY))
                .save(consumer, NiftyCarts.MOD_ID + "/root");

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        NiftyCarts.SEED_DRILL.get(NiftyCartsWoodType.OAK),
                        Component.translatable("advancements.niftycarts.seed_drill_plant.title"),
                        Component.translatable("advancements.niftycarts.seed_drill_plant.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("seed_drill_plant", NiftyCarts.SEED_DRILL_PLACE_CRITERION.create(ItemPredicate.ANY))
                .save(consumer, NiftyCarts.MOD_ID + "/seed_drill_plant_seed");

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        NiftyCarts.PLOW.get(NiftyCartsWoodType.OAK),
                        Component.translatable("advancements.niftycarts.plow_till_ground.title"),
                        Component.translatable("advancements.niftycarts.plow_till_ground.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "plow_till_ground",
                        NiftyCarts.USE_PLOW_CRITERION.create(ItemPredicate.Builder.item().of(ItemTags.HOES).build()))
                .save(consumer, NiftyCarts.MOD_ID + "/plow_till_ground");

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.DIRT_PATH,
                        Component.translatable("advancements.niftycarts.plow_create_path.title"),
                        Component.translatable("advancements.niftycarts.plow_create_path.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "plow_create_path",
                        NiftyCarts.USE_PLOW_CRITERION.create(ItemPredicate.Builder.item().of(ItemTags.SHOVELS).build()))
                .save(consumer, NiftyCarts.MOD_ID + "/plow_create_path");

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.STRIPPED_OAK_LOG,
                        Component.translatable("advancements.niftycarts.plow_strip_log.title"),
                        Component.translatable("advancements.niftycarts.plow_strip_log.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "plow_strip_log",
                        NiftyCarts.USE_PLOW_CRITERION.create(ItemPredicate.Builder.item().of(ItemTags.AXES).build()))
                .save(consumer, NiftyCarts.MOD_ID + "/plow_strip_log");

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.RED_BANNER,
                        Component.translatable("advancements.niftycarts.attach_banner.title"),
                        Component.translatable("advancements.niftycarts.attach_banner.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("attach_banner", NiftyCarts.CART_ADD_BANNER_CRITERION.create(ItemPredicate.Builder.item().of(ItemTags.BANNERS).build()))
                .save(consumer, NiftyCarts.MOD_ID + "/attach_banner");

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        NiftyCarts.REAPER.get(NiftyCartsWoodType.OAK),
                        Component.translatable("advancements.niftycarts.reaper_harvest.title"),
                        Component.translatable("advancements.niftycarts.reaper_harvest.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "reaper_harvest",
                        NiftyCarts.REAPER_HARVEST_CRITERION.create(SimpleBlockPredicate.Builder.block().of(BlockTags.CROPS).build()))
                .save(consumer, NiftyCarts.MOD_ID + "/reaper_harvest");

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        NiftyCarts.HAND_CART.get(NiftyCartsWoodType.OAK),
                        Component.translatable("advancements.niftycarts.hand_cart_one_k.title"),
                        Component.translatable("advancements.niftycarts.hand_cart_one_k.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("hand_cart_one_k", NiftyCarts.PULL_HAND_CART_CRITERION.create(1000_00, 0))
                .save(consumer, NiftyCarts.MOD_ID + "/hand_cart_one_k");

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        NiftyCarts.WAGON.get(NiftyCartsWoodType.OAK),
                        Component.translatable("advancements.niftycarts.wagon_cart_full.title"),
                        Component.translatable("advancements.niftycarts.wagon_cart_full.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("wagon_cart_full", NiftyCarts.PULL_WAGON_CRITERION.create(1, 4))
                .save(consumer, NiftyCarts.MOD_ID + "/wagon_cart_full");

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        NiftyCarts.ANIMAL_CART.get(NiftyCartsWoodType.OAK),
                        Component.translatable("advancements.niftycarts.animal_cart_steer.title"),
                        Component.translatable("advancements.niftycarts.animal_cart_steer.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("animal_cart_steer", NiftyCarts.STEER_ANIMAL_CART_CRITERION.create(1))
                .save(consumer, NiftyCarts.MOD_ID + "/animal_cart_steer");

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        NiftyCarts.SUPPLY_CART.get(NiftyCartsWoodType.OAK),
                        Component.translatable("advancements.niftycarts.supply_cart_filled.title"),
                        Component.translatable("advancements.niftycarts.supply_cart_filled.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("supply_cart_filled", NiftyCarts.PULL_FILLED_CART_CRITERION.create(NiftyCarts.SUPPLY_CART_ENTITY, 1.0f))
                .save(consumer, NiftyCarts.MOD_ID + "/supply_cart_filled");
    }
}
