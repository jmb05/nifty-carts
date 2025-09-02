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
        for (int i = 0; i < entity.getChestCount(); i++) {
            this.model.getChest().render(stack, source.getBuffer(this.model.renderType(ResourceLocation.fromNamespaceAndPath(NiftyCarts.MOD_ID, "textures/entity/wagon_chest.png"))), packedLight, OverlayTexture.NO_OVERLAY);
            stack.translate(0, 0, 1);
        }
        stack.popPose();
        if (entity.hasRoof()) {
            model.getRoof(entity.getUnfurled()).render(stack, source.getBuffer(this.model.renderType(entity.getRoofTexture())), packedLight, OverlayTexture.NO_OVERLAY);
        }
        stack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(WagonEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NiftyCarts.MOD_ID, "textures/entity/" + entity.getWoodType().name() + "_wagon.png");
    }
}
