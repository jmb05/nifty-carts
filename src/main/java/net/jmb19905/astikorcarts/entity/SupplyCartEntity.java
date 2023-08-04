package net.jmb19905.astikorcarts.entity;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.jmb19905.astikorcarts.AstikorCarts;
import net.jmb19905.astikorcarts.AstikorCartsConfig;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

public class SupplyCartEntity extends AbstractDrawnInventoryEntity {
    private static final ImmutableList<EntityDataAccessor<ItemStack>> CARGO = ImmutableList.of(
            SynchedEntityData.defineId(SupplyCartEntity.class, EntityDataSerializers.ITEM_STACK),
            SynchedEntityData.defineId(SupplyCartEntity.class, EntityDataSerializers.ITEM_STACK),
            SynchedEntityData.defineId(SupplyCartEntity.class, EntityDataSerializers.ITEM_STACK),
            SynchedEntityData.defineId(SupplyCartEntity.class, EntityDataSerializers.ITEM_STACK));

    private static final int CONTAINER_SIZE = 54;

    public SupplyCartEntity(EntityType<? extends Entity> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn, CONTAINER_SIZE);
    }

    @Override
    public Item getCartItem() {
        return AstikorCarts.SUPPLY_CART;
    }

    @Override
    protected AstikorCartsConfig.CartConfig getConfig() {
        return AstikorCartsConfig.get().supplyCart;
    }

    @Override
    public double getPassengersRidingOffset() {
        return 11.0D / 16.0D;
    }

    @Override
    public void positionRider(Entity passenger) {
        if (this.hasPassenger(passenger)) {
            final Vec3 forward = this.getLookAngle();
            final Vec3 origin = new Vec3(0.0D, this.getPassengersRidingOffset(), 1.0D / 16.0D);
            final Vec3 pos = origin.add(forward.scale(-0.68D));
            passenger.setPos(this.getX() + pos.x, this.getY() + pos.y - 0.1D + passenger.getMyRidingOffset(), this.getZ() + pos.z);
            passenger.setYBodyRot(this.getYRot() + 180.0F);
            final float f2 = Mth.wrapDegrees(passenger.getYRot() - this.getYRot() + 180.0F);
            final float f1 = Mth.clamp(f2, -105.0F, 105.0F);
            passenger.yRotO += f1 - f2;
            passenger.setYRot(passenger.getYRot() + (f1 - f2));
            passenger.setYHeadRot(passenger.getYRot());
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        for (final EntityDataAccessor<ItemStack> parameter : CARGO) {
            this.entityData.define(parameter, ItemStack.EMPTY);
        }
    }

    public NonNullList<ItemStack> getCargo() {
        final NonNullList<ItemStack> cargo = NonNullList.withSize(CARGO.size(), ItemStack.EMPTY);
        for (int i = 0; i < CARGO.size(); i++) {
            cargo.set(i, this.entityData.get(CARGO.get(i)));
        }
        return cargo;
    }

    @Override
    protected InteractionResult onInteractNotOpen(Player player, InteractionHand hand) {
        final InteractionResult bannerResult = this.useBanner(player, hand);
        if (bannerResult.consumesAction()) {
            return bannerResult;
        }
        if (this.isVehicle()) {
            return InteractionResult.PASS;
        }
        if (!this.level.isClientSide) {
            return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected AbstractContainerMenu createMenuLootUnpacked(int i, Inventory inventory, Player player) {
        return ChestMenu.sixRows(i, inventory, this);
    }

    @Override
    protected void onContentsChanged(int slot) {
        final Object2IntMap<Item> totals = new Object2IntLinkedOpenHashMap<>();
        final Object2ObjectMap<Item, ItemStack> stacks = new Object2ObjectOpenHashMap<>();
        for (int i = 0; i < this.getItemStacks().size(); i++) {
            final ItemStack stack = this.getItemStacks().get(i);
            if (!stack.isEmpty()) {
                totals.mergeInt(stack.getItem(), 1, Integer::sum);
                stacks.putIfAbsent(stack.getItem(), stack);
            }
        }
        final Iterator<Object2IntMap.Entry<Item>> topTotals = totals.object2IntEntrySet().stream()
                .sorted(Comparator.<Object2IntMap.Entry<Item>>comparingInt(e -> e.getKey() instanceof BlockItem ? 0 : 1)
                        .thenComparingInt(e -> -e.getIntValue()))
                .limit(CARGO.size()).iterator();
        final ItemStack[] items = new ItemStack[CARGO.size()];
        Arrays.fill(items, ItemStack.EMPTY);
        final int forth = this.getItemStacks().size() / CARGO.size();
        for (int pos = 0; topTotals.hasNext() && pos < CARGO.size(); ) {
            final Object2IntMap.Entry<Item> entry = topTotals.next();
            final int count = Math.max(1, (entry.getIntValue() + forth / 2) / forth);
            for (int n = 1; n <= count && pos < CARGO.size(); n++) {
                final ItemStack stack = stacks.getOrDefault(entry.getKey(), ItemStack.EMPTY).copy();
                stack.setCount(Math.min(stack.getMaxStackSize(), entry.getIntValue() / n));
                items[pos++] = stack;
            }
        }
        for (int i = 0; i < CARGO.size(); i++) {
            this.getEntityData().set(CARGO.get(i), items[i]);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        this.addChestVehicleSaveData(compound);
    }

    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.readChestVehicleSaveData(compoundTag);
    }

}
