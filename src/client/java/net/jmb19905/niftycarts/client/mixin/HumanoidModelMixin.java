package net.jmb19905.niftycarts.client.mixin;

import net.jmb19905.niftycarts.entity.WagonEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin {

    @SuppressWarnings({"unchecked", "DataFlowIssue"})
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    public <T extends LivingEntity> void setupAnim(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (livingEntity.isPassenger() && livingEntity.getRootVehicle() instanceof WagonEntity) {
            ((HumanoidModel<T>)(Object)this).rightLeg.xRot = -Mth.PI * 0.5f;
            ((HumanoidModel<T>)(Object)this).rightLeg.yRot = Mth.PI / 10;
            ((HumanoidModel<T>)(Object)this).rightLeg.zRot = 0.07853982F;
            ((HumanoidModel<T>)(Object)this).leftLeg.xRot = -Mth.PI * 0.5f;
            ((HumanoidModel<T>)(Object)this).leftLeg.yRot = -Mth.PI / 10;
            ((HumanoidModel<T>)(Object)this).leftLeg.zRot = -0.07853982F;
        }
    }

}
