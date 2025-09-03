package net.jmb19905.niftycarts.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.jmb19905.niftycarts.NiftyCartsConfig;
import net.jmb19905.niftycarts.entity.AbstractDrawnEntity;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(method = "renderHitbox", at = @At("HEAD"))
    private static void renderHitbox(PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity, float f, CallbackInfo ci) {
        if (NiftyCartsConfig.getClient().renderDebugBoxes.get() && entity instanceof AbstractDrawnEntity drawn) {
            Map<Vector3f, List<AABB>> map = drawn.getAdditionalColoredDebugBoxes();
            for (Vector3f color : map.keySet()) {
                for (AABB box : map.get(color)) {
                    LevelRenderer.renderLineBox(poseStack, vertexConsumer, box.move(-entity.getX(), -entity.getY(), -entity.getZ()), color.x, color.y, color.z, 1);
                }
            }
        }
    }

}
