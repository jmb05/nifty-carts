package net.jmb19905.niftycarts.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.jmb19905.niftycarts.entity.util.MultiPartEntity;
import net.jmb19905.niftycarts.entity.util.SubEntity;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "renderHitbox", at = @At("HEAD"))
    private static void renderHitbox(PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity, float delta, float g, float h, float i, CallbackInfo ci) {
        if (entity instanceof MultiPartEntity multiPartEntity) {
            double parentX = -Mth.lerp(delta, entity.xOld, entity.getX());
            double parentY = -Mth.lerp(delta, entity.yOld, entity.getY());
            double parentZ = -Mth.lerp(delta, entity.zOld, entity.getZ());
            for (SubEntity<?> part : multiPartEntity.getSubEntities()) {
                poseStack.pushPose();
                double x = parentX + Mth.lerp(delta, part.xOld, part.getX());
                double y = parentY + Mth.lerp(delta, part.yOld, part.getY());
                double z = parentZ + Mth.lerp(delta, part.zOld, part.getZ());
                poseStack.translate(x, y, z);
                Vector3f color = new Vector3f(g, h, i);
                if (part.hasCustomHitboxColor()) color = part.getHitboxColor();
                AABB box = part.getBoundingBox().move(-part.getX(), -part.getY(), -part.getZ());
                LevelRenderer.renderLineBox(poseStack, vertexConsumer, box, color.x, color.y, color.z, 1);
                poseStack.popPose();
            }
        }
    }
}
