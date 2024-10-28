package net.jmb19905.niftycarts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.jmb19905.niftycarts.entity.PostilionEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jetbrains.annotations.NotNull;

public class PostilionRenderer extends EntityRenderer<PostilionEntity, EntityRenderState> {

    public PostilionRenderer(final EntityRendererProvider.Context manager) {
        super(manager);
    }

    @Override
    public @NotNull EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void render(EntityRenderState state, PoseStack stack, MultiBufferSource source, int light) {}

    @Override
    protected boolean shouldShowName(PostilionEntity entity, double d) {
        return true;
    }
}
