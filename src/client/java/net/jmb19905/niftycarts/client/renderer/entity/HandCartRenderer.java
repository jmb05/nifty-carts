package net.jmb19905.niftycarts.client.renderer.entity;

import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.client.renderer.NiftyCartsModelLayers;
import net.jmb19905.niftycarts.client.renderer.entity.model.HandCartModel;
import net.jmb19905.niftycarts.entity.HandCartEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class HandCartRenderer extends CargoCartRenderer<HandCartEntity, HandCartModel> {

    public HandCartRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HandCartModel(ctx.bakeLayer(NiftyCartsModelLayers.HAND_CART)));
    }

    @Override
    protected double getFlowerOffsetZ() {
        return -1.0D / 16.0D;
    }

    @Override
    protected Vec3 getWheelOffset() {
        return new Vec3(0.91D, 0.05D, -0.15D);
    }

    @Override
    protected Vec3 getPaintingOffset(int i, int n, int count) {
        return new Vec3(0.0D, 0.03D, -1D / 16.0D * i + 0.0001f);
    }

    @Override
    protected float getPaintingAngleFactor() {
        return 0.2f;
    }

    @Override
    protected double getSuppliesOffsetX(int x) {
        return ((x * 2 - 1) * 4) / 16.0D;
    }

    @Override
    protected double getSuppliesOffsetZ(int z) {
        return ((z * 2 - 1) * 5) / 16.0D;
    }

    @Override
    protected float getBlockSize() {
        return 0.5f;
    }

    @Override
    protected float getArmorSize() {
        return 0.9f;
    }

    @Override
    protected double getShieldOffsetY() {
        return 1.2D;
    }

    @Override
    protected float getItemSize() {
        return 0.6f;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(CargoCartRenderState state) {
        return NiftyCarts.resLoc("textures/entity/" + state.woodType.name() + "_hand_cart.png");
    }
}