package net.jmb19905.niftycarts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.client.renderer.NiftyCartsModelLayers;
import net.jmb19905.niftycarts.client.renderer.entity.model.AnimalCartModel;
import net.jmb19905.niftycarts.entity.AnimalCartEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class AnimalCartRenderer extends DrawnRenderer<AnimalCartEntity, AnimalCartModel> {
    //This texture is not a real file it is assembled during resource loading
    private static final ResourceLocation TEXTURE = new ResourceLocation(NiftyCarts.MOD_ID, "textures/entity/animal_cart.png");

    public AnimalCartRenderer(final EntityRendererProvider.Context renderManager) {
        super(renderManager, new AnimalCartModel(renderManager.bakeLayer(NiftyCartsModelLayers.ANIMAL_CART)));
        this.shadowRadius = 1.0F;
    }

    @Override
    protected void renderContents(final AnimalCartEntity entity, final float delta, final PoseStack stack, final MultiBufferSource source, final int packedLight) {
        final List<Pair<Holder<BannerPattern>, DyeColor>> list = entity.getBannerPattern();
        if (!list.isEmpty()) {
            stack.pushPose();
            this.model.getBody().translateAndRotate(stack);
            stack.translate(0.0D, -0.6D, 1.56D);
            this.renderBanner(stack, source, packedLight, list);
            stack.popPose();
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(final AnimalCartEntity entity) {
        return TEXTURE;
    }
}
