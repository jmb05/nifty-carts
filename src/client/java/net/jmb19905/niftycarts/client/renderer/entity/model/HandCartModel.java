package net.jmb19905.niftycarts.client.renderer.entity.model;

import net.jmb19905.niftycarts.entity.HandCartEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;

public class HandCartModel extends CartModel<HandCartEntity> {
    private final ModelPart flowerBasket;
    public HandCartModel(ModelPart root) {
        super(root);
        this.flowerBasket = root.getChild("flowerBasket");
    }

    public ModelPart getFlowerBasket() {
        return this.flowerBasket;
    }

    public static LayerDefinition createLayer() {
        final MeshDefinition def = CartModel.createDefinition(7, 20);

        final EasyMeshBuilder axis = new EasyMeshBuilder("axis", 0, 20);
        axis.addBox(-10F, -1, -1.0F, 20, 2, 2);

        final EasyMeshBuilder cartBase = new EasyMeshBuilder("cartBase", 0, 0);
        cartBase.addBox(-10.0F, -9.0F, -2.0F, 19, 18, 1);
        cartBase.xRot = (float) -Math.PI / 2.0F;
        cartBase.yRot = (float) -Math.PI / 2.0F;

        final EasyMeshBuilder boardFront = new EasyMeshBuilder("boardFront", 0, 30);
        boardFront.addBox(-10.0F, -10.0F, -11F, 20, 9, 1);

        final EasyMeshBuilder[] boardsSide = new EasyMeshBuilder[4];
        boardsSide[0] = new EasyMeshBuilder("boards_side_0", 0, 26);
        boardsSide[0].addBox(-9F, -5.0F, -2.0F, 19, 3, 1);
        boardsSide[0].setRotationPoint(-11.0F, -5.0F, -1.0F);
        boardsSide[0].yRot = (float) Math.PI / -2.0F;

        boardsSide[1] = new EasyMeshBuilder("boards_side_1", 0, 26);
        boardsSide[1].addBox(-10F, -5.0F, -2.0F, 19, 3, 1);
        boardsSide[1].setRotationPoint(11.0F, -5.0F, -1.0F);
        boardsSide[1].yRot = (float) Math.PI / 2.0F;

        boardsSide[2] = new EasyMeshBuilder("boards_side_2", 0, 26);
        boardsSide[2].addBox(-9F, -1.0F, -2.0F, 19, 3, 1);
        boardsSide[2].setRotationPoint(-11.0F, -5.0F, -1.0F);
        boardsSide[2].yRot = (float) Math.PI / -2.0F;

        boardsSide[3] = new EasyMeshBuilder("boards_side_3", 0, 26);
        boardsSide[3].addBox(-10F, -1.0F, -2.0F, 19, 3, 1);
        boardsSide[3].setRotationPoint(11.0F, -5.0F, -1.0F);
        boardsSide[3].yRot = (float) Math.PI / 2.0F;

        final EasyMeshBuilder[] boardsRear = new EasyMeshBuilder[4];
        boardsRear[0] = new EasyMeshBuilder("boards_rear_0", 42, 2);
        boardsRear[0].addBox(8.0F, -10.0F, 9F, 2, 9, 1);

        boardsRear[1] = new EasyMeshBuilder("boards_rear_1", 42, 2);
        boardsRear[1].addBox(-10.0F, -10.0F, 9F, 2, 9, 1);

        boardsRear[2] = new EasyMeshBuilder("boards_rear_2", 0, 26);
        boardsRear[2].addBox(-8f, -10.0F, 9F, 16, 3, 1);

        boardsRear[3] = new EasyMeshBuilder("boards_rear_3", 0, 26);
        boardsRear[3].addBox(-8f, -6, 9F, 16, 3, 1);

        final EasyMeshBuilder shaft = new EasyMeshBuilder("shaft", 0, 31);
        shaft.setRotationPoint(0.0F, -5.0F, -15.0F);
        shaft.yRot = (float) Math.PI / 2.0F;
        shaft.addBox(-4F, 0F, -7.0F, 20, 2, 1);
        shaft.addBox(-4F, 0F, 6.0F, 20, 2, 1);

        final EasyMeshBuilder body = CartModel.createBody(7);
        body.addChild(axis);
        body.addChild(cartBase);
        body.addChild(boardFront);
        body.addChild(boardsRear[0]);
        body.addChild(boardsRear[1]);
        body.addChild(boardsRear[2]);
        body.addChild(boardsRear[3]);
        body.addChild(boardsSide[0]);
        body.addChild(boardsSide[1]);
        body.addChild(boardsSide[2]);
        body.addChild(boardsSide[3]);
        body.addChild(shaft);
        body.build(def.getRoot());

        final EasyMeshBuilder flowerBasket = new EasyMeshBuilder("flowerBasket");
        flowerBasket.setTextureOffset(-17, 45).addBox(-8.0F, -6.0F, -10F, 16.0F, 1.0F, 17.0F);
        flowerBasket.setTextureOffset(16, 45).addBox(-9.0F, -7.0F, 7F, 18.0F, 5.0F, 2.0F);
        final EasyMeshBuilder frontSide = new EasyMeshBuilder("frontSide", 16, 45);
        frontSide.yRot = (float) Math.PI;
        frontSide.addBox(-9.0F, -7.0F, 8F, 18.0F, 5.0F, 2.0F);
        flowerBasket.addChild(frontSide);
        final EasyMeshBuilder leftSide = new EasyMeshBuilder("leftSide", 16, 52);
        leftSide.yRot = (float) Math.PI / 2.0F;
        leftSide.addBox(-7F, -7.0F, 7.0F, 15.0F, 5.0F, 2.0F);
        flowerBasket.addChild(leftSide);
        final EasyMeshBuilder rightSide = new EasyMeshBuilder("rightSide", 16, 52);
        rightSide.yRot = (float) -Math.PI / 2.0F;
        rightSide.addBox(-8F, -7.0F, 7.0F, 15.0F, 5.0F, 2.0F);
        flowerBasket.addChild(rightSide);
        flowerBasket.build(def.getRoot());

        return LayerDefinition.create(def, 64, 64);
    }

}
