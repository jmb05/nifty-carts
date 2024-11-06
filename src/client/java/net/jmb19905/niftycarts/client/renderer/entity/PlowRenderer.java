package net.jmb19905.niftycarts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.client.renderer.NiftyCartsModelLayers;
import net.jmb19905.niftycarts.client.renderer.entity.model.PlowModel;
import net.jmb19905.niftycarts.entity.PlowEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class PlowRenderer extends DrawnRenderer<PlowEntity, PlowRenderState, PlowModel> {
    //This texture is not a real file it is assembled during resource loading
    private static final ResourceLocation TEXTURE = NiftyCarts.resLoc("textures/entity/plow.png");
    private final ItemRenderer itemRenderer;

    public PlowRenderer(final EntityRendererProvider.Context ctx) {
        super(ctx, new PlowModel(ctx.bakeLayer(NiftyCartsModelLayers.PLOW)));
        this.shadowRadius = 1.0F;
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void extractRenderState(PlowEntity entity, PlowRenderState state, float delta) {
        super.extractRenderState(entity, state, delta);
        state.plowing = entity.getPlowing();
        state.level = entity.level();
        state.items = NonNullList.create();
        for (int i = 0; i < entity.getItemStacks().size(); i++) {
            state.items.add(i, entity.getStackInSlot(i));
        }
    }

    @Override
    public @NotNull PlowRenderState createRenderState() {
        return new PlowRenderState();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(PlowRenderState state) {
        return TEXTURE;
    }

    @Override
    protected void renderContents(PlowRenderState state, final PoseStack stack, final MultiBufferSource source, final int packedLight) {
        for (int i = 0; i < state.items.size(); i++) {
            final ItemStack itemStack = state.items.get(i);
            if (itemStack.isEmpty()) {
                continue;
            }
            this.attach(this.model.getBody(), this.model.getShaft(i), s -> {
                s.mulPose(Axis.XP.rotationDegrees(-90.0F));
                s.mulPose(Axis.YP.rotationDegrees(90.0F));
                s.translate(-4.0D / 16.0D, 1.0D / 16.0D, 0.0D);
                if (itemStack.getItem() instanceof BlockItem) {
                    s.translate(0.0D, -0.1D, 0.0D);
                    s.mulPose(Axis.ZP.rotationDegrees(180.0F));
                }
                itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, stack, source, state.level, 0);
            }, stack);
        }
    }
}