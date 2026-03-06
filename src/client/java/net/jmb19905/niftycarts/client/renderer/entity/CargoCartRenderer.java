package net.jmb19905.niftycarts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsConfig;
import net.jmb19905.niftycarts.client.renderer.entity.model.CargoCartModel;
import net.jmb19905.niftycarts.entity.AbstractCargoCart;
import net.jmb19905.niftycarts.util.NiftyItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.armorstand.ArmorStandArmorModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Rotations;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;

import static net.jmb19905.niftycarts.client.renderer.entity.CargoRenderUtil.BACK_SPRITE_LOCATION;

public abstract class CargoCartRenderer<T extends AbstractCargoCart, M extends CargoCartModel<CargoCartRenderState>> extends DrawnRenderer<T, CargoCartRenderState, M> {

    private final ArmorModelSet<@NotNull ArmorStandArmorModel> armorSet;
    private final EntityRendererProvider.Context ctx;
    private final EquipmentLayerRenderer equipmentRenderer;
    private final ItemModelResolver itemModelResolver;

    protected CargoCartRenderer(EntityRendererProvider.Context ctx, M model) {
        super(ctx, model);
        this.armorSet = ArmorModelSet.bake(ModelLayers.ARMOR_STAND_ARMOR, ctx.getModelSet(), ArmorStandArmorModel::new);
        this.ctx = ctx;
        this.shadowRadius = 1.0F;
        this.equipmentRenderer = ctx.getEquipmentRenderer();
        this.itemModelResolver = ctx.getItemModelResolver();
    }

    @Override
    public @NotNull CargoCartRenderState createRenderState() {
        return new CargoCartRenderState();
    }

    @Override
    public void extractRenderState(T entity, CargoCartRenderState state, float delta) {
        super.extractRenderState(entity, state, delta);
        state.cargo = NonNullList.create();
        state.cargoStates = NonNullList.create();
        for (int i = 0; i < entity.getCargo().size(); i++) {
            state.cargo.add(i, entity.getCargo().get(i));
            ItemStackRenderState cargoState = new ItemStackRenderState();
            this.itemModelResolver.updateForNonLiving(cargoState, entity.getCargo().get(i), ItemDisplayContext.NONE, entity);
            state.cargoStates.add(i, cargoState);
        }
        state.rngSeed = entity.getUUID().getMostSignificantBits() ^ entity.getUUID().getLeastSignificantBits();
        state.level = entity.level();
        state.extraWheel = false;
        state.flowerBasket = false;
        state.armorRenderState = new ArmorStandRenderState();
        state.armorRenderState.isCrouching = false;
        state.armorRenderState.isUsingItem = false;
        state.armorRenderState.isVisuallySwimming = false;
        state.armorRenderState2 = new ArmorStandRenderState();
        state.armorRenderState2.isCrouching = false;
        state.armorRenderState2.isUsingItem = false;
        state.armorRenderState2.isVisuallySwimming = false;
    }

    @Override
    protected void submitContents(CargoCartRenderState state, final PoseStack stack, final SubmitNodeCollector collector) {
        Contents contents = Contents.SUPPLIES;
        final Iterator<ItemStack> it = state.cargo.iterator();
        outer: while (it.hasNext()) {
            final ItemStack s = it.next();
            if (s.isEmpty()) continue;
            for (final Contents c : Contents.values()) {
                if (c.predicate.test(s)) {
                    contents = c;
                    break outer;
                }
            }
        }
        while (contents != Contents.SUPPLIES && it.hasNext()) {
            final ItemStack s = it.next();
            if (s.isEmpty()) continue;
            if (!contents.predicate.test(s)) {
                contents = Contents.SUPPLIES;
                if (!contents.predicate.test(s)) {
                    contents = Contents.NONE;
                }
            }
        }
        stack.pushPose();
        this.model.getBody().translateAndRotate(stack);
        switch (contents) {
            case FLOWERS -> submitFlowers(state, stack, collector, state.cargo);
            case PAINTINGS -> submitPaintings(state, stack, collector, state.cargo);
            case WHEEL -> state.extraWheel = true;
            case SUPPLIES -> submitSupplies(state, stack, collector, state.cargo);
        }
        if (state.bannerColor != null) {
            stack.translate(0.0D, -0.6D, 1.5D);
            this.submitBanner(state, stack, collector);
        }
        stack.popPose();
    }

