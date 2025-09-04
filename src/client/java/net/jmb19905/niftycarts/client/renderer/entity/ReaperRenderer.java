package net.jmb19905.niftycarts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.client.renderer.NiftyCartsModelLayers;
import net.jmb19905.niftycarts.client.renderer.entity.model.ReaperModel;
import net.jmb19905.niftycarts.entity.ReaperCartEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public final class ReaperRenderer extends DrawnRenderer<ReaperCartEntity, ReaperRenderState, ReaperModel> {

    public ReaperRenderer(final EntityRendererProvider.Context renderManager) {
        super(renderManager, new ReaperModel(renderManager.bakeLayer(NiftyCartsModelLayers.REAPER)));
        this.shadowRadius = 1.0F;
    }

    @Override
    public void extractRenderState(ReaperCartEntity entity, ReaperRenderState state, float delta) {
        super.extractRenderState(entity, state, delta);
        state.folded = entity.isFolded();
    }

    @Override
    public @NotNull ReaperRenderState createRenderState() {
        return new ReaperRenderState();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(ReaperRenderState state) {
        return NiftyCarts.resLoc("textures/entity/" + state.woodType.name() + "_reaper.png");
    }

    @Override
    protected void renderContents(ReaperRenderState state, PoseStack stack, MultiBufferSource source, int packedLight) {}
}