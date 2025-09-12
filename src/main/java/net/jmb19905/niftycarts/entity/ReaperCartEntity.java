package net.jmb19905.niftycarts.entity;

import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("resource")
public class ReaperCartEntity extends AbstractDrawnEntity {

    private static final EntityDataAccessor<Boolean> FOLDED = SynchedEntityData.defineId(ReaperCartEntity.class, EntityDataSerializers.BOOLEAN);

    public ReaperCartEntity(EntityType<? extends Entity> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected double getSpacing() {
        return 1.3d;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FOLDED, true);
    }

    public boolean isFolded() {
        return this.entityData.get(FOLDED);
    }

    @Override
    public double getPassengersRidingOffset() {
        return 17.0D / 16.0D;
    }

    @Override
    public void tick() {
        super.tick();
        final Entity coachman = this.getControllingPassenger();
        final Entity pulling = this.getPulling();
        boolean folded = !(pulling != null && coachman != null);
        if (folded != this.entityData.get(FOLDED)) {
            playSound(SoundEvents.WOODEN_TRAPDOOR_CLOSE);
        }
        this.entityData.set(FOLDED, folded);
        managePostilion();
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction moveFunction) {
        if (this.hasPassenger(passenger)) {
            final Vec3 forward = this.getLookAngle();
            final Vec3 origin = new Vec3(0.0D, this.getPassengersRidingOffset(), 0);
            final Vec3 pos = origin.add(forward.scale(-0.4D));
            moveFunction.accept(passenger, this.getX() + pos.x, this.getY() + pos.y - 0.1D + passenger.getMyRidingOffset(), this.getZ() + pos.z);
            clampRiderRotation(passenger);
        }
    }

    @Override
    public @NotNull InteractionResult interact(Player player, InteractionHand interactionHand) {
        InteractionResult result = super.interact(player, interactionHand);
        if (result == InteractionResult.FAIL) return result;
        if (!this.level().isClientSide) {
            if (player.isSecondaryUseActive()) {
                player.displayClientMessage(Component.translatable("message.niftycarts.use_reaper"), true);
            } else if (!player.isSecondaryUseActive() && this.pulling != null && this.pulling != player) {
                if (player.startRiding(this)) {
                    return InteractionResult.CONSUME;
                }
            }
        }
        return InteractionResult.sidedSuccess(player.level().isClientSide);
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
                    this.harvest((ServerPlayer) pl);
                }
            }
        }
    }

    private void harvest(ServerPlayer player) {
        for (int i = 0; i <= 12; i += 2) {
            float f = 1.1f + ((float) i / 10f);
            final double x = this.getX() + Mth.sin((float) Math.toRadians(this.getYRot() + 90)) * f;
            final double z = this.getZ() - Mth.cos((float) Math.toRadians(this.getYRot() + 90)) * f;
            final BlockPos blockPos = new BlockPos((int) Math.round(x - 0.5), (int) Math.round(this.getY() - 0.75D), (int) Math.round(z - 0.5));
            BlockPos pos = blockPos.above();
            BlockState state = level().getBlockState(pos);
            if (state.is(NiftyCarts.REAPER_HARVESTABLE)) {
                if (level().removeBlock(pos, false)) {
                    NiftyCarts.REAPER_HARVEST_CRITERION.trigger(player, state);
                    level().destroyBlock(pos, false);
                    if (!state.requiresCorrectToolForDrops()) {
                        Block.dropResources(state, level(), pos, level().getBlockEntity(pos), player, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    @Override
    public Map<Vector3f, List<AABB>> getAdditionalColoredDebugBoxes() {
        Map<Vector3f, List<AABB>> boxes = new HashMap<>();
        Vector3f red = new Vector3f(1, 0, 0);
        Vector3f green = new Vector3f(0, 1, 0);
        boxes.put(red, new ArrayList<>());
        boxes.put(green, new ArrayList<>());
        for (int i = 0; i <= 12; i += 2) {
            float f = 1.1f + ((float) i / 10f);
            final double x = this.getX() + Mth.sin((float) Math.toRadians(this.getYRot() + 90)) * f;
            final double z = this.getZ() - Mth.cos((float) Math.toRadians(this.getYRot() + 90)) * f;
            final BlockPos blockPos = new BlockPos((int) Math.round(x - 0.5), (int) Math.round(this.getY() - 0.75D), (int) Math.round(z - 0.5));
            AABB box = new AABB(x - 0.5, getY() - 0.1, z - 0.5, x + 0.5, getY() + 0.1, z + 0.5);
            boxes.get(red).add(box);
            boxes.get(green).add(new AABB(blockPos.above()));
        }
        return boxes;
    }

    @Override
    public Item getCartItem() {
        return NiftyCarts.REAPER.get(getWoodType());
    }

    @Override
    public NiftyCartsConfig.CartConfig getConfig() {
        return NiftyCartsConfig.get().reaper;
    }
}
