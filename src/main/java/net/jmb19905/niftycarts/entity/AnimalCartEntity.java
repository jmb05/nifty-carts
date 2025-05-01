package net.jmb19905.niftycarts.entity;

import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsConfig;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public final class AnimalCartEntity extends AbstractDrawnEntity {
    public AnimalCartEntity(final EntityType<? extends Entity> entityTypeIn, final Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected NiftyCartsConfig.CartConfig getConfig() {
        return NiftyCartsConfig.get().animalCart;
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

    @Override
    public @NotNull InteractionResult interact(final Player player, final InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            if (!this.level().isClientSide) {
                for (final Entity entity : this.getPassengers()) {
                    if (!(entity instanceof Player)) {
                        entity.stopRiding();
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }
        final InteractionResult bannerResult = this.useBanner(player, hand);
        if (bannerResult.consumesAction()) {
            return bannerResult;
        }
        if (this.getPulling() != player) {
            if (!this.canAddPassenger(player)) {
                return InteractionResult.PASS;
            }
            if (!this.level().isClientSide) {
                return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void push(final Entity entityIn) {
        if (!entityIn.hasPassenger(this)) {
            if (!this.level().isClientSide && this.getPulling() != entityIn && this.getControllingPassenger() == null && this.getPassengers().size() < 2 && !entityIn.isPassenger() && entityIn.getBbWidth() < this.getBbWidth() && entityIn instanceof LivingEntity
                    && !(entityIn instanceof WaterAnimal) && !(entityIn instanceof Player)) {
                entityIn.startRiding(this);
            } else {
                super.push(entityIn);
            }
        }
    }

    @Override
    protected boolean canAddPassenger(final Entity passenger) {
        return this.getPassengers().size() < 2;
    }

    public float getPassengersRidingOffsetY(EntityDimensions entityDimensions, float f) {
        return (entityDimensions.height() - 8f / 16f) * f;
    }

    @Override
    protected @NotNull Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
        double f1 = -0.1d;
        if (this.getPassengers().size() > 1) {
            f1 = this.getPassengers().indexOf(entity) == 0 ? 0.2d : -0.6d;
            if (entity instanceof Animal) {
                f1 += 0.2d;
            }
        }
        final Vec3 forward = this.getLookAngle().scale(f1 + Mth.sin((float) Math.toRadians(this.getXRot())) * 0.7D);
        return new Vec3(forward.x, getPassengersRidingOffsetY(entityDimensions, f) + forward.y, forward.z);
    }

    @Override
    public void positionRider(final Entity passenger, MoveFunction moveFunction) {
        super.positionRider(passenger, moveFunction);
        if (this.hasPassenger(passenger)) {
            passenger.setYBodyRot(this.getYRot());
            final float f2 = Mth.wrapDegrees(passenger.getYRot() - this.getYRot());
            final float f1 = Mth.clamp(f2, -105.0F, 105.0F);
            passenger.yRotO += f1 - f2;
            passenger.setYRot(passenger.getYRot() + (f1 - f2));
            passenger.setYHeadRot(passenger.getYRot());
            if (passenger instanceof Animal && this.getPassengers().size() > 1) {
                final int j = passenger.getId() % 2 == 0 ? 90 : 270;
                passenger.setYBodyRot(((Animal) passenger).yBodyRot + j);
                passenger.setYHeadRot(passenger.getYHeadRot() + j);
            }
        }
    }

    @Override
    public Item getCartItem() {
        return NiftyCarts.ANIMAL_CART.get(getWoodType());
    }
}