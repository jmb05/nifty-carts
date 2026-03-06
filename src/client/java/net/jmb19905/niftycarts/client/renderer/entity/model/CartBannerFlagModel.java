package net.jmb19905.niftycarts.client.renderer.entity.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class CartBannerFlagModel extends BannerFlagModel {
    public CartBannerFlagModel(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(@NotNull Float sway) {
        super.setupAnim(sway);
        this.flag.xRot = (0.01F * Mth.cos(Mth.TWO_PI * sway)) * Mth.PI;
    }
}
