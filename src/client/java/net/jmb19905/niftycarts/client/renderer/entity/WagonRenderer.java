package net.jmb19905.niftycarts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.client.renderer.NiftyCartsModelLayers;
import net.jmb19905.niftycarts.client.renderer.entity.model.CartBannerFlagModel;
import net.jmb19905.niftycarts.client.renderer.entity.model.WagonModel;
import net.jmb19905.niftycarts.entity.WagonEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.banner.BannerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class WagonRenderer extends DrawnRenderer<WagonEntity, WagonRenderState, WagonModel> {

    public WagonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WagonModel(
                renderManager.bakeLayer(NiftyCartsModelLayers.WAGON),
                renderManager.bakeLayer(NiftyCartsModelLayers.WAGON_ROOF),
                renderManager.bakeLayer(NiftyCartsModelLayers.WAGON_CHEST),
                new BannerModel(renderManager.bakeLayer(ModelLayers.STANDING_BANNER)),
                new CartBannerFlagModel(renderManager.bakeLayer(ModelLayers.STANDING_BANNER_FLAG))
        ));
    }

    @Override
    public void extractRenderState(WagonEntity entity, WagonRenderState state, float delta) {
        super.extractRenderState(entity, state, delta);
        state.maxChestCount = entity.getMaxChestCount();
        state.chestCount = entity.getChestCount();
        state.hasRoof = entity.hasRoof();
        state.unfurled = entity.getUnfurled();
        state.roofTexture = entity.getRoofTexture();
    }

    @Override
    protected void submitContents(WagonRenderState state, PoseStack stack, SubmitNodeCollector collector) {
        stack.pushPose();
        this.model.getBody().translateAndRotate(stack);
        stack.pushPose();
        for (int i = 0; i < state.maxChestCount; i++) {
            this.model.getChest(i).visible = i < state.chestCount;
        }
        collector.submitModelPart(this.model.getChests(), stack, this.model.renderType(NiftyCarts.resLoc("textures/entity/wagon_chest.png")), state.lightCoords, OverlayTexture.NO_OVERLAY, null);
        stack.popPose();
        if (state.hasRoof) {
            collector.submitModelPart(this.model.getRoof(state.unfurled), stack, this.model.renderType(state.roofTexture), state.lightCoords, OverlayTexture.NO_OVERLAY, null);
        }
        if (state.bannerColor != null) {
            stack.pushPose();
            stack.translate(0.0D, -0.58D, 2.62D);
            this.submitBanner(state, stack, collector);
            stack.popPose();
        }
        stack.popPose();
    }

    @Override
    public @NotNull WagonRenderState createRenderState() {
        return new WagonRenderState();
    }

    @Override
    public @NotNull Identifier getTextureLocation(WagonRenderState state) {
        return NiftyCarts.resLoc("textures/entity/" + state.woodType.name() + "_wagon.png");
    }
}