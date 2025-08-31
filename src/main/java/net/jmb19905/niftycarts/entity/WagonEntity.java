package net.jmb19905.niftycarts.entity;

import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WagonEntity extends AbstractDrawnInventoryEntity {

    private static final EntityDataAccessor<Integer> UNFURL = SynchedEntityData.defineId(WagonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ROOF_COLOR = SynchedEntityData.defineId(WagonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<ItemStack> EQUIPPED_CARPET = SynchedEntityData.defineId(WagonEntity.class, EntityDataSerializers.ITEM_STACK);

    private static final int CONTAINER_SIZE = 54;

    public WagonEntity(EntityType<? extends Entity> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn, CONTAINER_SIZE);
    }

    @Override
    protected double getSpacing() {
        return 2.5;
    }

    @Override
    public boolean shouldPitch() {
        return false;
    }

    public int getUnfurled() {
        return this.entityData.get(UNFURL);
    }

    public boolean hasRoof() {
        return this.entityData.get(ROOF_COLOR) != -1;
    }

    @Nullable
    public DyeColor getRoofColor() {
        if (!hasRoof()) return null;
        return DyeColor.byId(this.entityData.get(ROOF_COLOR));
    }

    public ResourceLocation getRoofTexture() {
        DyeColor color = getRoofColor();
        String name = "white";
        if (color != null) {
            name = color.getName();
        }
        return ResourceLocation.fromNamespaceAndPath(NiftyCarts.MOD_ID, "textures/entity/wagon_roof_" + name + ".png");
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(UNFURL, 0);
        builder.define(ROOF_COLOR, -1);
        builder.define(EQUIPPED_CARPET, ItemStack.EMPTY);
    }

    @Override
    public Item getCartItem() {
        return NiftyCarts.WAGON.get(getWoodType());
    }

    @Override
    public float getDisconnectedAngle() {
        return 20;
    }

    @Override
    public @NotNull InteractionResult interactAt(Player player, Vec3 vec3, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (vec3.y > 2.2 && !player.isSecondaryUseActive()) {
            if (itemStack.is(ItemTags.WOOL_CARPETS)) {
                if (!(itemStack.getItem() instanceof BlockItem item)) return InteractionResult.PASS;
                if (!(item.getBlock() instanceof  WoolCarpetBlock block)) return InteractionResult.PASS;
                if (itemStack.getCount() >= 5 || player.hasInfiniteMaterials()) {
                    this.entityData.set(ROOF_COLOR, block.getColor().getId());
                    playSound(SoundEvents.WOOL_PLACE);
                    if (!player.hasInfiniteMaterials()) {
                        itemStack.shrink(5);
                        if (!player.getInventory().add(this.entityData.get(EQUIPPED_CARPET))) {
                            player.drop(this.entityData.get(EQUIPPED_CARPET), false);
                        }
                    }
                    this.entityData.set(EQUIPPED_CARPET, new ItemStack(item, 5));
                }
            } else {
                this.entityData.set(UNFURL, (getUnfurled() + 1) % 3);
                playSound(SoundEvents.WOOL_STEP);
            }
            return InteractionResult.CONSUME;
        } else {
            return super.interact(player, interactionHand);
        }
    }

    public void handleRotation(final Vec3 target) {
        this.setYRot(getYaw(target));
    }

    @Override
    protected NiftyCartsConfig.CartConfig getConfig() {
        return NiftyCartsConfig.get().wagon;
    }

    @Override
    protected InteractionResult onInteractNotOpen(Player player, InteractionHand hand) {
        if (this.getPulling() != player) {
            if (!this.canAddPassenger(player)) {
                return InteractionResult.PASS;
            }
            if (!this.level().isClientSide) {
                return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected boolean canAddPassenger(Entity entity) {
        return this.getPassengers().size() < 4;
    }

    public float getPassengersRidingOffsetY(EntityDimensions entityDimensions, float f) {
        return (entityDimensions.height() - 2 - 1f/16f) * f;
    }

    @Override
    protected @NotNull Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float factor) {
        int idx = this.getPassengers().indexOf(entity);
        double f = (idx == 0 || idx == 2) ? 0.1 : -1.2;
        double s = (idx == 0 || idx == 1) ? 0.7 : -0.7;
        final Vec3 forward = this.getLookAngle().scale(f);
        final Vec3 sideways = new Vec3(forward.z, 0, -forward.x).normalize().scale(s);
        return new Vec3(forward.x + sideways.x, getPassengersRidingOffsetY(entityDimensions, factor) + forward.y, forward.z + sideways.z);
    }

    @Override
    public void positionRider(final Entity passenger, MoveFunction moveFunction) {
        super.positionRider(passenger, moveFunction);
        int idx = this.getPassengers().indexOf(passenger);
        int dir = idx == 0 || idx == 3 ? 1 : -1;
        if (this.hasPassenger(passenger)) {
            passenger.setYBodyRot(this.getYRot() + 90 * dir);
            final float f2 = Mth.wrapDegrees(passenger.getYRot() - this.getYRot() - 90 * dir);
            final float clamped = Mth.clamp(f2, -105.0F, 105.0F);
            passenger.yRotO += clamped - f2;
            passenger.setYRot(passenger.getYRot() + (clamped - f2));
            passenger.setYHeadRot(passenger.getYRot());
        }
    }

    @Override
    protected AbstractContainerMenu createMenuLootUnpacked(int i, Inventory inventory, Player player) {
        return ChestMenu.sixRows(i, inventory, this);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Unfurl", this.entityData.get(UNFURL));
        compound.putInt("RoofColor", this.entityData.get(ROOF_COLOR));
        CompoundTag itemTag = new CompoundTag();
        if (!this.entityData.get(EQUIPPED_CARPET).isEmpty()) {
            this.entityData.get(EQUIPPED_CARPET).save(this.registryAccess(), itemTag);
        }
        compound.put("Carpet", itemTag);
    }

    @Override
    protected void saveInventory(CompoundTag tag) {
        this.addChestVehicleSaveData(tag, this.registryAccess());
    }

    @Override
    protected void readInventory(CompoundTag tag) {
        this.readChestVehicleSaveData(tag, this.registryAccess());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(UNFURL, compound.getInt("Unfurl"));
        this.entityData.set(ROOF_COLOR, compound.getInt("RoofColor"));
        CompoundTag itemTag = compound.getCompound("Carpet");
        this.entityData.set(EQUIPPED_CARPET, ItemStack.parse(this.registryAccess(), itemTag).orElse(ItemStack.EMPTY));
    }
}
