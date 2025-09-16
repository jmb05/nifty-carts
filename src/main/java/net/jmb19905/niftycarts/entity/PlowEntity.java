package net.jmb19905.niftycarts.entity;

import com.google.common.collect.ImmutableList;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsConfig;
import net.jmb19905.niftycarts.advancement.NCCriteriaTriggers;
import net.jmb19905.niftycarts.container.PlowMenu;
import net.jmb19905.niftycarts.util.NiftyItemUtil;
import net.jmb19905.niftycarts.util.ProxyItemUseContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

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
    }

    @Override
    protected double getSpacing() {
        return 1.3;
    }

    @Override
    public NiftyCartsConfig.CartConfig getConfig() {
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
            Optional<Player> playerOptional = getControllingPlayer();
            if (getPlowing() && playerOptional.isPresent()) {
                if (this.xo != this.getX() || this.zo != this.getZ()) {
                    this.plow((ServerPlayer) playerOptional.get());
                }
            }
        }
    }

    private void plow(final ServerPlayer player) {
        for (int i = 0; i < SLOT_COUNT; i++) {
            final ItemStack stack = this.getStackInSlot(i);
            if (NiftyItemUtil.isTool(stack)) {
                final float offset = 38.0F - i * 38.0F;
                final double blockPosX = this.getX() + Mth.sin((float) Math.toRadians(this.getYRot() - offset)) * BLADEOFFSET;
                final double blockPosZ = this.getZ() - Mth.cos((float) Math.toRadians(this.getYRot() - offset)) * BLADEOFFSET;
                final BlockPos blockPos = new BlockPos((int) blockPosX, (int) Math.round(this.getY() - 0.75D), (int) blockPosZ);
                final boolean damageable = stack.isDamageableItem();
                final int count = stack.getCount();
                tryBreakBlock(stack, blockPos.above(), level(), player);
                InteractionResult result = stack.getItem().useOn(new ProxyItemUseContext(player, stack, new BlockHitResult(Vec3.ZERO, Direction.UP, blockPos, false)));
                if (result.consumesAction()) NCCriteriaTriggers.USE_PLOW.trigger(player, stack);
                if (damageable && stack.getCount() < count) {
                    this.playSound(SoundEvents.ITEM_BREAK.value(), 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
                    this.updateSlot(i);
                }
            }
        }
    }

    private void tryBreakBlock(ItemStack stack, BlockPos pos, Level level, Player player) {
        BlockState state = level.getBlockState(pos);
        TagKey<Block> tag;
        switch (stack.getItem()) {
            case HoeItem ignored -> tag = NiftyCarts.PLOW_BREAKABLE_HOE;
            case ShovelItem ignored -> tag = NiftyCarts.PLOW_BREAKABLE_SHOVEL;
            case AxeItem ignored -> tag = NiftyCarts.PLOW_BREAKABLE_AXE;
            default -> {
                return;
            }
        }
        if (state.isAir()) return;
        if (state.is(tag)) {
            if (level.removeBlock(pos, false)) {
                level.destroyBlock(pos, false);
                if (!state.requiresCorrectToolForDrops() || stack.isCorrectToolForDrops(state)) {
                    Block.dropResources(state, level, pos, level.getBlockEntity(pos), player, stack);
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
        return NiftyCarts.PLOW.get(getWoodType());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PLOWING, false);
        for (final EntityDataAccessor<ItemStack> param : TOOLS) {
            builder.define(param, ItemStack.EMPTY);
        }
    }

    @Override
    protected InteractionResult onInteractNotOpen(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            this.entityData.set(PLOWING, !this.entityData.get(PLOWING));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Plowing", this.entityData.get(PLOWING));
    }

    @Override
    protected void saveInventory(ValueOutput output) {
        ContainerHelper.saveAllItems(output, this.getItemStacks());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(PLOWING, input.getBooleanOr("Plowing", false));
    }

    @Override
    protected void readInventory(ValueInput input) {
        ContainerHelper.loadAllItems(input, this.getItemStacks());
    }

}