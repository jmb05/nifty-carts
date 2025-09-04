package net.jmb19905.niftycarts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.client.renderer.NiftyCartsModelLayers;
import net.jmb19905.niftycarts.client.renderer.entity.model.WagonModel;
import net.jmb19905.niftycarts.entity.WagonEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class WagonRenderer extends DrawnRenderer<WagonEntity, WagonRenderState, WagonModel> {

    public WagonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WagonModel(
                renderManager.bakeLayer(NiftyCartsModelLayers.WAGON),
                renderManager.bakeLayer(NiftyCartsModelLayers.WAGON_ROOF),
                renderManager.bakeLayer(NiftyCartsModelLayers.WAGON_CHEST)
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
    protected void renderContents(WagonRenderState state, PoseStack stack, MultiBufferSource source, int packedLight) {
        stack.pushPose();
        this.model.getBody().translateAndRotate(stack);
        stack.pushPose();
        for (int i = 0; i < state.maxChestCount; i++) {
            this.model.getChest(i).visible = i < state.chestCount;
        }
        this.model.getChests().render(stack, source.getBuffer(this.model.renderType(ResourceLocation.fromNamespaceAndPath(NiftyCarts.MOD_ID, "textures/entity/wagon_chest.png"))), packedLight, OverlayTexture.NO_OVERLAY);
        stack.popPose();
        if (state.hasRoof) {
            model.getRoof(state.unfurled).render(stack, source.getBuffer(this.model.renderType(state.roofTexture)), packedLight, OverlayTexture.NO_OVERLAY);
        }
        stack.popPose();
    }

    @Override
    public @NotNull WagonRenderState createRenderState() {
        return new WagonRenderState();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(WagonRenderState state) {
        return ResourceLocation.fromNamespaceAndPath(NiftyCarts.MOD_ID, "textures/entity/" + state.woodType.name() + "_wagon.png");
    }
}