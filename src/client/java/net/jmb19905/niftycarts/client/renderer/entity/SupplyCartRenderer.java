package net.jmb19905.niftycarts.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.entity.SupplyCartEntity;
import net.jmb19905.niftycarts.client.renderer.NiftyCartsModelLayers;
import net.jmb19905.niftycarts.client.renderer.entity.model.SupplyCartModel;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public final class SupplyCartRenderer extends CargoCartRenderer<SupplyCartEntity, SupplyCartModel> {

    public SupplyCartRenderer(final EntityRendererProvider.Context ctx) {
        super(ctx, new SupplyCartModel(ctx.bakeLayer(NiftyCartsModelLayers.SUPPLY_CART)));
    }

    @Override
    protected double getFlowerOffsetZ() {
        return -3.0D / 16.0;
    }

    @Override
    protected Vec3 getWheelOffset() {
        return new Vec3(1.18D, 0.1D, -0.15D);
    }

    @Override
    protected Vec3 getPaintingOffset(int i, int n, int count) {
        return new Vec3(0.0D, 0.5 * (n - (count - 1) * 0.1D) / count, -1.0D / 16.0D * i);
    }

    @Override
    protected float getPaintingAngleFactor() {
        return 1;
    }

    @Override
    protected double getSuppliesOffsetX(int x) {
        return (x - 0.5D) * 11.0D / 16.0D;
    }

    @Override
    protected double getSuppliesOffsetZ(int z) {
        return (z * 11.0D - 9.0D) / 16.0D;
    }

    @Override
    protected float getBlockSize() {
        return 0.65F;
    }

    @Override
    protected float getArmorSize() {
        return 1f;
    }

    @Override
    protected double getShieldOffsetY() {
        return 1.2;
    }

    @Override
    protected float getItemSize() {
        return 0.7f;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(CargoCartRenderState state) {
        return NiftyCarts.resLoc("textures/entity/" + state.woodType.name() + "_supply_cart.png");
    }
}