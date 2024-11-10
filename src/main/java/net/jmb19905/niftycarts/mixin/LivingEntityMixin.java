package net.jmb19905.niftycarts.mixin;

import net.jmb19905.niftycarts.entity.AbstractDrawnEntity;
import net.jmb19905.niftycarts.util.NiftyWorld;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow public abstract void travel(Vec3 vec3);

    @Shadow
    protected abstract Vec3 getRiddenInput(Player player, Vec3 vec3);

    @Shadow
    public abstract void setSpeed(float f);

    @Shadow
    protected abstract float getRiddenSpeed(Player entity);

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;travel(Lnet/minecraft/world/phys/Vec3;)V"))
    public void travelRedirect(LivingEntity instance, Vec3 vec3) {
        Optional<AbstractDrawnEntity> drawnOpt = NiftyWorld.get(instance.level()).getDrawn(instance);
        if (drawnOpt.isPresent()) {
            var drawn = drawnOpt.get();
            if (drawn.getControllingPassenger() instanceof Player player) {
                if (!player.level().isClientSide) {
                    vec3 = new Vec3(drawn.getCoachmanXxa(), 0, drawn.getCoachmanZza());
                }
                this.travelCoachman(player, vec3);
                return;
            }
        }
        this.travel(vec3);
    }

    @Override
    public boolean isControlledByLocalInstance() {
        Optional<AbstractDrawnEntity> drawnOpt = NiftyWorld.get(this.level()).getDrawn(this);
        if (drawnOpt.isPresent()) {
            if (drawnOpt.get().getControllingPassenger() instanceof Player player) {
                return player.isLocalPlayer();
            }
        }
        return super.isControlledByLocalInstance();
    }

    @Unique
    private void travelCoachman(Player player, Vec3 vec) {
        Vec3 vec32 = this.getRiddenInput(player, vec);
        customTickRidden(player, vec32);
        if (this.isControlledByLocalInstance()) {
            this.setSpeed(this.getRiddenSpeed(player));
            this.travel(vec32);
        } else {

            // this.setDeltaMovement(Vec3.ZERO);
        }
    }

    @Unique
    protected void customTickRidden(LivingEntity coachman, Vec3 vec3) {
        LivingEntity living = (LivingEntity)(Object)(this);
        Vec2 vec2 = new Vec2(coachman.getXRot() * 0.5f, coachman.getYRot());
        this.setRot(vec2.y, vec2.x);
        living.yRotO = living.yBodyRot = living.yHeadRot = living.getYRot();
        if (this.isControlledByLocalInstance()) {
            if (living instanceof AbstractHorse horse) {
                if (vec3.z <= 0.0) {
                    horse.gallopSoundCounter = 0;
                }
                if (this.onGround()) {
                    horse.setIsJumping(false);
                }
            }
        }
    }

    @Inject(method = "tickRidden", at = @At("HEAD"))
    public void tickRidden(Player player, Vec3 vec3, CallbackInfo ci) {
    }

}
