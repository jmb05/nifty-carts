package net.jmb19905.niftycarts.entity;

import com.google.common.collect.ImmutableList;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsConfig;
import net.jmb19905.niftycarts.container.PlowMenu;
import net.jmb19905.niftycarts.util.ProxyItemUseContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public final class PlowEntity extends AbstractDrawnInventoryEntity {
    private static final int SLOT_COUNT = 3;
    private static final double BLADEOFFSET = 1.7D;
    private static final EntityDataAccessor<Boolean> PLOWING = SynchedEntityData.defineId(PlowEntity.class, EntityDataSerializers.BOOLEAN);
    private static final ImmutableList<EntityDataAccessor<ItemStack>> TOOLS = ImmutableList.of(
            SynchedEntityData.defineId(PlowEntity.class, EntityDataSerializers.ITEM_STACK),
            SynchedEntityData.defineId(PlowEntity.class, EntityDataSerializers.ITEM_STACK),
            SynchedEntityData.defineId(PlowEntity.class, EntityDataSerializers.ITEM_STACK));

    public PlowEntity(final EntityType<? extends Entity> entityTypeIn, final Level worldIn) {
        super(entityTypeIn, worldIn, SLOT_COUNT);
        this.spacing = 1.3D;
    }

    @Override
    protected NiftyCartsConfig.CartConfig getConfig() {
        return NiftyCartsConfig.get().plow;
    }

    public boolean getPlowing() {
        return this.entityData.get(PLOWING);
    }

    @Override
    public void pulledTick() {
        super.pulledTick();
        if (this.getPulling() == null) {
            return;
        }
        if (!this.level().isClientSide) {
            Player player = null;
            if (this.getPulling() instanceof Player pl) {
                player = pl;
            } else if (this.getPulling().getControllingPassenger() instanceof Player pl) {
                player = pl;
            }
            if (this.entityData.get(PLOWING) && player != null) {
                if (this.xo != this.getX() || this.zo != this.getZ()) {
                    this.plow(player);
                }
            }
        }
    }

    private void plow(final Player player) {
        for (int i = 0; i < SLOT_COUNT; i++) {
            final ItemStack stack = this.getStackInSlot(i);
            final float offset = 38.0F - i * 38.0F;
            final double blockPosX = this.getX() + Mth.sin((float) Math.toRadians(this.getYRot() - offset)) * BLADEOFFSET;
            final double blockPosZ = this.getZ() - Mth.cos((float) Math.toRadians(this.getYRot() - offset)) * BLADEOFFSET;
            final BlockPos blockPos = new BlockPos((int) blockPosX, (int) Math.round(this.getY() - 0.75D), (int) blockPosZ);
            final boolean damageable = stack.isDamageableItem();
            final int count = stack.getCount();
            tryBreakBlock(stack, blockPos.above(), level(), player);
            tryPlaceBlock(stack, blockPos.above(), level(), i);
            stack.getItem().useOn(new ProxyItemUseContext(player, stack, new BlockHitResult(Vec3.ZERO, Direction.UP, blockPos, false)));
            if (damageable && stack.getCount() < count) {
                this.playSound(SoundEvents.ITEM_BREAK, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
                this.updateSlot(i);
            }
        }
    }

    private void tryPlaceBlock(ItemStack stack, BlockPos pos, Level level, int slot) {
        var list = NiftyCartsConfig.get().plow.sowItems.get();
        if (list.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString())) {
            int i = list.indexOf(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
            ResourceLocation id = ResourceLocation.tryParse(NiftyCartsConfig.get().plow.sowItems.get().get(i));
            if (id == null) return;
            Item item = BuiltInRegistries.ITEM.get(id);
            if (item instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                //noinspection deprecation
                if (block.canSurvive(block.defaultBlockState(), level, pos)) {
                    level.setBlockAndUpdate(pos, block.defaultBlockState());
                    stack.setCount(stack.getCount() - 1);
                    onContentsChanged(slot);
                }
            }
        }
    }

    private void tryBreakBlock(ItemStack stack, BlockPos pos, Level level, Player player) {
        BlockState state = level.getBlockState(pos);
        List<TagKey<Block>> tags = new ArrayList<>();

        if (NiftyCartsConfig.get().plow.harvestItems.get().contains(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString())) {
            tags.add(BlockTags.CROPS);
        } if (stack.getItem() instanceof HoeItem) {
            tags.add(NiftyCarts.PLOW_BREAKABLE_HOE);
        } if (stack.getItem() instanceof ShovelItem) {
            tags.add(NiftyCarts.PLOW_BREAKABLE_SHOVEL);
        } if (stack.getItem() instanceof AxeItem) {
            tags.add(NiftyCarts.PLOW_BREAKABLE_AXE);
        } if (state.isAir()) return;
        for (TagKey<Block> tag : tags) {
            if (state.is(tag)) {
                if (level.removeBlock(pos, false)) {
                    level.destroyBlock(pos, false);
                    if (!state.requiresCorrectToolForDrops() || stack.isCorrectToolForDrops(state)) {
                        Block.dropResources(state, level, pos, level.getBlockEntity(pos), player, stack);
                    }
                }
            }
        }
    }

    @Override
    protected AbstractContainerMenu createMenuLootUnpacked(int i, Inventory inventory, Player player) {
        return new PlowMenu(i, inventory, this);
    }

    @Override
    protected void onContentsChanged(int slot) {
        updateSlot(slot);
    }

    public void updateSlot(final int slot) {
        if (!this.level().isClientSide) {
            if (this.getItemStacks().get(slot).isEmpty()) {
                this.entityData.set(TOOLS.get(slot), ItemStack.EMPTY);
            } else {
                this.entityData.set(TOOLS.get(slot), this.getItemStacks().get(slot));
            }

        }
    }

    public ItemStack getStackInSlot(final int i) {
        return this.entityData.get(TOOLS.get(i));
    }

    @Override
    public Item getCartItem() {
        return NiftyCarts.PLOW;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PLOWING, false);
        for (final EntityDataAccessor<ItemStack> param : TOOLS) {
            this.entityData.define(param, ItemStack.EMPTY);
        }
    }

    @Override
    protected InteractionResult onInteractNotOpen(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            this.entityData.set(PLOWING, !this.entityData.get(PLOWING));
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        ContainerHelper.saveAllItems(compound, this.getItemStacks());
        compound.putBoolean("Plowing", this.entityData.get(PLOWING));
    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        ContainerHelper.loadAllItems(compound, this.getItemStacks());
        this.entityData.set(PLOWING, compound.getBoolean("Plowing"));
    }

}