package net.jmb19905.niftycarts.client.renderer.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.jmb19905.niftycarts.entity.AbstractDrawnEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.util.Mth;

public abstract class CartModel<T extends AbstractDrawnEntity> extends EntityModel<T> {
    protected final ModelPart body;

    protected final ModelPart wheels;
    protected final ModelPart leftWheel;
    protected final ModelPart rightWheel;
    protected final ModelPart extraLeftWheel;
    protected final ModelPart extraRightWheel;

    protected CartModel(final ModelPart root) {
        this.body = root.getChild("body");
        this.wheels = root.getChild("wheels");
        this.leftWheel = wheels.getChild("leftWheel");
        this.rightWheel = wheels.getChild("rightWheel");
        if (wheels.hasChild("extraLeftWheel")) this.extraLeftWheel = wheels.getChild("extraLeftWheel");
        else this.extraLeftWheel = null;
        if (wheels.hasChild("extraRightWheel")) this.extraRightWheel = wheels.getChild("extraRightWheel");
        else this.extraRightWheel = null;
    }

    public ModelPart getBody() {
        return this.body;
    }

    public ModelPart getWheel() {
        return this.rightWheel;
    }

    @Override
    public void renderToBuffer(final PoseStack stack, final VertexConsumer buf, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
        this.body.render(stack, buf, packedLight, packedOverlay, red, green, blue, alpha);
        this.leftWheel.render(stack, buf, packedLight, packedOverlay, red, green, blue, alpha);
        this.rightWheel.render(stack, buf, packedLight, packedOverlay, red, green, blue, alpha);
        if (this.extraLeftWheel != null) this.extraLeftWheel.render(stack, buf, packedLight, packedOverlay, red, green, blue, alpha);
        if (this.extraRightWheel != null) this.extraRightWheel.render(stack, buf, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(final T entity, final float delta, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float pitch) {
        this.body.xRot = (float) Math.toRadians(pitch);
        this.rightWheel.xRot = (float) (entity.getWheelRotation(0) + entity.getWheelRotationIncrement(0) * delta);
        this.leftWheel.xRot = (float) (entity.getWheelRotation(1) + entity.getWheelRotationIncrement(1) * delta);
        if (this.extraLeftWheel != null)  this.extraLeftWheel.xRot = (float) (entity.getWheelRotation(0) + entity.getWheelRotationIncrement(0) * delta);
        if (this.extraRightWheel != null) this.extraRightWheel.xRot = (float) (entity.getWheelRotation(1) + entity.getWheelRotationIncrement(1) * delta);
        final float time = entity.getTimeSinceHit() - delta;
        final float rot;
        if (time > 0.0F) {
            final float damage = Math.max(entity.getDamageTaken() - delta, 0.0F);
            rot = (float) Math.toRadians(Mth.sin(time) * time * damage / 40.0F * -entity.getForwardDirection());
        } else {
            rot = 0.0F;
        }
        this.rightWheel.zRot = rot;
        this.leftWheel.zRot = rot;
        if (this.extraLeftWheel != null)  this.extraLeftWheel.zRot = rot;
        if (this.extraRightWheel != null) this.extraRightWheel.zRot = rot;
    }

    public static MeshDefinition createDefinition(float rimLength, float axleLength) {
        final MeshDefinition def = new MeshDefinition();

        float angle = Mth.PI / 8f;
        float wheelRadius = (rimLength / 2f) / (Mth.sin(angle) / Mth.cos(angle));
        float f = axleLength / 2f + 2;

        final EasyMeshBuilder wheels = new EasyMeshBuilder("wheels", 0, 0);

        final EasyMeshBuilder leftWheel = new EasyMeshBuilder("leftWheel", 46, 60);
        leftWheel.setRotationPoint(f, -wheelRadius, 1.0F);
        leftWheel.addBox(-2.0F, -1.0F, -1.0F, 2, 2, 2);
        for (int i = 0; i < 8; i++) {
            final EasyMeshBuilder rim = new EasyMeshBuilder("rim_" + i, 58, 54);
            rim.addBox(-2.0F, -rimLength / 2f, wheelRadius - 1, 2, rimLength, 1);
            rim.xRot = i * (float) Math.PI / 4.0F;
            leftWheel.addChild(rim);

            final EasyMeshBuilder spoke = new EasyMeshBuilder("spoke_" + i, 54, 54);
            spoke.addBox(-1.5F, 1.0F, -0.5F, 1, wheelRadius - 2, 1);
            spoke.xRot = i * (float) Math.PI / 4.0F;
            leftWheel.addChild(spoke);
        }
        wheels.addChild(leftWheel);

        final EasyMeshBuilder rightWheel = new EasyMeshBuilder("rightWheel", 46, 60);
        rightWheel.setRotationPoint(-f, -wheelRadius, 1.0F);
        rightWheel.addBox(0.0F, -1.0F, -1.0F, 2, 2, 2);
        for (int i = 0; i < 8; i++) {
            final EasyMeshBuilder rim = new EasyMeshBuilder("rim_" + i, 58, 54);
            rim.addBox(0.0F, -rimLength / 2f, wheelRadius - 1, 2, rimLength, 1);
            rim.xRot = i * (float) Math.PI / 4.0F;
            rightWheel.addChild(rim);

            final EasyMeshBuilder spoke = new EasyMeshBuilder("spoke_" + i, 54, 54);
            spoke.addBox(0.5F, 1.0F, -0.5F, 1, wheelRadius - 2, 1);
            spoke.xRot = i * (float) Math.PI / 4.0F;
            rightWheel.addChild(spoke);
        }
        wheels.addChild(rightWheel);
        wheels.build(def.getRoot());

        return def;
    }

    public static EasyMeshBuilder createBody(int rimLength) {
        final EasyMeshBuilder body = new EasyMeshBuilder("body");
        float angle = Mth.PI / 8f;
        float wheelRadius = (rimLength / 2f) / (Mth.sin(angle) / Mth.cos(angle));
        body.setRotationPoint(0.0F, -wheelRadius, 1.0F);
        return body;
    }
}