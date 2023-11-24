package net.jmb19905.niftycarts.client.renderer;

import net.jmb19905.niftycarts.NiftyCarts;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class NiftyCartsModelLayers {
    public static final ModelLayerLocation ANIMAL_CART = main("animal_cart");
    public static final ModelLayerLocation PLOW = main("plow");
    public static final ModelLayerLocation SUPPLY_CART = main("supply_cart");
    public static final ModelLayerLocation HAND_CART = main("hand_cart");

    @SuppressWarnings("ConfusingMainMethod")
    private static ModelLayerLocation main(String name) {
        return layer(name, "main");
    }

    @SuppressWarnings("SameParameterValue")
    private static ModelLayerLocation layer(String name, String layer) {
        return new ModelLayerLocation(new ResourceLocation(NiftyCarts.MOD_ID, name), layer);
    }
}
