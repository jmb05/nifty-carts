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

    private static final class Holder {
        private static final Common COMMON;

        private static final ForgeConfigSpec COMMON_SPEC;

        static {
            final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
            COMMON = specPair.getLeft();
            COMMON_SPEC = specPair.getRight();
        }
    }

    public static class Common {
        public final CartConfig supplyCart;
        public final CartConfig animalCart;
        public final CartConfig plow;
        public final CartConfig handCart;

        Common(final ForgeConfigSpec.Builder builder) {
            builder.comment("Configuration for all carts and cart-like vehicles, check log for automatic \"pull_animals\" list.").push("carts");
            this.supplyCart = new CartConfig(builder, "supply_cart", "The Supply Cart, a type of cart that stores items");
            this.animalCart = new CartConfig(builder, "animal_cart", "The Animal Cart, a type of cart to haul other animals");
            this.plow = new CartConfig(builder, "plow", "The Plow, an animal pulled machine for tilling soil and creating paths");
            ArrayList<String> list = new ArrayList<>();
            list.add("minecraft:player");
            this.handCart = new CartConfig(builder, "handCart", "The Hand Cart, a player pulled cart that stores items", list, -0.3);
            builder.pop();
        }
    }

    public static class CartConfig {
        public final ForgeConfigSpec.ConfigValue<ArrayList<String>> pullEntities;
        public final ForgeConfigSpec.DoubleValue slowSpeed;
        public final ForgeConfigSpec.DoubleValue pullSpeed;

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
            builder.pop();
        }
    }
}