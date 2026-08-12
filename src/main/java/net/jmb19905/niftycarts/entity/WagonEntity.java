package net.jmb19905.niftycarts.entity;

import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsConfig;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.AgeableWaterCreature;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WagonEntity extends AbstractDrawnInventoryEntity implements Leashable {

    private static final EntityDataAccessor<@NotNull Integer> UNFURL = SynchedEntityData.defineId(WagonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<@NotNull Integer> ROOF_COLOR = SynchedEntityData.defineId(WagonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<@NotNull ItemStack> EQUIPPED_CARPET = SynchedEntityData.defineId(WagonEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<@NotNull Integer> CHEST_COUNT = SynchedEntityData.defineId(WagonEntity.class, EntityDataSerializers.INT);

    @Nullable
    private Leashable.LeashData leashData;

    public WagonEntity(EntityType<? extends @NotNull Entity> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn, 3 * 4 * 9);
    }

    @Override
    public void onDestroyedAndDoDrops(DamageSource source) {
        super.onDestroyedAndDoDrops(source);
        if (level() instanceof ServerLevel serverLevel && serverLevel.getGameRules().get(GameRules.ENTITY_DROPS)) {
            this.spawnAtLocation(serverLevel, new ItemStack(Items.CHEST, getChestCount()));
            this.spawnAtLocation(serverLevel, this.entityData.get(EQUIPPED_CARPET));
        }
    }

    public int getChestCount() {
        return this.entityData.get(CHEST_COUNT);
    }

    public int getMaxChestCount() {
        return 3;
    }

    public int getCurrentRowCount() {
        return getChestCount() * 4;
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

    public Identifier getRoofTexture() {
        DyeColor color = getRoofColor();
        String name = "white";
        if (color != null) {
            name = color.getName();
        }
        return Identifier.fromNamespaceAndPath(NiftyCarts.MOD_ID, "textures/entity/wagon_roof_" + name + ".png");
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(UNFURL, 0);
        builder.define(ROOF_COLOR, -1);
        builder.define(CHEST_COUNT, 0);
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
    public boolean canHaveALeashAttachedTo(@NotNull Entity entity) {
        if (entity instanceof Player) return false;
        return Leashable.super.canHaveALeashAttachedTo(entity);
    }

    @Override
    public @NotNull InteractionResult interactAt(@NotNull Player player, @NotNull Vec3 vec3, @NotNull InteractionHand interactionHand) {
        if (isLocked()) return InteractionResult.FAIL;
        var leashResult = interactLeash(player, interactionHand);
        if (leashResult != InteractionResult.PASS) {
            return leashResult;
        }
        ItemStack itemStack = player.getItemInHand(interactionHand);
        final InteractionResult bannerResult = this.useBanner(player, interactionHand);
        if (bannerResult.consumesAction()) return bannerResult;
        if (vec3.y > 2.2 && !player.isSecondaryUseActive()) {
            return interactCarpet(itemStack, player);
        } else if (itemStack.is(Items.CHEST) && canAddChest()) {
            return interactChest(itemStack, player);
        } else if (player.isSecondaryUseActive() && !getPassengers().isEmpty() && !this.level().isClientSide()) {
            for (final Entity entity : this.getPassengers()) {
                if (!(entity instanceof Player)) {
                    entity.stopRiding();
                }
            }
            return InteractionResult.SUCCESS_SERVER;
        }
        return super.interact(player, interactionHand);
    }

    private InteractionResult interactLeash(@NotNull Player player, @NotNull InteractionHand hand) {
        if (!this.level().isClientSide() && player.isSecondaryUseActive()) {
            if (this.canBeLeashed() && this.isAlive()) {
                List<Leashable> list = Leashable.leashableInArea(this, (leashable) -> leashable.getLeashHolder() == player);
                if (!list.isEmpty()) {
                    boolean bl = false;

                    for (Leashable leashable2 : list) {
                        if (leashable2.canHaveALeashAttachedTo(this)) {
                            leashable2.setLeashedTo(this, true);
                            bl = true;
                        }
                    }

                    if (bl) {
                        this.level().gameEvent(GameEvent.ENTITY_ACTION, this.blockPosition(), GameEvent.Context.of(player));
                        this.playSound(SoundEvents.LEAD_TIED);
                        return InteractionResult.SUCCESS_SERVER.withoutItem();
                    }
                }
            }
        }

        if (this.isAlive()) {
            if (this.getLeashHolder() == player) {
                if (!this.level().isClientSide()) {
                    if (player.hasInfiniteMaterials()) {
                        this.removeLeash();
                    } else {
                        this.dropLeash();
                    }

                    this.gameEvent(GameEvent.ENTITY_INTERACT, player);
                    this.playSound(SoundEvents.LEAD_UNTIED);
                }

                return InteractionResult.SUCCESS.withoutItem();
            }

            ItemStack itemStack2 = player.getItemInHand(hand);
            if (itemStack2.is(Items.LEAD) && !(this.getLeashHolder() instanceof Player)) {
                if (this.level().isClientSide()) {
                    return InteractionResult.CONSUME;
                }

                if (this.canHaveALeashAttachedTo(player)) {
                    if (this.isLeashed()) {
                        this.dropLeash();
                    }

                    this.setLeashedTo(player, true);
                    this.playSound(SoundEvents.LEAD_TIED);
                    itemStack2.shrink(1);
                    return InteractionResult.SUCCESS_SERVER;
                }
            }
        }

        return InteractionResult.PASS;
    }

    private InteractionResult interactCarpet(ItemStack itemStack, Player player) {
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
        } else if (hasRoof()) {
            this.entityData.set(UNFURL, (getUnfurled() + 1) % 3);
            playSound(SoundEvents.WOOL_STEP);
        }
        return InteractionResult.CONSUME;
    }

    private InteractionResult interactChest(ItemStack itemStack, Player player) {
        this.entityData.set(CHEST_COUNT, getChestCount() + 1);
        if (!player.hasInfiniteMaterials()) {
            itemStack.shrink(1);
        }
        return InteractionResult.CONSUME;
    }

    private boolean canAddChest() {
        int chests = getChestCount();
        int passengers = getPassengers().size();
        return switch (chests) {
            case 0 -> true;
            case 1 -> passengers <= 2;
            case 2 -> passengers == 0;
            default -> false;
        };
    }

    public void handleRotation(final Vec3 target) {
        this.setYRot(getYaw(target));
    }

    @Override
    public NiftyCartsConfig.CartConfig getConfig() {
        return NiftyCartsConfig.get().wagon;
    }

    @Override
    protected InteractionResult onInteractNotOpen(Player player, InteractionHand hand) {
        if (this.getPulling() != player) {
            if (!this.canAddPassenger(player)) {
                return InteractionResult.PASS;
            }
            if (!this.level().isClientSide()) {
                return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected boolean canAddPassenger(@NotNull Entity entity) {
        return switch (getChestCount()) {
            case 0, 1 -> getPassengers().size() < 4;
            case 2 -> getPassengers().size() < 2;
            default -> false;
        };
    }

    public float getPassengersRidingOffsetY(EntityDimensions entityDimensions, float f) {
        return (entityDimensions.height() - 2 - 2f/16f) * f;
    }

    @Override
    protected @NotNull Vec3 getPassengerAttachmentPoint(@NotNull Entity entity, @NotNull EntityDimensions entityDimensions, float factor) {
        int idx = this.getPassengers().indexOf(entity);
        double f = (idx == 0 || idx == 2) ? 0.1 : -1.2;
        double s = (idx == 0 || idx == 1) ? 0.7 : -0.7;
        f = getChestCount() == 2 ? -1.2 : f;
        s = getChestCount() == 2 ? (idx == 0 ? -0.7 : 0.7)  : s;
        final Vec3 forward = this.getLookAngle().scale(f);
        final Vec3 sideways = new Vec3(forward.z, 0, -forward.x).normalize().scale(s);
        return new Vec3(forward.x + sideways.x, getPassengersRidingOffsetY(entityDimensions, factor) + forward.y, forward.z + sideways.z);
    }

    @Override
    public void positionRider(final @NotNull Entity passenger, @NotNull MoveFunction moveFunction) {
        super.positionRider(passenger, moveFunction);
        int idx = this.getPassengers().indexOf(passenger);
        int dir = idx == 0 || idx == 3 ? 1 : -1;
        if (this.hasPassenger(passenger)) {
            passenger.setYBodyRot(this.getYRot() + (passenger instanceof TamableAnimal ? 180 : 90) * dir);
            final float f2 = Mth.wrapDegrees(passenger.getYRot() - this.getYRot() - (passenger instanceof TamableAnimal ? 180 : 90) * dir);
            final float clamped = Mth.clamp(f2, -105.0F, 105.0F);
            passenger.yRotO += clamped - f2;
            passenger.setYRot(passenger.getYRot() + (clamped - f2));
            passenger.setYHeadRot(passenger.getYRot());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (isLocked()) return;
        List<Entity> list = this.level().getEntities(this, this.getBoundingBox().inflate(0.2F, -0.01F, 0.2F), EntitySelector.pushableBy(this));
        if (!list.isEmpty()) {
            boolean bl = !this.level().isClientSide() && !(this.getControllingPassenger() instanceof Player);
            for (Entity entity : list) {
                if (!entity.hasPassenger(this)) {
                    if (bl
                            && canAddPassenger(entity)
                            && !entity.isPassenger()
                            && entity.getBbWidth() < this.getBbWidth() / 2
                            && entity.getBbWidth() * entity.getBbHeight() < 1.5
                            && entity instanceof LivingEntity
                            && !(entity instanceof AgeableWaterCreature)
                            && !(entity instanceof Player)) {
                        if(entity instanceof TamableAnimal tamable) tamable.setInSittingPose(true);
                        entity.startRiding(this);
                    }
                }
            }
        }
    }

    @Override
    protected AbstractContainerMenu createMenuLootUnpacked(int i, Inventory inventory, Player player) {
        var type = switch (this.getChestCount()) {
            case 1 -> NiftyCarts.WAGON_9x4_MENU_TYPE;
            case 2 -> NiftyCarts.WAGON_9x8_MENU_TYPE;
            case 3 -> NiftyCarts.WAGON_12x9_MENU_TYPE;
            default -> null;
        };
        if (type == null) return null;
        return new ChestMenu(type, i, inventory, this, getCurrentRowCount());
    }

    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Unfurl", this.entityData.get(UNFURL));
        output.putInt("RoofColor", this.entityData.get(ROOF_COLOR));
        output.putInt("ChestCount", this.entityData.get(CHEST_COUNT));
        if (!this.entityData.get(EQUIPPED_CARPET).isEmpty()) {
            var itemStack = this.entityData.get(EQUIPPED_CARPET);
            output.store("Carpet", ItemStack.CODEC, itemStack);
        }
        writeLeashData(output, this.leashData);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(UNFURL, input.getInt("Unfurl").orElse(0));
        this.entityData.set(ROOF_COLOR, input.getInt("RoofColor").orElse(-1));
        this.entityData.set(CHEST_COUNT, input.getInt("ChestCount").orElse(0));
        this.entityData.set(EQUIPPED_CARPET, input.read("Carpet", ItemStack.CODEC).orElse(ItemStack.EMPTY));
        readLeashData(input);
    }

    @Override
    protected void saveInventory(ValueOutput output) {
        ContainerHelper.saveAllItems(output, this.getItemStacks());
    }

    @Override
    protected void readInventory(ValueInput input) {
        ContainerHelper.loadAllItems(input, this.getItemStacks());
    }

    @Override
    public @org.jspecify.annotations.Nullable LeashData getLeashData() {
        return this.leashData;
    }

    @Override
    public void setLeashData(@org.jspecify.annotations.Nullable LeashData leashData) {
        this.leashData = leashData;
    }

    @Override
    public @NotNull Vec3 getLeashOffset() {
        return new Vec3(0.0, this.getEyeHeight(), this.getBbWidth() * 0.4f);
    }

    @Override
    public @NotNull Vec3 getRopeHoldPosition(float partialTicks) {
        final float yaw = (float) Math.toRadians(this.getYRot());
        final float nx = -Mth.sin(yaw);
        final float nz = Mth.cos(yaw);
        final double r = 0.2D;
        Vec3 target = new Vec3(nx * r, 0, nz * r).normalize();
        return this.getPosition(partialTicks).add(this.getBbWidth() * 0.8f * -target.x, this.getEyeHeight() * 0.35, this.getBbWidth() * 0.8f * -target.z);
    }

    @Override
    public void remove(@NotNull RemovalReason removalReason) {
        if (!this.level().isClientSide() && removalReason.shouldDestroy() && this.isLeashed()) {
            this.dropLeash();
        }
        super.remove(removalReason);
    }
}