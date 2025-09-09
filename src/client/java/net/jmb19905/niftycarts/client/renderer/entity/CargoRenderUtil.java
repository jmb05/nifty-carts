package net.jmb19905.niftycarts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.jmb19905.niftycarts.client.renderer.entity.model.CartModel;
import net.jmb19905.niftycarts.client.renderer.entity.model.IFlowerCargoModel;
import net.jmb19905.niftycarts.entity.AbstractCargoCart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.*;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Iterator;
import java.util.List;

public class CargoRenderUtil {

    public static void renderContents(AbstractCargoCart entity, CartModel<?> model, DrawnRenderer<?, ?> renderer, float delta, PoseStack stack, MultiBufferSource source, int packedLight) {
        final NonNullList<ItemStack> cargo = entity.getCargo();
        Contents contents = Contents.SUPPLIES;
        final Iterator<ItemStack> it = cargo.iterator();
        outer:
        while (it.hasNext()) {
            final ItemStack s = it.next();
            if (s.isEmpty()) continue;
            for (final Contents c : Contents.values()) {
                if (c.getPredicate().test(s)) {
                    contents = c;
                    break outer;
                }
            }
        }
        while (contents != Contents.SUPPLIES && it.hasNext()) {
            final ItemStack s = it.next();
            if (s.isEmpty()) continue;
            if (!contents.getPredicate().test(s)) {
                contents = Contents.SUPPLIES;
                if (!contents.getPredicate().test(s)) {
                    contents = Contents.NONE;
                }
            }
        }
        stack.pushPose();
        model.getBody().translateAndRotate(stack);
        if (contents.getRenderer() != null && renderer instanceof ICargoCartRenderer cargoCartRenderer) {
            contents.getRenderer().render(cargoCartRenderer, entity, stack, source, packedLight, cargo);
        }
        final List<Pair<Holder<BannerPattern>, DyeColor>> list = entity.getBannerPattern();
        if (!list.isEmpty()) {
            stack.translate(0.0D, -0.6D, 1.5D);
            renderer.renderBanner(entity, delta, stack, source, packedLight, list);
        }
        stack.popPose();
    }

