package net.jmb19905.niftycarts.client.renderer.entity.model;

import net.jmb19905.niftycarts.client.renderer.entity.ReaperRenderState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.model.object.banner.BannerModel;
import net.minecraft.util.Mth;

public final class ReaperModel extends CartModel<ReaperRenderState> {

    private final ModelPart frame;
    private final ModelPart foldedFrame;

    public ReaperModel(final ModelPart root, BannerModel bannerModel, BannerFlagModel flagModel) {
        super(root, bannerModel, flagModel);
        this.frame = root.getChild("body").getChild("frame");
        this.foldedFrame = root.getChild("body").getChild("foldedFrame");
    }

    @Override
    public void setupAnim(ReaperRenderState state) {
        super.setupAnim(state);
        if (state.folded) {
            this.frame.visible = false;
            this.foldedFrame.visible = true;
        } else {
            this.frame.visible = true;
            this.foldedFrame.visible = false;
        }
    }

    private static EasyMeshBuilder createFrame() {
        final EasyMeshBuilder frame = new EasyMeshBuilder("frame", 0, 0);
        frame.addBox(14f, 5.0F, -1.0F, 26, 2, 2);
        frame.addBox(-3.99f, -10.5F, 3.0F, 44, 2, 2);

        final EasyMeshBuilder s = new EasyMeshBuilder("s", 0, 0);
        s.setRotationPoint(0.0F, -5.0F, -15.0F);
        s.yRot = (float) Math.PI / 2.0F;
        final EasyMeshBuilder t = new EasyMeshBuilder("t", 0, 0);
        t.zRot = -0.26f;
        final EasyMeshBuilder shaftConnector1 = new EasyMeshBuilder("shaftConnector1", 0, 0);
        shaftConnector1.addBox(-17.01F, 0.0F, 14.01F, 13, 2, 2);
        final EasyMeshBuilder shaftConnector2 = new EasyMeshBuilder("shaftConnector2", 0, 0);
        shaftConnector2.yRot = (float) Math.PI / 2.0F;
        shaftConnector2.addBox(-14f, 0.01f, -6, 21, 2, 2);
        t.addChild(shaftConnector1);
        t.addChild(shaftConnector2);
        s.addChild(t);
        frame.addChild(s);

        final EasyMeshBuilder axleConnectors = new EasyMeshBuilder("axleConnectors", 32, 12);
        axleConnectors.setRotationPoint(14,10,0);
        axleConnectors.xRot = -0.27f;
        axleConnectors.addBox(0,-15,0, 2, 15, 2);
        axleConnectors.addBox(23.99f,-15,0, 2, 15, 2);

        final EasyMeshBuilder blades = new EasyMeshBuilder("blades", 0, 32);
        blades.setRotationPoint(27, 11, 0);
        blades.addBox(12,-3,-6, 0.01f, 5, 8);
        blades.addBox(-12,-3,-6, 0.01f, 5, 8);
        blades.addBox(-12, 0, -1, 24, 1, 1);
        for (int i = 0; i < 12; i++) {
            int offset = (i * 2) - 11;
            blades.addBox(offset - 0.5f, 1, -4, 1, 0.01f, 3);
        }

        final EasyMeshBuilder frameParts = new EasyMeshBuilder("frameParts");
        frameParts.setRotationPoint(0.0F, -5.0F, -1.0F);
        frameParts.addChild(axleConnectors);
        frameParts.addChild(blades);
        frame.addChild(frameParts);
        return frame;
    }

