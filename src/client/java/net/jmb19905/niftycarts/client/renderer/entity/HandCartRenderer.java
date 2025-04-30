package net.jmb19905.niftycarts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsConfig;
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
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

public class HandCartRenderer extends DrawnRenderer<HandCartEntity, CargoCartRenderState, HandCartModel> {
    private final HumanoidModel<HumanoidRenderState> leggings, armor;
    private final EquipmentLayerRenderer equipmentRenderer;
    private final ItemModelResolver itemModelResolver;

    private static final HumanoidRenderState humanoidRenderState = new HumanoidRenderState();

    static {
        humanoidRenderState.isCrouching = false;
        humanoidRenderState.isUsingItem = false;
        humanoidRenderState.isVisuallySwimming = false;
    }

    public HandCartRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HandCartModel(ctx.bakeLayer(NiftyCartsModelLayers.HAND_CART)));
        this.leggings = new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
        this.armor = new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR));
        this.shadowRadius = 1.0F;
        this.equipmentRenderer = ctx.getEquipmentRenderer();
        itemModelResolver = ctx.getItemModelResolver();
    }

    @Override
    public @NotNull CargoCartRenderState createRenderState() {
        return new CargoCartRenderState();
    }

    @Override
    public void extractRenderState(HandCartEntity entity, CargoCartRenderState state, float delta) {
        super.extractRenderState(entity, state, delta);
        state.cargo = NonNullList.create();
        for (int i = 0; i < entity.getCargo().size(); i++) {
            state.cargo.add(i, entity.getCargo().get(i));
            ItemStackRenderState cargoState = new ItemStackRenderState();
            this.itemModelResolver.updateForNonLiving(cargoState, entity.getCargo().get(i), ItemDisplayContext.FIXED, entity);
            state.cargoStates.add(i, cargoState);
        }
        state.rngSeed = entity.getUUID().getMostSignificantBits() ^ entity.getUUID().getLeastSignificantBits();
        state.level = entity.level();
    }

    @Override
    protected void renderContents(CargoCartRenderState state, PoseStack stack, MultiBufferSource source, int packedLight) {
        HandCartRenderer.Contents contents = HandCartRenderer.Contents.SUPPLIES;
        final Iterator<ItemStack> it = state.cargo.iterator();
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
            if (!contents.predicate.test(s)) {
                contents = HandCartRenderer.Contents.SUPPLIES;
                if (!contents.predicate.test(s)) {
                    contents = HandCartRenderer.Contents.NONE;
                }
            }
        }
        stack.pushPose();
        this.model.getBody().translateAndRotate(stack);
        if (contents.renderer != null) {
            contents.renderer.render(this, state, stack, source, packedLight, state.cargo);
        }
        stack.popPose();
    }

    private void renderFlowers(CargoCartRenderState state, final PoseStack stack, final MultiBufferSource source, final int packedLight, final NonNullList<ItemStack> cargo) {
        this.model.getFlowerBasket().visible = true;
        this.model.getFlowerBasket().render(stack, source.getBuffer(this.model.renderType(this.getTextureLocation(state))), packedLight, OverlayTexture.NO_OVERLAY);
        this.model.getFlowerBasket().visible = false;
        final BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        final ModelBlockRenderer renderer = dispatcher.getModelRenderer();
        for (int i = 0; i < cargo.size(); i++) {
            final ItemStack itemStack = cargo.get(i);
            if (!(itemStack.getItem() instanceof BlockItem)) continue;
            final int ix = i % 2, iz = i / 2;
            final BlockState defaultState = ((BlockItem) itemStack.getItem()).getBlock().defaultBlockState();
            final BlockState blockState = defaultState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) ? defaultState.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER) : defaultState;
            final BakedModel model = dispatcher.getBlockModel(blockState);
            final int rgb = Minecraft.getInstance().getBlockColors().getColor(blockState, null, null, 0);
            final float r = (float) (rgb >> 16 & 255) / 255.0F;
            final float g = (float) (rgb >> 8 & 255) / 255.0F;
            final float b = (float) (rgb & 255) / 255.0F;
            stack.pushPose();
            stack.translate(0.0D, -0.7D, -1.0D / 16.0D);
            stack.scale(0.65F, 0.65F, 0.65F);
            stack.translate(ix, 0.5D, iz - 1.0D);
            stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            renderer.renderModel(stack.last(), source.getBuffer(RenderType.cutout()), blockState, model, r, g, b, packedLight, OverlayTexture.NO_OVERLAY);
            stack.popPose();
        }
    }

    private void renderWheel(CargoCartRenderState state, final PoseStack stack, final MultiBufferSource source, final int packedLight, final NonNullList<ItemStack> cargo) {
        stack.pushPose();
        stack.translate(0.91D, 0.05D, -0.15D);
        final ModelPart wheel = this.model.getWheel();
        wheel.xRot = 0.9F;
        wheel.zRot = (float) Math.PI * 0.3F;
        wheel.render(stack, source.getBuffer(this.model.renderType(this.getTextureLocation(state))), packedLight, OverlayTexture.NO_OVERLAY);
        stack.popPose();
    }

    private void renderPaintings(CargoCartRenderState state, final PoseStack stack, final MultiBufferSource source, final int packedLight, final NonNullList<ItemStack> cargo) {
        final VertexConsumer buf = source.getBuffer(RenderType.entitySolid(Minecraft.getInstance().getPaintingTextures().getBackSprite().atlasLocation()));
        final Random rng = new Random(state.rngSeed);
        stack.pushPose();
        stack.translate(0.0D, -2.5D / 16.0D, 0.0D);
        stack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        var registryAccess = state.level.registryAccess();
        ObjectList<PaintingVariant> variants = new ObjectArrayList<>();
        registryAccess.lookupOrThrow(Registries.PAINTING_VARIANT).getTagOrEmpty(PaintingVariantTags.PLACEABLE).forEach(variantHolder -> {
            if (variantHolder.value().area() == 1) variants.add(variantHolder.value());
        });
        ObjectLists.shuffle(variants, rng);
        for (int i = 0; i < cargo.size(); i++) {
            final ItemStack itemStack = cargo.get(i);
            if (itemStack.isEmpty()) continue;
            CustomData customData = itemStack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
            Optional<PaintingVariant> paintingVariant = Optional.empty();
            if (!customData.isEmpty()) {
                paintingVariant = customData.read(registryAccess.createSerializationContext(NbtOps.INSTANCE), Painting.VARIANT_MAP_CODEC).result().map(Holder::value);
            }
            if (paintingVariant.isEmpty() || paintingVariant.get().area() > 1) {
                if (variants.isEmpty()) continue;
                paintingVariant = Optional.of(variants.get(i % variants.size()));
            }
            stack.pushPose();
            stack.translate(0.0D, 0.03D, -1D / 16.0D * i + 0.0001f);
            stack.mulPose(Axis.ZP.rotation(rng.nextFloat() * (float) Math.PI * 0.2f));
            CargoRenderUtil.renderPainting(paintingVariant.get(), stack, buf, packedLight);
            stack.popPose();
        }
        stack.popPose();
    }

    private void renderSupplies(CargoCartRenderState state, final PoseStack stack, final MultiBufferSource source, final int packedLight, final NonNullList<ItemStack> cargo) {
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
            ItemStackRenderState cargoState = state.cargoStates.get(i);
            stack.pushPose();
            if (cargoState.isGui3d() && itemStack.getItem() != Items.TRIDENT && NiftyCartsConfig.getClient().renderSupplyGear.get()) {
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
                }
                cargoState.render(stack, source, packedLight, OverlayTexture.NO_OVERLAY);
            } else {
                rng.setSeed(32L * i + Objects.hashCode(BuiltInRegistries.ITEM.getKey(itemStack.getItem())));
                stack.translate(x, -0.15D + ((ix + iz) % 2 == 0 ? 0.0D : 1.0e-4D), z);
                if (ArmorItem.class.equals(itemStack.getItem().getClass()) && NiftyCartsConfig.getClient().renderSupplyGear.get()) {
                    stack.scale(0.9f, 0.9f, 0.9f);
                    this.renderArmor(stack, source, packedLight, itemStack, ix);
                } else {
                    stack.scale(0.6F, 0.6F, 0.6F);
                    stack.mulPose(Axis.YP.rotation(rng.nextFloat() * (float) Math.PI));
                    stack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                    final int copies = Math.min(itemStack.getCount(), (itemStack.getCount() - 1) / 16 + 2);
                    cargoState.render(stack, source, packedLight, OverlayTexture.NO_OVERLAY);
                    for (int n = 1; n < copies; n++) {
                        stack.pushPose();
                        stack.mulPose(Axis.ZP.rotation(rng.nextFloat() * (float) Math.PI));
                        stack.translate((rng.nextFloat() * 2.0F - 1.0F) * 0.05F, (rng.nextFloat() * 2.0F - 1.0F) * 0.05F, -0.1D * n);
                        cargoState.render(stack, source, packedLight, OverlayTexture.NO_OVERLAY);
                        stack.popPose();
                    }
                }
            }
            stack.popPose();
        }
    }

    private void renderArmor(final PoseStack stack, final MultiBufferSource source, final int packedLight, final ItemStack itemStack, final int ix) {
        final Item item = itemStack.getItem();
        if (!(item instanceof final ArmorItem armorItem)) return;
        Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
        if (equippable == null) return;
        final EquipmentSlot slot = equippable.slot();
        final HumanoidModel<HumanoidRenderState> m = slot == EquipmentSlot.LEGS ? this.leggings : this.armor;
        stack.mulPose(Axis.YP.rotation(ix == 0 ? (float) Math.PI * 0.5F : (float) -Math.PI * 0.5F));
        m.setAllVisible(false);
        m.setupAnim(humanoidRenderState);
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

        ResourceKey<EquipmentAsset> resourceLocation = equippable.assetId().orElseThrow();
        boolean usesInnerModel = slot == EquipmentSlot.LEGS;
        EquipmentClientInfo.LayerType layerType = usesInnerModel ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS : EquipmentClientInfo.LayerType.HUMANOID;
        equipmentRenderer.renderLayers(layerType, resourceLocation, m, itemStack, stack, source, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(CargoCartRenderState state) {
        return NiftyCarts.resLoc("textures/entity/" + state.woodType.name() + "_hand_cart.png");
    }

    private enum Contents {
        FLOWERS(s -> s.getItem() instanceof BlockItem && s.is(ItemTags.SMALL_FLOWERS) && NiftyCartsConfig.getClient().renderSupplyFlowers.get(), HandCartRenderer::renderFlowers),
        PAINTINGS(s -> s.getItem() == Items.PAINTING && NiftyCartsConfig.getClient().renderSupplyPaintings.get(), HandCartRenderer::renderPaintings),
        WHEEL(s -> s.getItem() == NiftyCarts.WHEEL && NiftyCartsConfig.getClient().renderSupplyWheel.get(), HandCartRenderer::renderWheel),
        SUPPLIES(s -> NiftyCartsConfig.getClient().renderSupplies.get(), HandCartRenderer::renderSupplies),
        NONE(s -> true, null);

        private final Predicate<? super ItemStack> predicate;
        private final ContentsRenderer renderer;

        Contents(final Predicate<? super ItemStack> predicate, final HandCartRenderer.ContentsRenderer renderer) {
            this.predicate = predicate;
            this.renderer = renderer;
        }
    }

    @FunctionalInterface
    private interface ContentsRenderer {
        void render(final HandCartRenderer renderer, CargoCartRenderState state, final PoseStack stack, final MultiBufferSource source, final int packedLight, final NonNullList<ItemStack> cargo);
    }
}
