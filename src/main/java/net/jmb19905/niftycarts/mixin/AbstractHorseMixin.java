package net.jmb19905.niftycarts.mixin;

import net.jmb19905.niftycarts.entity.PostilionEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends LivingEntity {

    @Shadow public abstract @Nullable LivingEntity getControllingPassenger();

    protected AbstractHorseMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    protected void customTickRidden(LivingEntity living, Vec3 vec3) {
        Vec2 vec2 = new Vec2(living.getXRot() * 0.5f, living.getYRot());
        this.setRot(vec2.y, vec2.x);
        this.yBodyRot = this.yHeadRot = this.getYRot();
        this.yRotO = this.yHeadRot;
        if (this.isControlledByLocalInstance()) {
            if (vec3.z <= 0.0) {
                ((AbstractHorse) (Object) this).gallopSoundCounter = 0;
            }
            if (this.onGround()) {
                ((AbstractHorse) (Object) this).setIsJumping(false);
            }
        }
    }

    @Override
    public void travel(Vec3 vec3) {
        var living = ((AbstractHorse) (Object) this).getControllingPassenger();
        if (living != null) {
            float f = living.xxa * 0.5f;
            float g = living.zza;
            if (g <= 0.0f) {
                g *= 0.25f;
            }
            Vec3 vec32 = new Vec3(f, 0.0, g);
            this.customTickRidden(living, vec32);
            this.setSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED));
            super.travel(vec32);
            if (!this.isControlledByLocalInstance()) {
                this.calculateEntityAnimation(false);
                this.setDeltaMovement(Vec3.ZERO);
                this.tryCheckInsideBlocks();
            }
        } else {
            super.travel(vec3);
        }
    }

    @Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
    public void getControllingPassenger(CallbackInfoReturnable<LivingEntity> cir) {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof PostilionEntity dummy) {
            cir.setReturnValue(dummy);
        }
    }

}
