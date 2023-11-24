package net.jmb19905.niftycarts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.client.renderer.NiftyCartsModelLayers;
import net.jmb19905.niftycarts.client.renderer.entity.model.HandCartModel;
import net.jmb19905.niftycarts.entity.HandCartEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class HandCartRenderer extends DrawnRenderer<HandCartEntity, HandCartModel> {
    //This texture is not a real file it is assembled during resource loading
    private static final ResourceLocation TEXTURE = new ResourceLocation(NiftyCarts.MOD_ID, "textures/entity/hand_cart.png");
    private static final HumanoidArmorLayer<LivingEntity, HumanoidModel<LivingEntity>, HumanoidModel<LivingEntity>> DUMMY = new HumanoidArmorLayer<>(null, null, null, Minecraft.getInstance().getModelManager());
    private final HumanoidModel<LivingEntity> leggings, armor;

    public HandCartRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HandCartModel(renderManager.bakeLayer(NiftyCartsModelLayers.HAND_CART)));
        this.leggings = new HumanoidModel<>(renderManager.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
        this.armor = new HumanoidModel<>(renderManager.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR));
        this.shadowRadius = 1.0F;
    }

    @Override
    protected void renderContents(HandCartEntity entity, float delta, PoseStack stack, MultiBufferSource source, int packedLight) {
        final NonNullList<ItemStack> cargo = entity.getCargo();
        HandCartRenderer.Contents contents = HandCartRenderer.Contents.SUPPLIES;
        final Iterator<ItemStack> it = cargo.iterator();
        outer:
        while (it.hasNext()) {
            final ItemStack s = it.next();
            if (s.isEmpty()) continue;
            for (final HandCartRenderer.Contents c : HandCartRenderer.Contents.values()) {
                if (c.predicate.test(s)) {
                    contents = c;
                    break outer;
                }
            }
        }
        while (contents != HandCartRenderer.Contents.SUPPLIES && it.hasNext()) {
            final ItemStack s = it.next();
            if (s.isEmpty()) continue;
            if (!contents.predicate.test(s)) contents = HandCartRenderer.Contents.SUPPLIES;
        }
        stack.pushPose();
        this.model.getBody().translateAndRotate(stack);
        contents.renderer.render(this, entity, stack, source, packedLight, cargo);
        final List<Pair<Holder<BannerPattern>, DyeColor>> list = entity.getBannerPattern();
        if (!list.isEmpty()) {
            stack.translate(0.0D, -0.6D, 1.5D);
            this.renderBanner(stack, source, packedLight, list);
        }
        stack.popPose();
    }

    private void renderFlowers(final HandCartEntity entity, final PoseStack stack, final MultiBufferSource source, final int packedLight, final NonNullList<ItemStack> cargo) {
        this.model.getFlowerBasket().render(stack, source.getBuffer(this.model.renderType(this.getTextureLocation(entity))), packedLight, OverlayTexture.NO_OVERLAY);
        final BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        final ModelBlockRenderer renderer = dispatcher.getModelRenderer();
        for (int i = 0; i < cargo.size(); i++) {
            final ItemStack itemStack = cargo.get(i);
            if (!(itemStack.getItem() instanceof BlockItem)) continue;
            final int ix = i % 2, iz = i / 2;
            final BlockState defaultState = ((BlockItem) itemStack.getItem()).getBlock().defaultBlockState();
            final BlockState state = defaultState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) ? defaultState.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER) : defaultState;
            final BakedModel model = dispatcher.getBlockModel(state);
            final int rgb = Minecraft.getInstance().getBlockColors().getColor(state, null, null, 0);
            final float r = (float) (rgb >> 16 & 255) / 255.0F;
            final float g = (float) (rgb >> 8 & 255) / 255.0F;
            final float b = (float) (rgb & 255) / 255.0F;
            stack.pushPose();
            stack.translate(0.0D, -0.7D, -1.0D / 16.0D);
            stack.scale(0.65F, 0.65F, 0.65F);
            stack.translate(ix, 0.5D, iz - 1.0D);
            stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            renderer.renderModel(stack.last(), source.getBuffer(RenderType.cutout()), state, model, r, g, b, packedLight, OverlayTexture.NO_OVERLAY);
            stack.popPose();
        }
    }

    private void renderWheel(final HandCartEntity entity, final PoseStack stack, final MultiBufferSource source, final int packedLight, final NonNullList<ItemStack> cargo) {
        stack.pushPose();
        stack.translate(0.91D, 0.05D, -0.15D);
        final ModelPart wheel = this.model.getWheel();
        wheel.xRot = 0.9F;
        wheel.zRot = (float) Math.PI * 0.3F;
        wheel.render(stack, source.getBuffer(this.model.renderType(this.getTextureLocation(entity))), packedLight, OverlayTexture.NO_OVERLAY);
        stack.popPose();
    }

    private void renderPaintings(final HandCartEntity entity, final PoseStack stack, final MultiBufferSource source, final int packedLight, final NonNullList<ItemStack> cargo) {
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
            stack.mulPose(Axis.ZP.rotation(rng.nextFloat() * (float) Math.PI * 0.2f));
            CargoRenderUtil.renderPainting(t, stack, buf, packedLight);
            stack.popPose();
        }
        stack.popPose();
    }

    private void renderSupplies(final HandCartEntity entity, final PoseStack stack, final MultiBufferSource source, final int packedLight, final NonNullList<ItemStack> cargo) {
        final ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        final Random rng = new Random();
        for (int i = 0; i < cargo.size(); i++) {
            final ItemStack itemStack = cargo.get(i);
            if (itemStack.isEmpty()) continue;
            final int ix = i % 2, iz = i / 2;
            if (i < cargo.size() - 2 && cargo.get(i + 2).is(ItemTags.BEDS)) continue;
            if (i >= 2 && cargo.get(i - 2).is(ItemTags.BEDS)) continue;
            final double x = ((ix * 2 - 1) * 4) / 16.0D;
            final double z = ((iz * 2 - 1) * 5) / 16.0D;
            final BakedModel model = renderer.getModel(itemStack, entity.level(), null, i);
            stack.pushPose();
            if (model.isGui3d() && itemStack.getItem() != Items.TRIDENT) {
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
                if (ArmorItem.class.equals(itemStack.getItem().getClass()) || DyeableArmorItem.class.equals(itemStack.getItem().getClass())) {
                    stack.scale(0.9f, 0.9f, 0.9f);
                    this.renderArmor(stack, source, packedLight, itemStack, ix);
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

    private void renderArmor(final PoseStack stack, final MultiBufferSource source, final int packedLight, final ItemStack itemStack, final int ix) {
        final Item item = itemStack.getItem();
        if (!(item instanceof final ArmorItem armor_)) return;
        final EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(itemStack);
        final HumanoidModel<LivingEntity> m = slot == EquipmentSlot.LEGS ? this.leggings : this.armor;
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
        if (armor_ instanceof DyeableArmorItem) {
            final int rgb = ((DyeableArmorItem) armor_).getColor(itemStack);
            final float r = (float) (rgb >> 16 & 255) / 255.0F;
            final float g = (float) (rgb >> 8 & 255) / 255.0F;
            final float b = (float) (rgb & 255) / 255.0F;
            m.renderToBuffer(stack, armorBuf, packedLight, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
            final VertexConsumer overlayBuf = ItemRenderer.getArmorFoilBuffer(source,
                    RenderType.armorCutoutNoCull(getArmorResource(itemStack, slot, "overlay")),
                    false,
                    itemStack.hasFoil()
            );
            m.renderToBuffer(stack, overlayBuf, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            m.renderToBuffer(stack, armorBuf, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
    public ResourceLocation getArmorResource(ItemStack stack, EquipmentSlot slot, @Nullable String type) {
        ArmorItem item = (ArmorItem)stack.getItem();
        return DUMMY.getArmorLocation(item, DUMMY.usesInnerModel(slot), type);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(HandCartEntity entity) {
        return TEXTURE;
    }

    private enum Contents {
        FLOWERS(s -> s.getItem() instanceof BlockItem && s.is(ItemTags.FLOWERS), HandCartRenderer::renderFlowers),
        PAINTINGS(s -> s.getItem() == Items.PAINTING, HandCartRenderer::renderPaintings),
        WHEEL(s -> s.getItem() == NiftyCarts.WHEEL, HandCartRenderer::renderWheel),
        SUPPLIES(s -> true, HandCartRenderer::renderSupplies);

        private final Predicate<? super ItemStack> predicate;
        private final ContentsRenderer renderer;

        Contents(final Predicate<? super ItemStack> predicate, final HandCartRenderer.ContentsRenderer renderer) {
            this.predicate = predicate;
            this.renderer = renderer;
        }
    }

    @FunctionalInterface
    private interface ContentsRenderer {
        void render(final HandCartRenderer renderer, final HandCartEntity entity, final PoseStack stack, final MultiBufferSource source, final int packedLight, final NonNullList<ItemStack> cargo);
    }
}