    void submitFlowers(
            CargoCartRenderState state,
            final PoseStack stack,
            final SubmitNodeCollector collector,
            final NonNullList<@NotNull ItemStack> cargo) {
        state.flowerBasket = true;
        final BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        for (int i = 0; i < cargo.size(); i++) {
            final ItemStack itemStack = cargo.get(i);
            if (!(itemStack.getItem() instanceof BlockItem)) continue;
            final int ix = i % 2, iz = i / 2;
            final BlockState defaultState = ((BlockItem) itemStack.getItem()).getBlock().defaultBlockState();
            final BlockState blockState = defaultState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) ? defaultState.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER) : defaultState;
            final BlockStateModel model = dispatcher.getBlockModel(blockState);
            final int rgb = Minecraft.getInstance().getBlockColors().getColor(blockState, null, null, 0);
            final float r = (float) (rgb >> 16 & 255) / 255.0F;
            final float g = (float) (rgb >> 8 & 255) / 255.0F;
            final float b = (float) (rgb & 255) / 255.0F;
            stack.pushPose();
            stack.translate(0.0D, -0.7D, getFlowerOffsetZ());
            stack.scale(0.65F, 0.65F, 0.65F);
            stack.translate(ix, 0.5D, iz - 1.0D);
            stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            collector.submitCustomGeometry(stack, RenderTypes.cutoutMovingBlock(),
                    (pose, vertexConsumer) -> ModelBlockRenderer.renderModel(pose, vertexConsumer, model, r, g, b, state.lightCoords, OverlayTexture.NO_OVERLAY));
            stack.popPose();
        }
    }

    private void submitPaintings(
            CargoCartRenderState state,
            final PoseStack stack,
            final SubmitNodeCollector collector,
            final NonNullList<@NotNull ItemStack> cargo) {
        final Random rng = new Random(state.rngSeed);
        int count = 0;
        for (final ItemStack itemStack : cargo) {
            if (itemStack.isEmpty()) continue;
            count++;
        }
        stack.pushPose();
        stack.translate(0.0D, -2.5D / 16.0D, 0.0D);
        stack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        var registryAccess = state.level.registryAccess();
        ObjectList<PaintingVariant> variants = new ObjectArrayList<>();
        registryAccess.lookupOrThrow(Registries.PAINTING_VARIANT).getTagOrEmpty(PaintingVariantTags.PLACEABLE).forEach(variantHolder -> {
            if (variantHolder.value().area() == 1) variants.add(variantHolder.value());
        });
        ObjectLists.shuffle(variants, rng);
        for (int i = 0, n = 0; i < cargo.size(); i++) {
            final ItemStack itemStack = cargo.get(i);
            if (itemStack.isEmpty()) continue;

            Holder<@NotNull PaintingVariant> holder = itemStack.get(DataComponents.PAINTING_VARIANT);
            PaintingVariant paintingVariant = null;
            if (holder != null) paintingVariant = holder.value();
            if (paintingVariant == null || paintingVariant.area() > 1) {
                if (variants.isEmpty()) continue;
                paintingVariant = variants.get(i % variants.size());
            }
            stack.pushPose();
            stack.translate(getPaintingOffset(i, n, count));
            n++;
            stack.mulPose(Axis.ZP.rotation(rng.nextFloat() * (float) Math.PI * getPaintingAngleFactor()));
            var atlas = ctx.getAtlas(AtlasIds.PAINTINGS);
            final TextureAtlasSprite back = atlas.getSprite(BACK_SPRITE_LOCATION);
            CargoRenderUtil.renderPainting(ctx, paintingVariant, stack, RenderTypes.entitySolidZOffsetForward(back.atlasLocation()), collector, state.lightCoords);
            stack.popPose();
        }
        stack.popPose();
    }

    protected boolean shouldRender(ItemStack stack) {
        var blacklist = NiftyCartsConfig.getClient().renderBlacklist;
        for (String item : blacklist.get()) {
            if (item.startsWith("#")) {
                var tag = TagKey.create(Registries.ITEM, Identifier.parse(item.substring(1)));
                if (stack.is(tag)) {
                    return false;
                }
            } else if (item.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString())) {
                return false;
            }
        }
        return true;
    }

    private void submitSupplies(CargoCartRenderState state, final PoseStack stack, final SubmitNodeCollector collector, final NonNullList<@NotNull ItemStack> cargo) {
        final Random rng = new Random();
        for (int i = 0; i < cargo.size(); i++) {
            final ItemStack itemStack = cargo.get(i);
            if (itemStack.isEmpty()) continue;
            final int ix = i % 2, iz = i / 2;
            if (i < cargo.size() - 2 && cargo.get(i + 2).is(ItemTags.BEDS)) continue;
            if (i >= 2 && cargo.get(i - 2).is(ItemTags.BEDS)) continue;
            final double x = getSuppliesOffsetX(ix);
            final double z = getSuppliesOffsetZ(iz);
            stack.pushPose();
            ItemStackRenderState cargoState = state.cargoStates.get(i);
            if (shouldRender(itemStack)) {
                if (cargoState.usesBlockLight() && itemStack.getItem() != Items.TRIDENT && NiftyCartsConfig.getClient().renderSupplyGear.get()) {
                    stack.translate(x, -0.46D, z);
                    stack.scale(getBlockSize(), getBlockSize(), getBlockSize());
                    stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
                    if (iz < 1 && itemStack.is(ItemTags.BEDS)) {
                        stack.translate(0.0D, 0.0D, 1.0D);
                    }
                    cargoState.submit(stack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
                } else {
                    rng.setSeed(32L * i + Objects.hashCode(BuiltInRegistries.ITEM.getKey(itemStack.getItem())));
                    stack.translate(x, -0.15D + ((ix + iz) % 2 == 0 ? 0.0D : 1.0e-4D), z);
                    if (NiftyItemUtil.isHumanoidArmor(itemStack) && NiftyCartsConfig.getClient().renderSupplyGear.get()) {
                        stack.scale(getArmorSize(), getArmorSize(), getArmorSize());
                        this.renderArmor(state, stack, collector, itemStack, ix);
                    } else {
                        if (itemStack.getItem() == Items.SHIELD) {
                            //stack.translate(x, -0.46D, z);
                            stack.scale(getBlockSize(), getBlockSize(), getBlockSize());
                            stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
                            stack.scale(1.2F, 1.2F, 1.2F);
                            stack.mulPose(Axis.YP.rotationDegrees(ix == 0 ? -90.0F : 90.0F));
                            stack.translate(0.5D, getShieldOffsetY(), -0.05D);
                            stack.mulPose(Axis.XP.rotationDegrees(-22.5F));
                        } else {
                            stack.scale(getItemSize(), getItemSize(), getItemSize());
                            stack.mulPose(Axis.YP.rotation(rng.nextFloat() * (float) Math.PI));
                            stack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                        }
                        final int copies = Math.min(itemStack.getCount(), (itemStack.getCount() - 1) / 16 + 2);
                        cargoState.submit(stack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
                        for (int n = 1; n < copies; n++) {
                            stack.pushPose();
                            stack.mulPose(Axis.ZP.rotation(rng.nextFloat() * (float) Math.PI));
                            stack.translate((rng.nextFloat() * 2.0F - 1.0F) * 0.05F, (rng.nextFloat() * 2.0F - 1.0F) * 0.05F, -0.1D * n);
                            cargoState.submit(stack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
                            stack.popPose();
                        }
                    }
                }
            }
            stack.popPose();
        }
    }

    private void renderArmor(CargoCartRenderState state, final PoseStack stack, final SubmitNodeCollector collector, final ItemStack itemStack, final int ix) {
        Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
        if (equippable == null) return;
        EquipmentSlot slot = equippable.slot();
        final ArmorStandArmorModel model = this.armorSet.get(slot);
        stack.mulPose(Axis.YP.rotation(ix == 0 ? (float) Math.PI * 0.5F : (float) -Math.PI * 0.5F));
        ArmorStandRenderState armorRenderState = null;
        switch (slot) {
            case HEAD -> {
                stack.translate(0.0D, 0.105D, 0.0D);
                state.armorRenderState.headPose = new Rotations(0.25f * 180f / Mth.PI, 0, 0);
                armorRenderState = state.armorRenderState;
            }
            case CHEST -> {
                stack.translate(0.0D, -0.3D, -0.27D);
                stack.mulPose(Axis.XP.rotation(0.9f));
                state.armorRenderState.leftArmPose = new Rotations(-0.75f * 180f / Mth.PI, 0, 0);
                state.armorRenderState.rightArmPose = new Rotations(-0.75f * 180f / Mth.PI, 0, 0);
                armorRenderState = state.armorRenderState;
            }
            case LEGS -> {
                stack.translate(0.0D, -0.7D, -0.226D);
                state.armorRenderState.bodyPose = new Rotations(0, 0,0);
                state.armorRenderState.rightLegPose = new Rotations(1.2f * 180f / Mth.PI, -0.3f * 180f / Mth.PI, 0);
                state.armorRenderState.leftLegPose = new Rotations(1.2f * 180f / Mth.PI, 0.3f * 180f / Mth.PI, 0);
                armorRenderState = state.armorRenderState;
            }
            case FEET -> {
                stack.translate(0.0D, -1.096D, -0.19D);
                state.armorRenderState2.rightLegPose = new Rotations(0, -0.1f * 180f / Mth.PI, 0);
                state.armorRenderState2.leftLegPose = new Rotations(0, 0, 0);
                armorRenderState = state.armorRenderState2;
            }
        }
        assert armorRenderState != null;
        model.setupAnim(armorRenderState);
        stack.scale(0.75F, 0.75F, 0.75F);

        boolean usesInnerModel = slot == EquipmentSlot.LEGS;
        EquipmentClientInfo.LayerType layerType = usesInnerModel ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS
                : EquipmentClientInfo.LayerType.HUMANOID;
        equipmentRenderer.renderLayers(layerType, equippable.assetId().orElseThrow(), model, armorRenderState,
                itemStack, stack, collector, state.lightCoords, state.outlineColor);
    }

    protected abstract double getFlowerOffsetZ();
    protected abstract Vec3 getWheelOffset();
    protected abstract Vec3 getPaintingOffset(int i, int n, int count);
    protected abstract float getPaintingAngleFactor();
    protected abstract double getSuppliesOffsetX(int x);
    protected abstract double getSuppliesOffsetZ(int z);
    protected abstract float getBlockSize();
    protected abstract float getArmorSize();
    protected abstract float getItemSize();
    protected abstract double getShieldOffsetY();

    private enum Contents {
        FLOWERS(s -> s.getItem() instanceof BlockItem && s.is(ItemTags.SMALL_FLOWERS) && NiftyCartsConfig.getClient().renderSupplyFlowers.get()),
        PAINTINGS(s -> s.getItem() == Items.PAINTING && NiftyCartsConfig.getClient().renderSupplyPaintings.get()),
        WHEEL(s -> s.getItem() == NiftyCarts.WHEEL && NiftyCartsConfig.getClient().renderSupplyWheel.get() && NiftyCartsConfig.getClient().renderSupplies.get()),
        SUPPLIES(s -> NiftyCartsConfig.getClient().renderSupplies.get()),
        NONE(s -> true);

        private final Predicate<? super ItemStack> predicate;

        Contents(final Predicate<? super ItemStack> predicate) {
            this.predicate = predicate;
        }
    }
}