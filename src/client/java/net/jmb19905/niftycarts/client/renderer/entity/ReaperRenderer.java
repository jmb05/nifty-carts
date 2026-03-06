package net.jmb19905.niftycarts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.client.renderer.NiftyCartsModelLayers;
import net.jmb19905.niftycarts.client.renderer.entity.model.CartBannerFlagModel;
import net.jmb19905.niftycarts.client.renderer.entity.model.ReaperModel;
import net.jmb19905.niftycarts.entity.ReaperCartEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.banner.BannerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public final class ReaperRenderer extends DrawnRenderer<ReaperCartEntity, ReaperRenderState, ReaperModel> {

    public ReaperRenderer(final EntityRendererProvider.Context renderManager) {
        super(renderManager, new ReaperModel(renderManager.bakeLayer(NiftyCartsModelLayers.REAPER),
                new BannerModel(renderManager.bakeLayer(ModelLayers.STANDING_BANNER)),
                new CartBannerFlagModel(renderManager.bakeLayer(ModelLayers.STANDING_BANNER_FLAG))));
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
    public @NotNull Identifier getTextureLocation(ReaperRenderState state) {
        return NiftyCarts.resLoc("textures/entity/" + state.woodType.name() + "_reaper.png");
    }

    @Override
    protected void submitContents(ReaperRenderState state, PoseStack stack, SubmitNodeCollector collector) {}
}