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
        public final ForgeConfigSpec.ConfigValue<ArrayList<String>> renderBlacklist;

        Client(final ForgeConfigSpec.Builder builder) {
            builder.comment("Configuration to disable the rendering of certain supplies in the supply cart");
            this.renderSupplies = builder.comment("Enables/Disables the rendering of all supplies")
                    .define("render_supplies", true);
            this.renderSupplyGear = builder.comment("Falls back to rendering as items if false").define("render_supply_gear", true);
            this.renderSupplyFlowers = builder.comment("Falls back to rendering as items if false").define("render_supply_flowers", true);
            this.renderSupplyPaintings = builder.comment("Falls back to rendering as items if false").define("render_supply_paintings", true);
            this.renderSupplyWheel = builder.comment("Falls back to rendering as items if false").define("render_supply_wheel", true);
            ArrayList<String> blacklist = new ArrayList<>();
            blacklist.add("minecraft:trident");
            blacklist.add("minecraft:decorated_pot");
            blacklist.add("#minecraft:buttons");
            blacklist.add("#minecraft:banners");
            this.renderBlacklist = builder.comment("Disables rendering for these blocks and items").define("render_item_blacklist", blacklist);
        }

    }

    public static class Common {
        public final CartConfig supplyCart;
        public final CartConfig animalCart;
        public final CartConfig plow;
        public final CartConfig handCart;
        public final CartConfig seedDrill;
        public final CartConfig reaper;
        public final CartConfig wagon;

        Common(final ForgeConfigSpec.Builder builder) {
            builder.comment("Configuration for all carts and cart-like vehicles, check log for automatic \"pull_animals\" list.").push("carts");
            this.supplyCart = new CartConfig(builder, "supply_cart", "The Supply Cart, a type of cart that stores items");
            this.animalCart = new CartConfig(builder, "animal_cart", "The Animal Cart, a type of cart to haul other animals");
            this.plow = new CartConfig(builder, "plow", "The Plow, an animal pulled machine for tilling soil and creating paths");
            ArrayList<String> list = new ArrayList<>();
            list.add("minecraft:player");
            this.handCart = new CartConfig(builder, "handCart", "The Hand Cart, a player pulled cart that stores items", list, 0);
            this.seedDrill = new CartConfig(builder, "seedDrill", "The Seed Drill, a type of cart that plants crops");
            this.reaper = new CartConfig(builder, "reaper", "The Reaper, a type of cart that harvests crops");
            this.wagon = new CartConfig(builder, "wagon", "The Covered wagon, a horse drawn cart that multiple people and/or lots of items", new ArrayList<>(), -0.2);
            builder.pop();
        }
    }

    public static class CartConfig {
        public final ForgeConfigSpec.ConfigValue<ArrayList<String>> pullEntities;
        public final ForgeConfigSpec.DoubleValue slowSpeed;
        public final ForgeConfigSpec.DoubleValue pullSpeed;
        public final ForgeConfigSpec.BooleanValue adventureModeInteract;

        CartConfig(final ForgeConfigSpec.Builder builder, final String name, final String description) {
            this(builder, name, description, new ArrayList<>(), 0);
        }

        CartConfig(final ForgeConfigSpec.Builder builder, final String name, final String description, ArrayList<String> defaultEntityList, double defaultPullSpeed) {
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
            this.adventureModeInteract = builder.comment("Players in adventure mode can interact with cart")
                    .define("adventure_mode_interact", true);
            builder.pop();
        }
    }
}