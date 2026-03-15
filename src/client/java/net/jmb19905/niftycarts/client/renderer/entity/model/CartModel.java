package net.jmb19905.niftycarts.client.renderer.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.jmb19905.niftycarts.entity.AbstractDrawnEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public abstract class CartModel<T extends AbstractDrawnEntity> extends EntityModel<T> {

    protected final ModelPart body;
    @Nullable
    protected final ModelPart rotating;
    protected final Set<ModelPart> leftWheels = new HashSet<>();
    protected final Set<ModelPart> rightWheels = new HashSet<>();

    protected CartModel(final ModelPart root) {
        this(root, 1);
    }

    protected CartModel(final ModelPart root, int axleCount) {
        this.body = root.getChild("body");
        if (root.hasChild("rotating"))
            this.rotating = root.getChild("rotating");
        else this.rotating = null;
        for (int i = 0; i < axleCount; i++) {
            this.leftWheels.add(root.getChild("leftWheel_" + i));
            this.rightWheels.add(root.getChild("rightWheel_" + i));
        }
    }

    public ModelPart getBody() {
        return this.body;
    }

    public ModelPart getWheel() {
        return this.rightWheels.stream().findAny().orElse(null);
    }

    @Override
    public void renderToBuffer(final PoseStack stack, final VertexConsumer buf, final int packedLight, final int packedOverlay, int k) {
        this.body.render(stack, buf, packedLight, packedOverlay, k);
        if (rotating != null) rotating.render(stack, buf, packedLight, packedOverlay, k);
        leftWheels.forEach(part -> part.render(stack, buf, packedLight, packedOverlay, k));
        rightWheels.forEach(part -> part.render(stack, buf, packedLight, packedOverlay, k));
    }

    @Override
    public void setupAnim(final T entity, final float delta, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float pitch) {
        if (rotating == null) {
            this.body.xRot = (float) Math.toRadians(pitch);
        } else {
            this.rotating.xRot = (float) Math.toRadians(pitch);
        }
        rightWheels.forEach(part -> part.xRot = (float) (entity.getWheelRotation(0) + entity.getWheelRotationIncrement(0) * delta));
        leftWheels.forEach(part -> part.xRot = (float) (entity.getWheelRotation(1) + entity.getWheelRotationIncrement(1) * delta));
        final float time = entity.getTimeSinceHit() - delta;
        final float rot;
        if (time > 0.0F) {
            final float damage = Math.max(entity.getDamageTaken() - delta, 0.0F) * 0.5f;
            rot = (float) Math.toRadians(Mth.sin(time) * time * damage / 40.0F * -entity.getForwardDirection());
        } else {
            rot = 0.0F;
        }
        rightWheels.forEach(part -> part.zRot = rot);
        leftWheels.forEach(part -> part.zRot = rot);
    }

    public static MeshDefinition createDefinition(float rimLength, float axleLength) {
        return createDefinition(rimLength, axleLength, 1, 0);
    }

    public static MeshDefinition createDefinition(float rimLength, float axleLength, int axleCount, float axleDist) {
        final MeshDefinition def = new MeshDefinition();

        float xOffset = axleLength / 2f + 2;

        for (int k = 0; k < axleCount; k++) {
            EasyMeshBuilder leftWheel = createWheel("leftWheel_" + k, xOffset, 0, rimLength, axleDist, -2, k, axleCount);
            leftWheel.build(def.getRoot());
            EasyMeshBuilder rightWheel = createWheel("rightWheel_" + k, -xOffset, 0, rimLength, axleDist, 0, k, axleCount);
            rightWheel.build(def.getRoot());
        }

        return def;
    }

    protected static EasyMeshBuilder createWheel(String name, float xOffset, float yOffset, float rimLength, float axleDist, float boxOffset, int axle, int maxAxle) {
        float angle = Mth.PI / 8f;
        float wheelRadius = (rimLength / 2f) / (Mth.sin(angle) / Mth.cos(angle));
        final EasyMeshBuilder wheel = new EasyMeshBuilder(name, 46, 60);
        float d = (float) axle - ((maxAxle - 1) / 2f);
        wheel.setRotationPoint(xOffset, yOffset - wheelRadius,1.0F + axleDist * d);
        wheel.addBox(boxOffset, -1, -1, 2, 2, 2);
        for (int i = 0; i < 8; i++) {
            final EasyMeshBuilder rim = new EasyMeshBuilder(name + "_rim_" + i, 58, 64 - ((int) rimLength + 1));
            float epsilon = 0.001f * i % 2;
            rim.addBox(boxOffset + epsilon, -rimLength / 2f, wheelRadius - 1, 2, rimLength, 1);
            rim.xRot = i * (float) Math.PI / 4.0F;
            wheel.addChild(rim);

            final EasyMeshBuilder spoke = new EasyMeshBuilder(name + "_spoke_" + i, 54, 64 - Mth.ceil(wheelRadius - 1));
            spoke.addBox(0.5f + boxOffset, 1.0F, -0.5F, 1,  wheelRadius - 2, 1);
            spoke.xRot = i * (float) Math.PI / 4.0F;
            wheel.addChild(spoke);
        }
        return wheel;
    }

    public static EasyMeshBuilder createBody(int rimLength) {
        final EasyMeshBuilder body = new EasyMeshBuilder("body");
        float angle = Mth.PI / 8f;
        float wheelRadius = (rimLength / 2f) / (Mth.sin(angle) / Mth.cos(angle));
        body.setRotationPoint(0.0F, -wheelRadius, 1.0F);
        return body;
    }
}