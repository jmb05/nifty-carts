package net.jmb19905.astikorcarts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.jmb19905.astikorcarts.entity.PostilionEntity;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class PostilionRenderer extends EntityRenderer<PostilionEntity> {

    public PostilionRenderer(final EntityRendererProvider.Context manager) {
        super(manager);
    }

    @Override
    public void render(final PostilionEntity postilion, final float yaw, final float delta, final PoseStack stack, final MultiBufferSource source, final int packedLight) {
        if (!postilion.isInvisible()) {
            stack.pushPose();
            stack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
            final AABB bounds = postilion.getBoundingBox().move(-postilion.getX(), -postilion.getY(), -postilion.getZ());
            LevelRenderer.renderLineBox(stack, source.getBuffer(RenderType.lines()), bounds, 1.0F, 1.0F, 1.0F, 1.0F);
            stack.popPose();
            super.render(postilion, yaw, delta, stack, source, packedLight);
        }
    }

    @Override
    protected boolean shouldShowName(final PostilionEntity postilion) {
        return true;
    }

    @Nullable
    @Override
    public ResourceLocation getTextureLocation(final PostilionEntity postilion) {
        return null;
    }
}
