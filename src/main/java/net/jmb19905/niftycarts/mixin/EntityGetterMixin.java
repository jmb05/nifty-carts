package net.jmb19905.niftycarts.mixin;

import com.google.common.collect.ImmutableList;
import net.jmb19905.niftycarts.entity.util.ColliderEntity;
import net.jmb19905.niftycarts.entity.util.MultiPartEntity;
import net.jmb19905.niftycarts.entity.util.SubEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(EntityGetter.class)
public interface EntityGetterMixin {

    @Inject(method = "getEntityCollisions", at = @At("HEAD"), cancellable = true)
    default void getEntityCollisions(@Nullable Entity entity, AABB aABB, CallbackInfoReturnable<List<VoxelShape>> cir) {
        if (aABB.getSize() < 1.0E-7) {
            cir.setReturnValue(List.of());
        } else {
            Predicate<Entity> predicate = entity == null ? EntitySelector.CAN_BE_COLLIDED_WITH : EntitySelector.NO_SPECTATORS.and(entity::canCollideWith);
            predicate = predicate.or(e -> e instanceof MultiPartEntity);
            List<Entity> list = this.getEntities(entity, aABB.inflate(1.0E-7), predicate);
            if (list.isEmpty()) {
                cir.setReturnValue(List.of());
            } else {
                ImmutableList.Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(list.size());

                for (Entity entity2 : list) {
                    if (entity2 instanceof MultiPartEntity multiPart) {
                        for (SubEntity<?> subEntity : multiPart.getSubEntities()) {
                            if (subEntity instanceof ColliderEntity<?>) {
                                builder.add(Shapes.create(subEntity.getBoundingBox()));
                            }
                        }
                    } else {
                        builder.add(Shapes.create(entity2.getBoundingBox()));
                    }
                }

                cir.setReturnValue(builder.build());
            }
        }
        if (cir.getReturnValue() == null) cir.setReturnValue(List.of());
    }

    @Shadow
    List<Entity> getEntities(@Nullable Entity entity, AABB inflate, Predicate<Entity> predicate);

}
