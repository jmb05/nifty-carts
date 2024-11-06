package net.jmb19905.niftycarts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.client.renderer.NiftyCartsModelLayers;
import net.jmb19905.niftycarts.client.renderer.entity.model.SeedDrillModel;
import net.jmb19905.niftycarts.entity.SeedDrillEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

public final class SeedDrillRenderer extends DrawnRenderer<SeedDrillEntity, SeedDrillModel> {

    private final Random random = new Random();

    public SeedDrillRenderer(final EntityRendererProvider.Context renderManager) {
        super(renderManager, new SeedDrillModel(renderManager.bakeLayer(NiftyCartsModelLayers.SEED_DRILL)));
        this.shadowRadius = 1.0F;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(final SeedDrillEntity entity) {
        return new ResourceLocation(NiftyCarts.MOD_ID, "textures/entity/" + entity.getWoodType().getId() + "_seed_drill.png");
    }

    @Override
    protected void renderContents(final SeedDrillEntity entity, final float delta, final PoseStack stack, final MultiBufferSource source, final int packedLight) {
        for (int i = 0; i < entity.getItemStacks().size(); i++) {
            final ItemStack itemStack = entity.getStackInSlot(i);
            if (itemStack.isEmpty()) {
                continue;
            }
            int finalI = i;
            random.setSeed(32L * i + Objects.hashCode(BuiltInRegistries.ITEM.getKey(itemStack.getItem())));
            for (int j = 0; j < itemStack.getCount() / 4; j++) {
                int finalJ = j;
                this.attach(this.model.getBody(), this.model.getBody(), s -> {
                    float f = (float) finalI - 4.5f;
                    s.translate(f / 6.5 + 1f / 16,-2f/16 - (float) finalJ / 32 + (finalI % 4) * 0.001, random.nextFloat(3f / 8) - 3f / 16);
                    s.mulPose(Axis.XP.rotationDegrees(-90.0F));
                    s.scale(0.6f, 0.6f, 0.6f);
                    s.mulPose(Axis.ZP.rotation(random.nextFloat(2 * Mth.PI)));
                    Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, s, source, entity.level(), 0);
                }, stack);
            }
        }
    }
}