    public static void renderFlowers(IFlowerCargoModel model, ResourceLocation textureLocation, double offset, final PoseStack stack, final MultiBufferSource source, final int packedLight, final NonNullList<ItemStack> cargo) {
        model.getFlowerBasket().render(stack, source.getBuffer(RenderType.entityCutoutNoCull(textureLocation)), packedLight, OverlayTexture.NO_OVERLAY);
        final BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        final ModelBlockRenderer renderer = dispatcher.getModelRenderer();
        for (int i = 0; i < cargo.size(); i++) {
            final ItemStack itemStack = cargo.get(i);
            if (!(itemStack.getItem() instanceof BlockItem)) continue;
            final int ix = i % 2, iz = i / 2;
            final BlockState defaultState = ((BlockItem) itemStack.getItem()).getBlock().defaultBlockState();
            final BlockState state = defaultState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) ? defaultState.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER) : defaultState;
            final BakedModel bakedModel = dispatcher.getBlockModel(state);
            final int rgb = Minecraft.getInstance().getBlockColors().getColor(state, null, null, 0);
            final float r = (float) (rgb >> 16 & 255) / 255.0F;
            final float g = (float) (rgb >> 8 & 255) / 255.0F;
            final float b = (float) (rgb & 255) / 255.0F;
            stack.pushPose();
            stack.translate(0.0D, -0.7D, -offset / 16.0D);
            stack.scale(0.65F, 0.65F, 0.65F);
            stack.translate(ix, 0.5D, iz - 1.0D);
            stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            renderer.renderModel(stack.last(), source.getBuffer(RenderType.cutout()), state, bakedModel, r, g, b, packedLight, OverlayTexture.NO_OVERLAY);
            stack.popPose();
        }
    }

    public static void renderWheel(CartModel<?> model, ResourceLocation textureLocation, double xOffset, double yOffset, double zOffset, final PoseStack stack, final MultiBufferSource source, final int packedLight) {
        stack.pushPose();
        stack.translate(xOffset, yOffset, zOffset);
        final ModelPart wheel = model.getWheel();
        wheel.xRot = 0.9F;
        wheel.zRot = (float) Math.PI * 0.3F;
        wheel.render(stack, source.getBuffer(model.renderType(textureLocation)), packedLight, OverlayTexture.NO_OVERLAY);
        stack.popPose();
    }

    @SuppressWarnings("resource")
    public static void renderArmor(Entity entity, HumanoidModel<LivingEntity> leggings, HumanoidModel<LivingEntity> armor, TextureAtlas armorTrimAtlas, final PoseStack stack, final MultiBufferSource source, final int packedLight, final ItemStack itemStack, final int ix) {
        final Item item = itemStack.getItem();
        if (!(item instanceof final ArmorItem armorItem)) return;
        final EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(itemStack);
        final HumanoidModel<LivingEntity> m = slot == EquipmentSlot.LEGS ? leggings : armor;
        stack.mulPose(Axis.YP.rotation(ix == 0 ? (float) Math.PI * 0.5F : (float) -Math.PI * 0.5F));
        m.setAllVisible(false);
        m.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        m.rightArmPose = HumanoidModel.ArmPose.EMPTY;
        m.crouching = false;
        m.swimAmount = 0.0F;
        m.young = false;
        switch (slot) {
            case HEAD -> {
                stack.translate(0.0D, 0.1D, 0.0D);
                m.head.xRot = 0.2F;
                m.hat.copyFrom(m.head);
                m.head.visible = true;
                m.hat.visible = true;
            }
            case CHEST -> {
                stack.translate(0.0D, -0.4D, -0.15D);
                m.leftArm.xRot = -0.15F;
                m.rightArm.xRot = -0.15F;
                m.body.xRot = 0.9F;
                m.body.visible = true;
                m.rightArm.visible = true;
                m.leftArm.visible = true;
            }
            case LEGS -> {
                stack.translate(0.0D, -0.7D, -0.15D);
                m.body.xRot = 0.0F;
                m.rightLeg.xRot = 1.2F;
                m.leftLeg.xRot = 1.2F;
                m.rightLeg.yRot = -0.3F;
                m.leftLeg.yRot = 0.3F;
                m.body.visible = true;
                m.rightLeg.visible = true;
                m.leftLeg.visible = true;
            }
            case FEET -> {
                stack.translate(0.0D, -1.15D, -0.1D);
                m.rightLeg.xRot = 0.0F;
                m.leftLeg.xRot = 0.0F;
                m.rightLeg.yRot = -0.1F;
                m.leftLeg.yRot = 0.0F;
                m.rightLeg.visible = true;
                m.leftLeg.visible = true;
            }
        }
        stack.scale(0.75F, 0.75F, 0.75F);
        final VertexConsumer armorBuf = ItemRenderer.getArmorFoilBuffer(source,
                RenderType.armorCutoutNoCull(getArmorResource(itemStack, slot, null)),
                false,
                itemStack.hasFoil()
        );
        if (armorItem instanceof DyeableArmorItem) {
            final int rgb = ((DyeableArmorItem) armorItem).getColor(itemStack);
            final float r = (float) (rgb >> 16 & 255) / 255.0F;
            final float g = (float) (rgb >> 8 & 255) / 255.0F;
            final float b = (float) (rgb & 255) / 255.0F;
            renderArmorModelPart(stack, source, packedLight, itemStack, m, slot, r, g, b, null);
            renderArmorModelPart(stack, source, packedLight, itemStack, m, slot, 1.0F, 1.0F, 1.0F, "overlay");
        } else {
            m.renderToBuffer(stack, armorBuf, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        ArmorTrim.getTrim(entity.level().registryAccess(), itemStack).ifPresent(armorTrim -> {
            TextureAtlasSprite textureAtlasSprite = armorTrimAtlas.getSprite(slot == EquipmentSlot.LEGS ? armorTrim.innerTexture(armorItem.getMaterial()) : armorTrim.outerTexture(armorItem.getMaterial()));
            VertexConsumer vertexConsumer = textureAtlasSprite.wrap(source.getBuffer(Sheets.armorTrimsSheet()));
            m.renderToBuffer(stack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        });
        if (itemStack.hasFoil()) {
            m.renderToBuffer(stack, source.getBuffer(RenderType.armorEntityGlint()), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private static void renderArmorModelPart(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, ItemStack armorItem, HumanoidModel<LivingEntity> humanoidModel, EquipmentSlot slot, float f, float g, float h, @Nullable String string) {
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.armorCutoutNoCull(getArmorResource(armorItem, slot, string)));
        humanoidModel.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, f, g, h, 1.0F);
    }

    public static ResourceLocation getArmorResource(ItemStack stack, EquipmentSlot slot, @Nullable String type) {
        ArmorItem item = (ArmorItem)stack.getItem();
        return getArmorLocation(item, slot == EquipmentSlot.LEGS, type);
    }

    private static ResourceLocation getArmorLocation(ArmorItem armorItem, boolean bl, @Nullable String string) {
        String var10000 = armorItem.getMaterial().getName();
        String string2 = "textures/models/armor/" + var10000 + "_layer_" + (bl ? 2 : 1) + (string == null ? "" : "_" + string) + ".png";
        return new ResourceLocation(string2);
    }

    public static void renderPainting(final PaintingVariant painting, final PoseStack stack, final VertexConsumer buf, final int packedLight) {
        final PaintingTextureManager uploader = Minecraft.getInstance().getPaintingTextures();
        final int width = painting.getWidth();
        final int height = painting.getHeight();
        final TextureAtlasSprite art = uploader.get(painting);
        final TextureAtlasSprite back = uploader.getBackSprite();
        final Matrix4f model = stack.last().pose();
        final Matrix3f normal = stack.last().normal();
        final int blockWidth = width / 16;
        final int blockHeight = height / 16;
        final float offsetX = -blockWidth / 2.0F;
        final float offsetY = -blockHeight / 2.0F;
        final float depth = 0.5F / 16.0F;
        final float bu0 = back.getU0();
        final float bu1 = back.getU1();
        final float bv0 = back.getV0();
        final float bv1 = back.getV1();
        final float bup = back.getU(1.0D);
        final float bvp = back.getV(1.0D);
        final double uvX = 16.0D / blockWidth;
        final double uvY = 16.0D / blockHeight;
        for (int x = 0; x < blockWidth; ++x) {
            for (int y = 0; y < blockHeight; ++y) {
                final float x1 = offsetX + (x + 1);
                final float x0 = offsetX + x;
                final float y1 = offsetY + (y + 1);
                final float y0 = offsetY + y;
                final float u0 = art.getU(uvX * (blockWidth - x));
                final float u1 = art.getU(uvX * (blockWidth - x - 1));
                final float v0 = art.getV(uvY * (blockHeight - y));
                final float v1 = art.getV(uvY * (blockHeight - y - 1));
                vert(model, normal, buf, x1, y0, u1, v0, -depth, 0, 0, -1, packedLight);
                vert(model, normal, buf, x0, y0, u0, v0, -depth, 0, 0, -1, packedLight);
                vert(model, normal, buf, x0, y1, u0, v1, -depth, 0, 0, -1, packedLight);
                vert(model, normal, buf, x1, y1, u1, v1, -depth, 0, 0, -1, packedLight);
                vert(model, normal, buf, x1, y1, bu0, bv0, depth, 0, 0, 1, packedLight);
                vert(model, normal, buf, x0, y1, bu1, bv0, depth, 0, 0, 1, packedLight);
                vert(model, normal, buf, x0, y0, bu1, bv1, depth, 0, 0, 1, packedLight);
                vert(model, normal, buf, x1, y0, bu0, bv1, depth, 0, 0, 1, packedLight);
                vert(model, normal, buf, x1, y1, bu0, bv0, -depth, 0, 1, 0, packedLight);
                vert(model, normal, buf, x0, y1, bu1, bv0, -depth, 0, 1, 0, packedLight);
                vert(model, normal, buf, x0, y1, bu1, bvp, depth, 0, 1, 0, packedLight);
                vert(model, normal, buf, x1, y1, bu0, bvp, depth, 0, 1, 0, packedLight);
                vert(model, normal, buf, x1, y0, bu0, bv0, depth, 0, -1, 0, packedLight);
                vert(model, normal, buf, x0, y0, bu1, bv0, depth, 0, -1, 0, packedLight);
                vert(model, normal, buf, x0, y0, bu1, bvp, -depth, 0, -1, 0, packedLight);
                vert(model, normal, buf, x1, y0, bu0, bvp, -depth, 0, -1, 0, packedLight);
                vert(model, normal, buf, x1, y1, bup, bv0, depth, -1, 0, 0, packedLight);
                vert(model, normal, buf, x1, y0, bup, bv1, depth, -1, 0, 0, packedLight);
                vert(model, normal, buf, x1, y0, bu0, bv1, -depth, -1, 0, 0, packedLight);
                vert(model, normal, buf, x1, y1, bu0, bv0, -depth, -1, 0, 0, packedLight);
                vert(model, normal, buf, x0, y1, bup, bv0, -depth, 1, 0, 0, packedLight);
                vert(model, normal, buf, x0, y0, bup, bv1, -depth, 1, 0, 0, packedLight);
                vert(model, normal, buf, x0, y0, bu0, bv1, depth, 1, 0, 0, packedLight);
                vert(model, normal, buf, x0, y1, bu0, bv0, depth, 1, 0, 0, packedLight);
            }
        }
    }

    private static void vert(final Matrix4f stack, final Matrix3f normal, final VertexConsumer buf, final float x, final float y, final float u, final float v, final float z, final int nx, final int ny, final int nz, final int packedLight) {
        buf.vertex(stack, x, y, z).color(0xFF, 0xFF, 0xFF, 0xFF).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, nx, ny, nz).endVertex();
    }

}