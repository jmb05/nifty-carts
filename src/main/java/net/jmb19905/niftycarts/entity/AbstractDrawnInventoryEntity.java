package net.jmb19905.niftycarts.entity;

import net.jmb19905.niftycarts.util.NCInventory;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public abstract class AbstractDrawnInventoryEntity extends AbstractDrawnEntity implements MenuProvider, Container {

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
        if (this.isRemoved()) {
            return false;
        } else {
            return !(player.distanceToSqr(this) > 64.0);
        }
    }

    @Override
    public void onDestroyedAndDoDrops(DamageSource source) {
        Containers.dropContents(this.level, this, this);
        if (!this.level.isClientSide) {
            Entity entity = source.getDirectEntity();
            if (entity != null && entity.getType() == EntityType.PLAYER) {
                PiglinAi.angerNearbyPiglins((Player)entity, true);
            }
        }
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
        this.itemStacks.clear();
    }

    public int getContainerSize() {
        return containerSize;
    }

    @Override
    public boolean isEmpty() {
        Iterator<ItemStack> var1 = this.itemStacks.iterator();

        ItemStack itemStack;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemStack = var1.next();
        } while(itemStack.isEmpty());

        return false;
    }

    public @NotNull ItemStack getItem(int i) {
        this.unpackLootTable((Player)null);
        return this.itemStacks.get(i);
    }

    public @NotNull ItemStack removeItem(int i, int j) {
        this.unpackLootTable((Player)null);
        return ContainerHelper.removeItem(this.itemStacks, i, j);
    }

    public @NotNull ItemStack removeItemNoUpdate(int i) {
        this.unpackLootTable((Player)null);
        ItemStack itemStack = this.itemStacks.get(i);
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.itemStacks.set(i, ItemStack.EMPTY);
            return itemStack;
        }
    }

    public void setItem(int i, ItemStack itemStack) {
        this.unpackLootTable((Player)null);
        this.itemStacks.set(i, itemStack);
        if (!itemStack.isEmpty() && itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
        }

    }

    public @NotNull SlotAccess getSlot(final int i) {
        return i >= 0 && i < this.getContainerSize() ? new SlotAccess() {
            public @NotNull ItemStack get() {
                return AbstractDrawnInventoryEntity.this.getItem(i);
            }

            public boolean set(ItemStack itemStack) {
                AbstractDrawnInventoryEntity.this.setItem(i, itemStack);
                return true;
            }
        } : super.getSlot(i);
    }

    public void setChanged() {}

    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        if (this.lootTable != null) {
            compoundTag.putString("LootTable", this.lootTable.toString());
            if (this.lootTableSeed != 0L) {
                compoundTag.putLong("LootTableSeed", this.lootTableSeed);
            }
        } else {
            ContainerHelper.saveAllItems(compoundTag, this.itemStacks);
        }

    }

    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.itemStacks = NCInventory.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (compoundTag.contains("LootTable", 8)) {
            this.lootTable = new ResourceLocation(compoundTag.getString("LootTable"));
            this.lootTableSeed = compoundTag.getLong("LootTableSeed");
        } else {
            ContainerHelper.loadAllItems(compoundTag, this.itemStacks);
        }

    }

    public void unpackLootTable(@Nullable Player player) {
        if (this.lootTable != null && this.level.getServer() != null) {
            LootTable lootTable = this.level.getServer().getLootTables().get(this.lootTable);
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer)player, this.lootTable);
            }

            this.lootTable = null;
            LootContext.Builder builder = (new LootContext.Builder((ServerLevel)this.level)).withParameter(LootContextParams.ORIGIN, this.position()).withOptionalRandomSeed(this.lootTableSeed);
            if (player != null) {
                builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
            }

            lootTable.fill(this, builder.create(LootContextParamSets.CHEST));
        }
    }

    public void setLootTable(ResourceLocation resourceLocation, long l) {
        this.lootTable = resourceLocation;
        this.lootTableSeed = l;
    }

    @Nullable
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        if (this.lootTable != null && player.isSpectator()) {
            return null;
        } else {
            this.unpackLootTable(inventory.player);
            return this.createMenu(i, inventory);
        }
    }

    public @NotNull NonNullList<ItemStack> getItemStacks() {
        return this.itemStacks;
    }

    public void stopOpen(Player player) {
        this.level.gameEvent(player, GameEvent.CONTAINER_CLOSE, this.getOnPos());
    }

    protected void onContentsChanged(int slot) {}

    protected abstract AbstractContainerMenu createMenu(int i, Inventory inventory);

}
