package net.jmb19905.niftycarts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.client.renderer.NiftyCartsModelLayers;
import net.jmb19905.niftycarts.client.renderer.entity.model.AnimalCartModel;
import net.jmb19905.niftycarts.entity.AnimalCartEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public final class AnimalCartRenderer extends DrawnRenderer<AnimalCartEntity, AnimalCartModel> {
    //This texture is not a real file it is assembled during resource loading
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(NiftyCarts.MOD_ID, "textures/entity/animal_cart.png");

    public AnimalCartRenderer(final EntityRendererProvider.Context renderManager) {
        super(renderManager, new AnimalCartModel(renderManager.bakeLayer(NiftyCartsModelLayers.ANIMAL_CART)));
        this.shadowRadius = 1.0F;
    }

    @Override
    protected void renderContents(final AnimalCartEntity entity, final float delta, final PoseStack stack, final MultiBufferSource source, final int packedLight) {
        if (entity.getBannerColor() != null) {
            stack.pushPose();
            this.model.getBody().translateAndRotate(stack);
            stack.translate(0.0D, -0.6D, 1.56D);
            this.renderBanner(entity, stack, source, delta, packedLight, entity.getBannerColor(), entity.getBannerPattern());
            stack.popPose();
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(final AnimalCartEntity entity) {
        return TEXTURE;
    }
}
