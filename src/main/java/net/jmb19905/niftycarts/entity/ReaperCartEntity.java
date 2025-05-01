package net.jmb19905.niftycarts.entity;

import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ReaperCartEntity extends AbstractDrawnEntity {

    public ReaperCartEntity(EntityType<? extends Entity> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.spacing = 1.3D;
    }

    @Override
    public void tick() {
        super.tick();
        final Entity coachman = this.getControllingPassenger();
        final Entity pulling = this.getPulling();
        if (pulling != null && coachman != null && pulling.getControllingPassenger() == null) {
            final PostilionEntity postilion = NiftyCarts.POSTILION_ENTITY.create(this.level(), EntitySpawnReason.SPAWN_ITEM_USE);
            if (postilion != null) {
                postilion.snapTo(pulling.getX(), pulling.getY(), pulling.getZ(), coachman.getYRot(), coachman.getXRot());
                if (postilion.startRiding(pulling)) {
                    this.level().addFreshEntity(postilion);
                } else {
                    postilion.discard();
                }
            }
        }
    }

    public float getPassengersRidingOffsetY(EntityDimensions entityDimensions, float f) {
        //18/16
        return (entityDimensions.height() - 3f / 16f) * f;
    }

    @Override
    protected @NotNull Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
        final Vec3 forward = this.getLookAngle().scale(-0.45);
        return new Vec3(forward.x, getPassengersRidingOffsetY(entityDimensions, f) + forward.y, forward.z);
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction moveFunction) {
        super.positionRider(passenger, moveFunction);
        if (this.hasPassenger(passenger)) {
            passenger.setYBodyRot(this.getYRot());
            final float f2 = Mth.wrapDegrees(passenger.getYRot() - this.getYRot());
            final float f1 = Mth.clamp(f2, -105.0F, 105.0F);
            passenger.yRotO += f1 - f2;
            passenger.setYRot(passenger.getYRot() + (f1 - f2));
            passenger.setYHeadRot(passenger.getYRot());
        }
    }

    @Override
    public @NotNull InteractionResult interact(Player player, InteractionHand interactionHand) {
        if (!this.level().isClientSide) {
            if (!player.isSecondaryUseActive() && this.pulling != null && this.pulling != player) {
                if (player.startRiding(this)) {
                    return InteractionResult.CONSUME;
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void pulledTick() {
        super.pulledTick();
        if (this.getPulling() == null) {
            return;
        }
        if (!this.level().isClientSide) {
            if (this.getFirstPassenger() instanceof Player pl) {
                if (this.xo != this.getX() || this.zo != this.getZ()) {
                    this.harvest(pl);
                }
            }
        }
    }

    private void harvest(Player player) {
        for (float f = 0.9f; f <= 2; f += 0.1f) {
            final double blockPosX = this.getX() + Mth.sin((float) Math.toRadians(this.getYRot() + 90)) * f;
            final double blockPosZ = this.getZ() - Mth.cos((float) Math.toRadians(this.getYRot() + 90)) * f;
            final BlockPos blockPos = new BlockPos((int) blockPosX, (int) Math.round(this.getY() - 0.75D), (int) blockPosZ);
            BlockPos pos = blockPos.above();
            BlockState state = level().getBlockState(pos);
            if (state.is(BlockTags.CROPS)) {
                if (level().removeBlock(pos, false)) {
                    level().destroyBlock(pos, false);
                    if (!state.requiresCorrectToolForDrops()) {
                        Block.dropResources(state, level(), pos, level().getBlockEntity(pos), player, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    @Override
    public Item getCartItem() {
        return NiftyCarts.REAPER.get(getWoodType());
    }

    @Override
    protected NiftyCartsConfig.CartConfig getConfig() {
        return NiftyCartsConfig.get().reaper;
    }
}