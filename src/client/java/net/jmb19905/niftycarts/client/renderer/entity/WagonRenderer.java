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

public class WagonRenderer extends DrawnRenderer<WagonEntity, WagonModel> {

    public WagonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WagonModel(
                renderManager.bakeLayer(NiftyCartsModelLayers.WAGON),
                renderManager.bakeLayer(NiftyCartsModelLayers.WAGON_ROOF),
                renderManager.bakeLayer(NiftyCartsModelLayers.WAGON_CHEST)
        ));
    }

    @Override
    protected void renderContents(WagonEntity entity, float delta, PoseStack stack, MultiBufferSource source, int packedLight) {
        stack.pushPose();
        this.model.getBody().translateAndRotate(stack);
        stack.pushPose();
        for (int i = 0; i < entity.getMaxChestCount(); i++) {
            this.model.getChest(i).visible = i < entity.getChestCount();
        }
        this.model.getChests().render(stack, source.getBuffer(this.model.renderType(new ResourceLocation(NiftyCarts.MOD_ID, "textures/entity/wagon_chest.png"))), packedLight, OverlayTexture.NO_OVERLAY);
        stack.popPose();
        if (entity.hasRoof()) {
            model.getRoof(entity.getUnfurled()).render(stack, source.getBuffer(this.model.renderType(entity.getRoofTexture())), packedLight, OverlayTexture.NO_OVERLAY);
        }
        stack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(WagonEntity entity) {
        return new ResourceLocation(NiftyCarts.MOD_ID, "textures/entity/" + entity.getWoodType().getId() + "_wagon.png");
    }
}