package net.jmb19905.astikorcarts.entity;

import net.jmb19905.astikorcarts.util.ACInventory;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractDrawnInventoryEntity extends AbstractDrawnEntity implements MenuProvider, Container {

    private ACInventory itemStacks;
    private final int containerSize;
    @Nullable
    private ResourceLocation lootTable;
    private long lootTableSeed;

    public AbstractDrawnInventoryEntity(EntityType<? extends Entity> entityTypeIn, Level worldIn, int containerSize) {
        super(entityTypeIn, worldIn);
        this.itemStacks = ACInventory.withSize(containerSize, ItemStack.EMPTY);
        this.containerSize = containerSize;
        this.itemStacks.setOnContentsChanged(this::onContentsChanged);
    }

    public boolean stillValid(Player player) {
        if (this.isRemoved()) {
            return false;
        } else {
            return !(player.distanceToSqr(this) > 64.0);
        }
    }

    @Override
    public void onDestroyedAndDoDrops(DamageSource source) {
        this.chestVehicleDestroyed(source, this.level, this);
    }

    public void remove(Entity.RemovalReason removalReason) {
        if (!this.level.isClientSide && removalReason.shouldDestroy()) {
            Containers.dropContents(this.level, this, this);
        }
        super.remove(removalReason);
    }

    protected abstract InteractionResult onInteractNotOpen(Player player, InteractionHand hand);

    @Override
    public @NotNull InteractionResult interact(Player player, InteractionHand interactionHand) {
        if (this.canAddPassenger(player) && !player.isSecondaryUseActive()) {
            return onInteractNotOpen(player, interactionHand);
        } else {
            player.openMenu(this);
            if (!player.level.isClientSide) {
                this.gameEvent(GameEvent.CONTAINER_OPEN, player);
                PiglinAi.angerNearbyPiglins(player, true);
                return InteractionResult.CONSUME;
            } else {
                return InteractionResult.SUCCESS;
            }
        }
    }

    public void openCustomInventoryScreen(Player player) {
        player.openMenu(this);
        if (!player.level.isClientSide) {
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

    public @NotNull NonNullList<ItemStack> getItemStacks() {
        return this.itemStacks;
    }

    public void clearItemStacks() {
        this.itemStacks = ACInventory.withSize(this.getContainerSize(), ItemStack.EMPTY);
        this.itemStacks.setOnContentsChanged(this::onContentsChanged);
    }

    public void stopOpen(Player player) {
        this.level.gameEvent(player, GameEvent.CONTAINER_CLOSE, this.getOnPos());
    }

    protected void onContentsChanged(int slot) {}

}
