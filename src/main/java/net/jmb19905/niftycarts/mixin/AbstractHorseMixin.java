package net.jmb19905.niftycarts.mixin;

import net.jmb19905.niftycarts.entity.AbstractDrawnEntity;
import net.jmb19905.niftycarts.entity.PostilionEntity;
import net.jmb19905.niftycarts.util.NiftyWorld;
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
    protected void customTickRidden(Vec2 rot, Vec3 vec3) {
        this.setRot(rot.y, rot.x);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
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
    public void travel(Vec3 travelVec) {
        /*Optional<AbstractDrawnEntity> drawnOpt = NiftyWorld.get(this.level()).getDrawn(this);
        if (drawnOpt.isPresent()) {
            AbstractDrawnEntity drawn = drawnOpt.get();
            if (drawn.hasControllingPassenger()) {
                float xxa = drawn.getCoachmanXxa() * 0.5f;
                float zza = drawn.getCoachmanZza();
                if (zza <= 0.0f) {
                    zza *= 0.25f;
                }
                Vec3 vec32 = new Vec3(xxa, 0.0, zza);
                this.customTickRidden(new Vec2(drawn.getCoachmanXRot() * 0.5f, drawn.getCoachmanYRot()), vec32);
                this.setSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                super.travel(vec32);
                if (!this.isControlledByLocalInstance()) {
                    this.calculateEntityAnimation(false);
                    this.setDeltaMovement(Vec3.ZERO);
                    //this.tryCheckInsideBlocks();TODO: where did this method go?
                }
                return;
            }
        }*/
        super.travel(travelVec);
    }

    @Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
    public void getControllingPassenger(CallbackInfoReturnable<LivingEntity> cir) {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof PostilionEntity dummy) {
            cir.setReturnValue(dummy);
        }
    }

}