    private static EasyMeshBuilder createFoldedFrame() {
        final EasyMeshBuilder frame = new EasyMeshBuilder("foldedFrame", 0, 0);
        frame.addBox(-3.99f, -10.5F, 3.0F, 18, 2, 2);
        final EasyMeshBuilder foldedFrame = new EasyMeshBuilder("foldedInFrame", 0, 0);
        foldedFrame.setRotationPoint(14.001f, -10.5f, 3f);
        foldedFrame.setRotationAngles(0, Mth.HALF_PI, 0);
        foldedFrame.addBox(-2f, 15.5F, 0.0F, 26, 2, 2);
        foldedFrame.addBox(-1.99f, 0F, 0F, 26, 2, 2);
        frame.addChild(foldedFrame);

        final EasyMeshBuilder s = new EasyMeshBuilder("s", 0, 0);
        s.setRotationPoint(0.0F, -5.0F, -15.0F);
        s.yRot = (float) Math.PI / 2.0F;
        final EasyMeshBuilder t = new EasyMeshBuilder("t", 0, 0);
        t.zRot = -0.26f;
        final EasyMeshBuilder shaftConnector1 = new EasyMeshBuilder("shaftConnector1", 0, 0);
        shaftConnector1.addBox(-20.01F, 0.0F, 14.01F, 16, 2, 2);
        final EasyMeshBuilder shaftConnector2 = new EasyMeshBuilder("shaftConnector2", 0, 0);
        shaftConnector2.yRot = (float) Math.PI / 2.0F;
        shaftConnector2.addBox(-14f, 0.01f, -6, 21, 2, 2);
        t.addChild(shaftConnector1);
        t.addChild(shaftConnector2);
        s.addChild(t);
        frame.addChild(s);

        final EasyMeshBuilder axleConnectors = new EasyMeshBuilder("axleConnectors", 32, 12);
        axleConnectors.setRotationPoint(14,10,4);
        //axleConnectors.xRot = -0.27f;
        axleConnectors.addBox(-2,-15,0, 2, 15, 2);
        axleConnectors.addBox(21.99f,-15,0, 2, 15, 2);

        final EasyMeshBuilder blades = new EasyMeshBuilder("blades", 0, 32);
        blades.setRotationPoint(27, 11, 4);
        blades.setRotationAngles(-Mth.HALF_PI, 0, 0);
        blades.addBox(10,-3,-6, 0.01f, 5, 8);
        blades.addBox(-14,-3,-6, 0.01f, 5, 8);
        blades.addBox(-14, 0, -1, 24, 1, 1);
        for (int i = 0; i < 12; i++) {
            int offset = (i * 2) - 13;
            blades.addBox(offset - 0.5f, 1, -4, 1, 0.01f, 3);
        }

        final EasyMeshBuilder frameParts = new EasyMeshBuilder("frameParts");
        frameParts.setRotationPoint(10.0F, -5.0F, 17);
        frameParts.setRotationAngles(0, Mth.HALF_PI, 0);
        frameParts.addChild(axleConnectors);
        frameParts.addChild(blades);
        frame.addChild(frameParts);
        return frame;
    }

    public static LayerDefinition createLayer() {
        final MeshDefinition def = CartModel.createDefinition(7, 20);

        final EasyMeshBuilder axle = new EasyMeshBuilder("axle", 0, 0);
        axle.addBox(-10f, -1.0F, -1.0F, 20, 2, 2);

        final EasyMeshBuilder frame = createFrame();
        final EasyMeshBuilder foldedFrame = createFoldedFrame();

        final EasyMeshBuilder seat = new EasyMeshBuilder("seat", 8, 12);
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

        final EasyMeshBuilder shafts = new EasyMeshBuilder("shafts");
        shafts.setRotationPoint(0.0F, 0.0F, -14.0F);
        shafts.yRot = (float) Math.PI / 2.0F;
        shafts.addChild(shaft);
        shafts.addChild(shaftConnector0);

        float rimLength = 4;
        float angle = Mth.PI / 8f;
        float wheelRadius = (rimLength / 2f) / (Mth.sin(angle) / Mth.cos(angle));

        final EasyMeshBuilder parts = new EasyMeshBuilder("parts");
        parts.setRotationPoint(0.0F, -5.0F, -1.0F);
        parts.addChild(shafts);
        parts.addChild(seat);

        float radius = (7 / 2f) / (Mth.sin(angle) / Mth.cos(angle));

        final EasyMeshBuilder body = CartModel.createBody(9);
        body.setRotationPoint(0, -radius, 1);
        body.addChild(axle);
        body.addChild(frame);
        body.addChild(foldedFrame);
        body.addChild(parts);
        body.build(def.getRoot());

        return LayerDefinition.create(def, 64, 64);
    }
}