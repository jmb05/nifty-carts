package net.jmb19905.niftycarts.client.renderer.entity.model;

import net.jmb19905.niftycarts.entity.ReaperCartEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public final class ReaperModel extends CartModel<ReaperCartEntity> {

    public ReaperModel(final ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(final ReaperCartEntity entity, final float delta, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float pitch) {
        super.setupAnim(entity, delta, limbSwingAmount, ageInTicks, netHeadYaw, pitch);
    }

    public static LayerDefinition createLayer() {
        final MeshDefinition def = CartModel.createDefinition(7, 20);

        final EasyMeshBuilder axis = new EasyMeshBuilder("axis", 0, 0);
        axis.addBox(-10f, -1.0F, -1.0F, 20, 2, 2);
        axis.addBox(14f, 5.0F, -1.0F, 26, 2, 2);
        axis.addBox(-3.99f, -10.5F, 3.0F, 44, 2, 2);

        final EasyMeshBuilder axisConnectors = new EasyMeshBuilder("axisConnectors", 32, 12);
        axisConnectors.setRotationPoint(14,10,0);
        axisConnectors.xRot = -0.27f;
        axisConnectors.addBox(0,-15,0, 2, 15, 2);
        axisConnectors.addBox(23.99f,-15,0, 2, 15, 2);

        final EasyMeshBuilder seat = new EasyMeshBuilder("seat", 0, 12);
        seat.addBox(-4,-6,1, 8, 2, 8);

        final EasyMeshBuilder seatConnectors = new EasyMeshBuilder("seatConnectors", 0, 12);
        seatConnectors.setRotationPoint(0,0,3);
        seatConnectors.xRot = -Mth.PI / 8;
        seatConnectors.addBox(1f,-5,-0.5f,2, 10, 2);
        seatConnectors.addBox(-3f,-5,-0.5f,2, 10, 2);
        seat.addChild(seatConnectors);

        final EasyMeshBuilder shaft = new EasyMeshBuilder("shaft", 8, 8);
        shaft.zRot = -0.07F;
        shaft.addBox(0.0F, 0.0F, -8.01F, 17, 2, 1);
        shaft.addBox(0.0F, 0.0F, 7.01F, 17, 2, 1);

        final EasyMeshBuilder shaftConnector0 = new EasyMeshBuilder("shaftConnector0", 0, 27);
        shaftConnector0.zRot = -0.26F;
        shaftConnector0.addBox(-16.0F, 0.0F, -8.0F, 16, 2, 1);
        shaftConnector0.addBox(-16.0F, 0.0F, 7.0F, 16, 2, 1);
        final EasyMeshBuilder shaftConnector1 = new EasyMeshBuilder("shaftConnector1", 0, 0);
        shaftConnector1.addBox(-17.01F, 0.0F, 14.01F, 13, 2, 2);
        final EasyMeshBuilder shaftConnector2 = new EasyMeshBuilder("shaftConnector2", 0, 0);
        shaftConnector2.yRot = (float) Math.PI / 2.0F;
        shaftConnector2.addBox(-14f, 0.01f, -6, 21, 2, 2);
        shaftConnector0.addChild(shaftConnector1);
        shaftConnector0.addChild(shaftConnector2);

        final EasyMeshBuilder shafts = new EasyMeshBuilder("shafts");
        shafts.setRotationPoint(0.0F, 0.0F, -14.0F);
        shafts.yRot = (float) Math.PI / 2.0F;
        shafts.addChild(shaft);
        shafts.addChild(shaftConnector0);

        final EasyMeshBuilder blades = new EasyMeshBuilder("blades", 0, 32);
        blades.setRotationPoint(27, 0, 0);
        blades.addBox(12,8,-6, 0.01f, 5, 8);
        blades.addBox(-12,8,-6, 0.01f, 5, 8);
        blades.addBox(-12, 11, -1, 24, 1, 1);
        for (int i = 0; i < 12; i++) {
            int offset = (i * 2) - 11;
            blades.addBox(offset - 0.5f, 12, -4, 1, 0.01f, 3);
        }

        float rimLength = 4;
        float angle = Mth.PI / 8f;
        float wheelRadius = (rimLength / 2f) / (Mth.sin(angle) / Mth.cos(angle));

        final EasyMeshBuilder parts = new EasyMeshBuilder("parts");
        parts.setRotationPoint(0.0F, -5.0F, -1.0F);
        parts.addChild(shafts);
        parts.addChild(seat);
        parts.addChild(axisConnectors);
        parts.addChild(blades);

        float radius = (7 / 2f) / (Mth.sin(angle) / Mth.cos(angle));

        final EasyMeshBuilder body = CartModel.createBody(9);
        body.setRotationPoint(0, -radius, 1);
        body.addChild(axis);
        body.addChild(parts);
        body.build(def.getRoot());

        return LayerDefinition.create(def, 64, 64);
    }
}
