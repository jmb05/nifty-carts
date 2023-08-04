package net.jmb19905.niftycarts.mixin;

import net.jmb19905.niftycarts.entity.PostilionEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public class AbstractHorseMixin {

    @Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
    public void getControllingPassenger(CallbackInfoReturnable<LivingEntity> cir) {
        Entity entity = ((AbstractHorse) (Object)this).getFirstPassenger();
        if (entity instanceof PostilionEntity dummy) {
            cir.setReturnValue(dummy);
        }
    }

}
