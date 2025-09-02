package net.jmb19905.niftycarts.mixin;

import net.jmb19905.niftycarts.entity.util.ColliderEntity;
import net.jmb19905.niftycarts.entity.util.MultiPartEntity;
import net.jmb19905.niftycarts.entity.util.SubEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Mixin(Level.class)
public class LevelMixin {

    @Inject(method = "getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;", at = @At("RETURN"), cancellable = true)
    public void getEntities(@Nullable Entity entity, AABB aABB, Predicate<? super Entity> predicate, CallbackInfoReturnable<List<Entity>> cir) {
        if (cir.getReturnValue() == null) return;
        List<Entity> entities = cir.getReturnValue();
        cir.setReturnValue(entities.stream().<Entity>mapMulti((entity1, consumer) -> {
            if (entity1 instanceof MultiPartEntity multiPart) {
                Arrays.stream(multiPart.getSubEntities()).filter(part -> part instanceof ColliderEntity<?>).forEach(consumer);
            } else {
                consumer.accept(entity1);
            }
        }).toList());
    }

}
