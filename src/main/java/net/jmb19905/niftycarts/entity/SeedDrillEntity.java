package net.jmb19905.niftycarts.entity;

import com.google.common.collect.ImmutableList;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsConfig;
import net.jmb19905.niftycarts.advancement.NCCriteriaTriggers;
import net.jmb19905.niftycarts.container.SeedDrillMenu;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SeedDrillEntity extends AbstractDrawnInventoryEntity {

    private static final int SLOT_COUNT = 9;
    private static final ImmutableList<@NotNull EntityDataAccessor<@NotNull ItemStack>> SEEDS = ImmutableList.of(
            SynchedEntityData.defineId(SeedDrillEntity.class, EntityDataSerializers.ITEM_STACK),
            SynchedEntityData.defineId(SeedDrillEntity.class, EntityDataSerializers.ITEM_STACK),
            SynchedEntityData.defineId(SeedDrillEntity.class, EntityDataSerializers.ITEM_STACK),
            SynchedEntityData.defineId(SeedDrillEntity.class, EntityDataSerializers.ITEM_STACK),
            SynchedEntityData.defineId(SeedDrillEntity.class, EntityDataSerializers.ITEM_STACK),
            SynchedEntityData.defineId(SeedDrillEntity.class, EntityDataSerializers.ITEM_STACK),
            SynchedEntityData.defineId(SeedDrillEntity.class, EntityDataSerializers.ITEM_STACK),
            SynchedEntityData.defineId(SeedDrillEntity.class, EntityDataSerializers.ITEM_STACK),
            SynchedEntityData.defineId(SeedDrillEntity.class, EntityDataSerializers.ITEM_STACK));

    public SeedDrillEntity(EntityType<? extends @NotNull Entity> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn, SLOT_COUNT);
    }

    @Override
    protected double getSpacing() {
        return 1.3;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void plant(Optional<ServerPlayer> playerOptional) {
        for (int i = 0; i < 3; i++) {
            int j = this.level().random.nextInt(SLOT_COUNT);
            final ItemStack stack = this.getStackInSlot(j);
            final float f = i - 1;
            final double x = this.getX() + Mth.sin((float) Math.toRadians(this.getYRot() + 90)) * f;
            final double z = this.getZ() - Mth.cos((float) Math.toRadians(this.getYRot() + 90)) * f;
            final BlockPos blockPos = new BlockPos((int) Math.round(x - 0.5), (int) Math.round(this.getY() - 0.75D), (int) Math.round(z - 0.5));
            if (tryPlaceCrop(stack, blockPos.above(), level(), j)) {
                playerOptional.ifPresent(player -> {
                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                    CriteriaTriggers.PLACED_BLOCK.trigger(player, blockPos.above(), stack);
                    NCCriteriaTriggers.SEED_DRILL_PLACE.trigger(player, stack);
                });
                break;
            }
        }
    }

    private boolean tryPlaceCrop(ItemStack stack, BlockPos pos, Level level, int slot) {
        if (stack.getItem() instanceof BlockItem item) {
            if (stack.is(NiftyCarts.SEED_DRILL_PLANTABLE) || item.getBlock() instanceof CropBlock) {
                Block block = item.getBlock();
                if (level.getBlockState(pos).isAir() && block.defaultBlockState().canSurvive(level, pos)) {
                    level.setBlockAndUpdate(pos, block.defaultBlockState());
                    stack.shrink(1);
                    onContentsChanged(slot);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void pulledTick() {
        super.pulledTick();
        if (this.getPulling() == null) {
            return;
        }
        if (!this.level().isClientSide()) {
            if (this.xo != this.getX() || this.zo != this.getZ()) {
                this.plant(getControllingPlayer().flatMap(pl -> Optional.of((ServerPlayer) pl)));
            }
        }
    }

    @Override
    protected void onContentsChanged(int slot) {
        updateSlot(slot);
    }

    public void updateSlot(final int slot) {
        if (!this.level().isClientSide()) {
            if (this.getItemStacks().get(slot).isEmpty()) {
                this.entityData.set(SEEDS.get(slot), ItemStack.EMPTY);
            } else {
                this.entityData.set(SEEDS.get(slot), this.getItemStacks().get(slot));
            }
        }
    }

    public ItemStack getStackInSlot(final int i) {
        return this.entityData.get(SEEDS.get(i));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        for (final EntityDataAccessor<@NotNull ItemStack> param : SEEDS) {
            builder.define(param, ItemStack.EMPTY);
        }
    }

    @Override
    protected InteractionResult onInteractNotOpen(Player player, InteractionHand hand) {
        return InteractionResult.SUCCESS;
    }

    @Override
    protected AbstractContainerMenu createMenuLootUnpacked(int i, Inventory inventory, Player player) {
        return new SeedDrillMenu(i, inventory, this);
    }

    @Override
    public Item getCartItem() {
        return NiftyCarts.SEED_DRILL.get(getWoodType());
    }

    @Override
    public NiftyCartsConfig.CartConfig getConfig() {
        return NiftyCartsConfig.get().seedDrill;
    }

    @Override
    protected void saveInventory(ValueOutput output) {
        ContainerHelper.saveAllItems(output, this.getItemStacks());
    }

    @Override
    protected void readInventory(ValueInput input) {
        ContainerHelper.loadAllItems(input, this.getItemStacks());
    }
}
