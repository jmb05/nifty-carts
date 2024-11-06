package net.jmb19905.niftycarts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsConfig;
import net.jmb19905.niftycarts.client.renderer.NiftyCartsModelLayers;
import net.jmb19905.niftycarts.client.renderer.entity.model.HandCartModel;
import net.jmb19905.niftycarts.entity.AbstractCargoCart;
import net.jmb19905.niftycarts.entity.HandCartEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class HandCartRenderer extends DrawnRenderer<HandCartEntity, HandCartModel> implements ICargoCartRenderer {
    private final HumanoidModel<LivingEntity> leggings, armor;
    private final TextureAtlas armorTrimAtlas;

    public HandCartRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HandCartModel(renderManager.bakeLayer(NiftyCartsModelLayers.HAND_CART)));
        this.leggings = new HumanoidModel<>(renderManager.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
        this.armor = new HumanoidModel<>(renderManager.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR));
        this.armorTrimAtlas = renderManager.getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET);
        this.shadowRadius = 1.0F;
    }

    @Override
    protected void renderContents(HandCartEntity entity, float delta, PoseStack stack, MultiBufferSource source, int packedLight) {
        CargoRenderUtil.renderContents(entity, this.model, this, stack, source, packedLight);
    }

    public void renderFlowers(final AbstractCargoCart entity, final PoseStack stack, final MultiBufferSource source, final int packedLight, final NonNullList<ItemStack> cargo) {
        CargoRenderUtil.renderFlowers(this.model, getTextureLocation((HandCartEntity) entity), 1, stack, source, packedLight, cargo);
    }

    public void renderWheel(final AbstractCargoCart entity, final PoseStack stack, final MultiBufferSource source, final int packedLight, final NonNullList<ItemStack> cargo) {
        CargoRenderUtil.renderWheel(this.model, getTextureLocation((HandCartEntity) entity), 0.91D, 0.05D, -0.15D, stack, source, packedLight);
    }

    public void renderPaintings(final AbstractCargoCart entity, final PoseStack stack, final MultiBufferSource source, final int packedLight, final NonNullList<ItemStack> cargo) {
        final VertexConsumer buf = source.getBuffer(RenderType.entitySolid(Minecraft.getInstance().getPaintingTextures().getBackSprite().atlasLocation()));
        final ObjectList<PaintingVariant> types = StreamSupport.stream(BuiltInRegistries.PAINTING_VARIANT.spliterator(), false)
                .filter(t -> t.getWidth() == 16 && t.getHeight() == 16)
                .collect(Collectors.toCollection(ObjectArrayList::new));
        final Random rng = new Random(entity.getUUID().getMostSignificantBits() ^ entity.getUUID().getLeastSignificantBits());
        ObjectLists.shuffle(types, rng);
        stack.pushPose();
        stack.translate(0.0D, -2.5D / 16.0D, 0.0D);
        stack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        for (int i = 0; i < cargo.size(); i++) {
            final ItemStack itemStack = cargo.get(i);
            if (itemStack.isEmpty()) continue;
            final PaintingVariant t = types.get(i % types.size());
            stack.pushPose();
            stack.translate(0.0D, 0.03D, -1D / 16.0D * i + 0.0001f);
            stack.mulPose(Axis.ZP.rotation(rng.nextFloat() * (float) Math.PI * 0.075f));
            CargoRenderUtil.renderPainting(t, stack, buf, packedLight);
            stack.popPose();
        }
        stack.popPose();
    }

    public void renderSupplies(final AbstractCargoCart entity, final PoseStack stack, final MultiBufferSource source, final int packedLight, final NonNullList<ItemStack> cargo) {
        final ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        final Random rng = new Random();
        for (int i = 0; i < cargo.size(); i++) {
            final ItemStack itemStack = cargo.get(i);
            if (itemStack.isEmpty()) continue;
            final int ix = i % 2, iz = i / 2;
            if (i < cargo.size() - 2 && cargo.get(i + 2).is(ItemTags.BEDS)) continue;
            if (i >= 2 && cargo.get(i - 2).is(ItemTags.BEDS)) continue;
            final double x = ((ix * 2 - 1) * 4) / 16.0D;
            final double z = ((iz * 2 - 1) * 5) / 16.0D - (1f/32f);
            final BakedModel model = renderer.getModel(itemStack, entity.level(), null, i);
            stack.pushPose();
            if (model.isGui3d() && itemStack.getItem() != Items.TRIDENT && NiftyCartsConfig.getClient().renderSupplyGear.get()) {
                stack.translate(x, -0.46D, z);
                stack.scale(0.5F, 0.5F, 0.5F);
                stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
                if (itemStack.getItem() == Items.SHIELD) {
                    stack.scale(1.2F, 1.2F, 1.2F);
                    stack.mulPose(Axis.YP.rotationDegrees(ix == 0 ? -90.0F : 90.0F));
                    stack.translate(0.5D, 0.8D, -0.05D);
                    stack.mulPose(Axis.XP.rotationDegrees(-22.5F));
                } else if (iz < 1 && itemStack.is(ItemTags.BEDS)) {
                    stack.translate(0.0D, 0.0D, 1.0D);
                } else if (!model.isCustomRenderer()) {
                    stack.mulPose(Axis.YP.rotationDegrees(180.0F));
                }
                renderer.render(itemStack, ItemDisplayContext.NONE, false, stack, source, packedLight, OverlayTexture.NO_OVERLAY, model);
            } else {
                rng.setSeed(32L * i + Objects.hashCode(BuiltInRegistries.ITEM.getKey(itemStack.getItem())));
                stack.translate(x, -0.15D + ((ix + iz) % 2 == 0 ? 0.0D : 1.0e-4D), z);
                if ((ArmorItem.class.equals(itemStack.getItem().getClass()) || DyeableArmorItem.class.equals(itemStack.getItem().getClass())) && NiftyCartsConfig.getClient().renderSupplyGear.get()) {
                    stack.scale(0.9f, 0.9f, 0.9f);
                    CargoRenderUtil.renderArmor(entity, leggings, armor, armorTrimAtlas, stack, source, packedLight, itemStack, ix);
                } else {
                    stack.scale(0.6F, 0.6F, 0.6F);
                    stack.mulPose(Axis.YP.rotation(rng.nextFloat() * (float) Math.PI));
                    stack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                    final int copies = Math.min(itemStack.getCount(), (itemStack.getCount() - 1) / 16 + 2);
                    renderer.render(itemStack, ItemDisplayContext.FIXED, false, stack, source, packedLight, OverlayTexture.NO_OVERLAY, model);
                    for (int n = 1; n < copies; n++) {
                        stack.pushPose();
                        stack.mulPose(Axis.ZP.rotation(rng.nextFloat() * (float) Math.PI));
                        stack.translate((rng.nextFloat() * 2.0F - 1.0F) * 0.05F, (rng.nextFloat() * 2.0F - 1.0F) * 0.05F, -0.1D * n);
                        renderer.render(itemStack, ItemDisplayContext.FIXED, false, stack, source, packedLight, OverlayTexture.NO_OVERLAY, model);
                        stack.popPose();
                    }
                }
            }
            stack.popPose();
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(HandCartEntity entity) {
        return new ResourceLocation(NiftyCarts.MOD_ID, "textures/entity/" + entity.getWoodType().getId() + "_hand_cart.png");
    }
}
