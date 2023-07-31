package net.jmb19905.astikorcarts.config;

import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.data.ConfigOptions;
import net.jmb19905.astikorcarts.AstikorCarts;

public class ACConfig extends Config {

    @Transitive
    public final CartConfig supplyCart = new CartConfig();
    @Transitive
    public final CartConfig plowCart = new CartConfig();
    @Transitive
    public final CartConfig animalCart = new CartConfig();

    public ACConfig() {
        super(ConfigOptions.mod(AstikorCarts.MOD_ID));
    }

    @Transitive
    public static class CartConfig implements ConfigContainer {
        @ConfigEntry(comment =
                "Animals that are able to pull this cart, such as [\"minecraft:horse\"]\n" +
                "An empty list defaults to all which may wear a saddle but not steered by an item"
        )
        public String[] pullAnimals = {};

        @ConfigEntry(comment = "Slow speed modifier toggled by the sprint key")
        @ConfigEntry.BoundedDouble(min = -1.0, max = 0.0)
        public double slowSpeed = -0.65D;

        @ConfigEntry(comment = "Base speed modifier applied to animals (-0.5 = half normal speed)")
        @ConfigEntry.BoundedDouble(min = -1.0, max = 0.0)
        public double pullSpeed = 0.0D;
    }

}
