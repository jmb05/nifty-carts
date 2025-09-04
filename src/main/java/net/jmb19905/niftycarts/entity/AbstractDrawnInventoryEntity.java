package net.jmb19905.niftycarts.entity;

import net.jmb19905.niftycarts.util.NiftyInventory;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractDrawnInventoryEntity extends AbstractDrawnEntity implements HasCustomInventoryScreen, ContainerEntity {

    private NiftyInventory itemStacks;
    private final int containerSize;
    @Nullable
    private ResourceKey<LootTable> lootTable;
    private long lootTableSeed;

    public AbstractDrawnInventoryEntity(EntityType<? extends Entity> entityTypeIn, Level worldIn, int containerSize) {
        super(entityTypeIn, worldIn);
        this.itemStacks = NiftyInventory.withSize(containerSize, ItemStack.EMPTY);
        this.containerSize = containerSize;
        this.itemStacks.setOnContentsChanged(this::onContentsChanged);
    }

    public boolean stillValid(Player player) {
        return this.isChestVehicleStillValid(player);
    }

    @Override
    public void onDestroyedAndDoDrops(DamageSource source) {
        if (!(this.level() instanceof ServerLevel)) return;
        this.chestVehicleDestroyed(source, (ServerLevel) this.level(), this);
    }

    public void remove(Entity.RemovalReason removalReason) {
        if (!this.level().isClientSide && removalReason.shouldDestroy()) {
            Containers.dropContents(this.level(), this, this);
        }
        super.remove(removalReason);
    }

    protected abstract InteractionResult onInteractNotOpen(Player player, InteractionHand hand);

    protected boolean canInteractNotOpen() {
        return true;
    }

    @Override
    public @NotNull InteractionResult interact(Player player, InteractionHand interactionHand) {
        if (canInteractNotOpen() && this.canAddPassenger(player) && !player.isSecondaryUseActive()) {
            return onInteractNotOpen(player, interactionHand);
        } else {
            InteractionResult interactionResult = this.interactWithContainerVehicle(player);
            if (interactionResult.consumesAction()) {
                this.gameEvent(GameEvent.CONTAINER_OPEN, player);
                if (this.level() instanceof ServerLevel serverLevel) {
                    PiglinAi.angerNearbyPiglins(serverLevel, player, true);
                }
            }

            return interactionResult;
        }
    }

    public void openCustomInventoryScreen(Player player) {
        player.openMenu(this);
        if (!player.level().isClientSide) {
            this.gameEvent(GameEvent.CONTAINER_OPEN, player);
            if (this.level() instanceof ServerLevel serverLevel) {
                PiglinAi.angerNearbyPiglins(serverLevel, player, true);
            }
        }
    }

    public void clearContent() {
        this.clearChestVehicleContent();
    }

    public int getContainerSize() {
        return containerSize;
    }

    public @NotNull ItemStack getItem(int i) {
        return this.getChestVehicleItem(i);
    }

    public @NotNull ItemStack removeItem(int i, int j) {
        return this.removeChestVehicleItem(i, j);
    }

    public @NotNull ItemStack removeItemNoUpdate(int i) {
        return this.removeChestVehicleItemNoUpdate(i);
    }

    public void setItem(int i, ItemStack itemStack) {
        this.setChestVehicleItem(i, itemStack);
    }

    public @NotNull SlotAccess getSlot(int i) {
        return this.getChestVehicleSlot(i);
    }

    public void setChanged() {
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        if (this.lootTable != null && player.isSpectator()) {
            return null;
        } else {
            this.unpackLootTable(inventory.player);
            return createMenuLootUnpacked(i, inventory, player);
        }
    }

    protected abstract AbstractContainerMenu createMenuLootUnpacked(int i, Inventory inventory, Player player);

    public @NotNull NonNullList<ItemStack> getItemStacks() {
        return this.itemStacks;
    }

    public void clearItemStacks() {
        this.itemStacks = NiftyInventory.withSize(this.getContainerSize(), ItemStack.EMPTY);
        this.itemStacks.setOnContentsChanged(this::onContentsChanged);
    }

    public void stopOpen(Player player) {
        this.level().gameEvent(GameEvent.CONTAINER_CLOSE, this.position(), GameEvent.Context.of(player));
    }

    protected void onContentsChanged(int slot) {}

    public void unpackLootTable(@Nullable Player player) {
        this.unpackChestVehicleLootTable(player);
    }

    @Nullable
    @Override
    public ResourceKey<LootTable> getContainerLootTable() {
        return this.lootTable;
    }

    @Override
    public void setContainerLootTable(@Nullable ResourceKey<LootTable> resourceLocation) {
        this.lootTable = resourceLocation;
    }

    @Override
    public long getContainerLootTableSeed() {
        return this.lootTableSeed;
    }

    @Override
    public void setContainerLootTableSeed(long l) {
        this.lootTableSeed = l;
    }


    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        saveInventory(compound);
    }

    protected abstract void saveInventory(CompoundTag tag);

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        readInventory(compound);
    }

    protected abstract void readInventory(CompoundTag tag);

}
