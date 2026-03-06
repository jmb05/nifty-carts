package net.jmb19905.niftycarts.advancement;

import net.jmb19905.niftycarts.NiftyCarts;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class NCCriteriaTriggers {

    public static CartAddBannerCriterion CART_ADD_BANNER;
    public static PlaceCartItemCriterion PLACE_CART_ITEM;
    public static PullCartCriterion PULL_CART;
    public static ReaperHarvestCriterion REAPER_HARVEST;
    public static SeedDrillPlaceCriterion SEED_DRILL_PLACE;
    public static SteerCartCriterion STEER_CART;
    public static UsePlowCriterion USE_PLOW;

    private static <T extends CriterionTrigger<?>> T register(String string, T criterionTrigger) {
        return Registry.register(BuiltInRegistries.TRIGGER_TYPES, Identifier.fromNamespaceAndPath(NiftyCarts.MOD_ID, string), criterionTrigger);
    }

    public static void register() {
        CART_ADD_BANNER = register("cart_add_banner", new CartAddBannerCriterion());
        PLACE_CART_ITEM = register("place_cart_item", new PlaceCartItemCriterion());
        PULL_CART = register("pull_cart", new PullCartCriterion());
        REAPER_HARVEST = register("reaper_harvest", new ReaperHarvestCriterion());
        SEED_DRILL_PLACE = register("seed_drill_place", new SeedDrillPlaceCriterion());
        STEER_CART = register("steer_cart", new SteerCartCriterion());
        USE_PLOW = register("use_plow", new UsePlowCriterion());
    }

}