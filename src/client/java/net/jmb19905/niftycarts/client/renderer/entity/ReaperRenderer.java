package net.jmb19905.niftycarts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.client.renderer.NiftyCartsModelLayers;
import net.jmb19905.niftycarts.client.renderer.entity.model.ReaperModel;
import net.jmb19905.niftycarts.entity.ReaperEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ReaperRenderer extends DrawnRenderer<ReaperEntity, ReaperModel> {

    public ReaperRenderer(final EntityRendererProvider.Context renderManager) {
        super(renderManager, new ReaperModel(renderManager.bakeLayer(NiftyCartsModelLayers.REAPER)));
        this.shadowRadius = 1.0F;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(final ReaperEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NiftyCarts.MOD_ID, "textures/entity/" + entity.getWoodType().name() + "_reaper.png");
    }

    @Override
    protected void renderContents(ReaperEntity entity, float delta, PoseStack stack, MultiBufferSource source, int packedLight) {}
}