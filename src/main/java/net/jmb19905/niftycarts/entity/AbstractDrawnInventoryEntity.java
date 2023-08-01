package net.jmb19905.niftycarts.entity;

import net.jmb19905.niftycarts.util.NCInventory;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractDrawnInventoryEntity extends AbstractDrawnEntity implements HasCustomInventoryScreen, ContainerEntity {

    private NCInventory itemStacks;
    private final int containerSize;
    @Nullable
    private ResourceLocation lootTable;
    private long lootTableSeed;

    public AbstractDrawnInventoryEntity(EntityType<? extends Entity> entityTypeIn, Level worldIn, int containerSize) {
        super(entityTypeIn, worldIn);
        this.itemStacks = NCInventory.withSize(containerSize, ItemStack.EMPTY);
        this.containerSize = containerSize;
        this.itemStacks.setOnContentsChanged(this::onContentsChanged);
    }

    public boolean stillValid(Player player) {
        return this.isChestVehicleStillValid(player);
    }

    @Override
    public void onDestroyedAndDoDrops(DamageSource source) {
        this.chestVehicleDestroyed(source, this.level(), this);
    }

    public void remove(Entity.RemovalReason removalReason) {
        if (!this.level().isClientSide && removalReason.shouldDestroy()) {
            Containers.dropContents(this.level(), this, this);
        }
        super.remove(removalReason);
    }

    protected abstract InteractionResult onInteractNotOpen(Player player, InteractionHand hand);

    @Override
    public @NotNull InteractionResult interact(Player player, InteractionHand interactionHand) {
        if (this.canAddPassenger(player) && !player.isSecondaryUseActive()) {
            return onInteractNotOpen(player, interactionHand);
        } else {
            InteractionResult interactionResult = this.interactWithContainerVehicle(player);
            if (interactionResult.consumesAction()) {
                this.gameEvent(GameEvent.CONTAINER_OPEN, player);
                PiglinAi.angerNearbyPiglins(player, true);
            }

            return interactionResult;
        }
    }

    public void openCustomInventoryScreen(Player player) {
        player.openMenu(this);
        if (!player.level().isClientSide) {
            this.gameEvent(GameEvent.CONTAINER_OPEN, player);
            PiglinAi.angerNearbyPiglins(player, true);
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
        this.itemStacks = NCInventory.withSize(this.getContainerSize(), ItemStack.EMPTY);
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
    public ResourceLocation getLootTable() {
        return this.lootTable;
    }

    public void setLootTable(@Nullable ResourceLocation resourceLocation) {
        this.lootTable = resourceLocation;
    }

    public long getLootTableSeed() {
        return this.lootTableSeed;
    }

    public void setLootTableSeed(long l) {
        this.lootTableSeed = l;
    }

}
