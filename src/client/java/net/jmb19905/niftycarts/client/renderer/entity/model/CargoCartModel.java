package net.jmb19905.niftycarts.client.renderer.entity.model;

import net.jmb19905.niftycarts.client.renderer.entity.CargoCartRenderState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.model.object.banner.BannerModel;

public abstract class CargoCartModel<T extends CargoCartRenderState> extends CartModel<T> {

    protected final ModelPart flowerBasket;
    protected final ModelPart extraWheel;

    protected CargoCartModel(ModelPart root, BannerModel bannerModel, BannerFlagModel flagModel) {
        super(root, bannerModel, flagModel);
        this.extraWheel = root.getChild("body").getChild("extraWheel");
        this.flowerBasket = root.getChild("body").getChild("flowerBasket");
        this.flowerBasket.visible = false;
    }

    @Override
    public void setupAnim(T state) {
        super.setupAnim(state);
        this.extraWheel.xRot = 0.9F;
        this.extraWheel.zRot = (float) Math.PI * 0.3F;
        this.extraWheel.visible = false;
        this.extraWheel.visible = state.extraWheel;
        this.flowerBasket.visible = state.flowerBasket;
    }
}
