package net.jmb19905.niftycarts.client.renderer.entity.model;

import net.jmb19905.niftycarts.client.renderer.entity.SeedDrillRenderState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.model.object.banner.BannerModel;
import net.minecraft.util.Mth;

public final class SeedDrillModel extends CartModel<SeedDrillRenderState> {

    public SeedDrillModel(final ModelPart root, BannerModel bannerModel, BannerFlagModel flagModel) {
        super(root, bannerModel, flagModel);
    }

    public static LayerDefinition createLayer() {
        final MeshDefinition def = CartModel.createDefinition(9, 30);

        final EasyMeshBuilder axis = new EasyMeshBuilder("axis", 0, 0);
        axis.addBox(-15f, -1.0F, -1.0F, 30, 2, 2);

        final EasyMeshBuilder shaft = new EasyMeshBuilder("shaft", 0, 8);
        shaft.zRot = -0.07F;
        shaft.addBox(0.0F, 0.0F, -8.0F, 20, 2, 1);
        shaft.addBox(0.0F, 0.0F, 7.0F, 20, 2, 1);

        final EasyMeshBuilder shaftConnector = new EasyMeshBuilder("shaftConnector", 0, 27);
        shaftConnector.zRot = -0.26F;
        shaftConnector.addBox(-16.0F, 0.0F, -8.0F, 16, 2, 1);
        shaftConnector.addBox(-16.0F, 0.0F, 7.0F, 16, 2, 1);

        final EasyMeshBuilder shafts = new EasyMeshBuilder("shafts");
        shafts.setRotationPoint(0.0F, 0.0F, -14.0F);
        shafts.yRot = (float) Math.PI / 2.0F;
        shafts.addChild(shaft);
        shafts.addChild(shaftConnector);

        final EasyMeshBuilder seedBox = new EasyMeshBuilder("seedBox", 0, 11);
        seedBox.setRotationPoint(0, 3, 1);
        seedBox.addBox(-14, 0, -6, 28, 1, 12);
        final EasyMeshBuilder seedBoxWalls = new EasyMeshBuilder("seedBoxWalls", 2, 8);
        seedBoxWalls.addBox(-15, -8, -7, 30, 9, 1);
        seedBoxWalls.addBox(-15, -8, 6, 30, 9, 1);
        final EasyMeshBuilder seedBoxSideWalls = new EasyMeshBuilder("seedBoxSideWalls", 2, 8);
        seedBoxSideWalls.setRotationPoint(0,0,0);
        seedBoxSideWalls.yRot = -Mth.PI / 2;
        seedBoxSideWalls.addBox(-6, -8, -15, 12, 9, 1);
        seedBoxSideWalls.addBox(-6, -8, 14, 12, 9, 1);
        seedBox.addChild(seedBoxWalls);
        seedBox.addChild(seedBoxSideWalls);

        final EasyMeshBuilder hoppers = new EasyMeshBuilder("hoppers", 0, 57);
        hoppers.setRotationPoint(0, 4, 4);
        hoppers.addBox(-13.5f, 0, 0, 2, 5, 2);
        hoppers.addBox(-8.5f, 0, 0, 2, 5, 2);
        hoppers.addBox(-3.5f, 0, 0, 2, 5, 2);
        hoppers.addBox(1.5f, 0, 0, 2, 5, 2);
        hoppers.addBox(6.5f, 0, 0, 2, 5, 2);
        hoppers.addBox(11.5f, 0, 0, 2, 5, 2);

        final EasyMeshBuilder hopperExtensions = new EasyMeshBuilder("hopperExtensions", 0, 57);
        hopperExtensions.setRotationPoint(0, 4.75f, 0);
        hopperExtensions.xRot = (float) Math.PI / 8.0f;
        hopperExtensions.addBox(-13.51f, 0, 0, 2, 5, 2);
        hopperExtensions.addBox(-8.51f, 0, 0, 2, 5, 2);
        hopperExtensions.addBox(-3.51f, 0, 0, 2, 5, 2);
        hopperExtensions.addBox(1.51f, 0, 0, 2, 5, 2);
        hopperExtensions.addBox(6.51f, 0, 0, 2, 5, 2);
        hopperExtensions.addBox(11.51f, 0, 0, 2, 5, 2);
        hoppers.addChild(hopperExtensions);

        final EasyMeshBuilder hopperSupport = new EasyMeshBuilder("hopperSupport", 0, 0);
        hopperSupport.setRotationPoint(0, 6, 0);
        hopperSupport.addBox(-13.5f, 0, 0, 2, 2, 6, 0.1f);
        hopperSupport.addBox(-8.5f, 0, 0, 2, 2, 6, 0.1f);
        hopperSupport.addBox(-3.5f, 0, 0, 2, 2, 6, 0.1f);
        hopperSupport.addBox(1.5f, 0, 0, 2, 2, 6, 0.1f);
        hopperSupport.addBox(6.5f, 0, 0, 2, 2, 6, 0.1f);
        hopperSupport.addBox(11.5f, 0, 0, 2, 2, 6, 0.1f);

        final EasyMeshBuilder parts = new EasyMeshBuilder("parts");
        parts.setRotationPoint(0.0F, -5.0F, -1.0F);
        parts.addChild(shafts);
        parts.addChild(seedBox);
        parts.addChild(hoppers);
        parts.addChild(hopperSupport);

        final EasyMeshBuilder body = CartModel.createBody(9);
        body.addChild(axis);
        body.addChild(parts);
        body.build(def.getRoot());

        return LayerDefinition.create(def, 64, 64);
    }
}