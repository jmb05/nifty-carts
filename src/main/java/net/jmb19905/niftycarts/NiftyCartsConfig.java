package net.jmb19905.niftycarts;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

public final class NiftyCartsConfig {
    public static Common get() {
        return Holder.COMMON;
    }

    public static ForgeConfigSpec spec() {
        return Holder.COMMON_SPEC;
    }

    public static Client getClient() {
        return Holder.CLIENT;
    }

    public static ForgeConfigSpec clientSpec() {
        return Holder.CLIENT_SPEC;
    }

    private static final class Holder {
        private static final Common COMMON;

        private static final ForgeConfigSpec COMMON_SPEC;

        private static final Client CLIENT;
        private static final ForgeConfigSpec CLIENT_SPEC;

        static {
            final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
            COMMON = specPair.getLeft();
            COMMON_SPEC = specPair.getRight();
            final Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
            CLIENT = clientSpecPair.getLeft();
            CLIENT_SPEC = clientSpecPair.getRight();
        }
    }


    public static class Client {
        public final ForgeConfigSpec.BooleanValue renderSupplies;
        public final ForgeConfigSpec.BooleanValue renderSupplyGear;
        public final ForgeConfigSpec.BooleanValue renderSupplyFlowers;
        public final ForgeConfigSpec.BooleanValue renderSupplyPaintings;
        public final ForgeConfigSpec.BooleanValue renderSupplyWheel;
        public final ForgeConfigSpec.BooleanValue renderDebugBoxes;

        Client(final ForgeConfigSpec.Builder builder) {
            builder.comment("Configuration to disable the rendering of certain supplies in the supply cart");
            this.renderSupplies = builder.comment("Enables/Disables the rendering of all supplies")
                    .define("render_supplies", true);
            this.renderSupplyGear = builder.comment("Falls back to rendering as items if false").define("render_supply_gear", true);
            this.renderSupplyFlowers = builder.comment("Falls back to rendering as items if false").define("render_supply_flowers", true);
            this.renderSupplyPaintings = builder.comment("Falls back to rendering as items if false").define("render_supply_paintings", true);
            this.renderSupplyWheel = builder.comment("Falls back to rendering as items if false").define("render_supply_wheel", true);
            this.renderDebugBoxes = builder.comment("Render boxes used for debugging the carts when Hitbox rendering is enabled").define("render_debug_boxes", false);
        }

    }


    public static class Common {
        public final CartConfig supplyCart;
        public final CartConfig handCart;
        public final CartConfig animalCart;
        public final CartConfig plow;
        public final CartConfig seedDrill;
        public final CartConfig reaper;
        public final CartConfig wagon;

        Common(final ForgeConfigSpec.Builder builder) {
            builder.comment("Configuration for all carts and cart-like vehicles, check log for automatic \"pull_animals\" list.").push("carts");
            this.supplyCart = new CartConfig(builder, "supply_cart", "The Supply Cart, a type of cart that stores items");
            this.supplyCart.pop();
            ArrayList<String> list = new ArrayList<>();
            list.add("minecraft:player");
            this.handCart = new CartConfig(builder, "handCart", "The Hand Cart, a player pulled cart that stores items", list, -0.1);
            this.handCart.pop();
            this.animalCart = new CartConfig(builder, "animal_cart", "The Animal Cart, a type of cart to haul other animals");
            this.animalCart.pop();
            this.plow = new CartConfig(builder, "plow", "The Plow, an animal pulled machine for tilling soil and creating paths");
            this.plow.pop();
            this.seedDrill = new CartConfig(builder, "seed_drill", "The Seed Drill, a cart that plants crops");
            this.seedDrill.pop();
            this.reaper = new CartConfig(builder, "reaper", "The Reaper, a cart that harvests crops");
            this.reaper.pop();
            this.wagon = new CartConfig(builder, "wagon", "The Covered wagon, a horse drawn cart that multiple people and/or lots of items", new ArrayList<>(), -0.2f);
            this.wagon.pop();
            builder.pop();
        }
    }

    public static class CartConfig {
        public final ForgeConfigSpec.ConfigValue<ArrayList<String>> pullEntities;
        public final ForgeConfigSpec.DoubleValue slowSpeed;
        public final ForgeConfigSpec.DoubleValue pullSpeed;
        public final ForgeConfigSpec.IntValue destroyDamage;
        public final ForgeConfigSpec.BooleanValue adventureModeInteract;
        private final ForgeConfigSpec.Builder builder;

        CartConfig(final ForgeConfigSpec.Builder builder, final String name, final String description) {
            this(builder, name, description, new ArrayList<>(), -0.1);
        }

        CartConfig(final ForgeConfigSpec.Builder builder, final String name, final String description, ArrayList<String> defaultEntityList, double defaultPullSpeed) {
            this.builder = builder;
            builder.comment(description).push(name);
            this.pullEntities = builder
                    .comment(
                            "Entity that are able to pull this cart, such as [\"minecraft:horse\"]\n" +
                                    "An empty list defaults to all which may wear a saddle but not steered by an item"
                    )
                    .define("pull_animals", defaultEntityList);
            this.slowSpeed = builder.comment("Slow speed modifier toggled by the sprint key")
                    .defineInRange("slow_speed", -0.65D, -1.0D, 0.0D);
            this.pullSpeed = builder.comment("Base speed modifier applied to animals (-0.5 = half normal speed)")
                    .defineInRange("pull_speed", 0.0D, -1.0D, defaultPullSpeed);
            this.destroyDamage = builder.comment("Damage needed to destroy the cart. Damage accumulates over time but decays at a rate of 2 damage per second.")
                    .defineInRange("destroy_damage", 4, 1, 100);
            this.adventureModeInteract = builder.comment("Players in adventure mode can interact with cart")
                    .define("adventure_mode_interact", true);
        }

        protected void pop() {
            builder.pop();
        }
    }

}