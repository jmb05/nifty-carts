package net.jmb19905.niftycarts.client.renderer.entity.model;

import net.jmb19905.niftycarts.client.renderer.entity.WagonRenderState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.model.object.banner.BannerModel;
import net.minecraft.util.Mth;

public class WagonModel extends CartModel<WagonRenderState> {

    private static final float Z_FIGHTING_EPSILON = 0.001f;

    private final WagonRoofModel roofModel;
    private final WagonChestModel chestModel;

    public WagonModel(ModelPart root, WagonRoofModel roofModel, WagonChestModel chestModel, BannerModel bannerModel, BannerFlagModel flagModel) {
        super(root, 2, bannerModel, flagModel);
        this.roofModel = roofModel;
        this.chestModel = chestModel;
    }

    public WagonChestModel getChestModel() {
        return chestModel;
    }

    public WagonRoofModel getRoofModel() {
        return roofModel;
    }

    @Override
    public void setupAnim(WagonRenderState state) {
        super.setupAnim(state);
        roofModel.setupAnim(state);
        chestModel.setupAnim(state);
    }

    public static LayerDefinition createLayer() {
        int rimLength = 10;
        float axleLength = 35;
        float axleDist = 40;
        final MeshDefinition def = CartModel.createDefinition(rimLength, axleLength, 2, axleDist);

        final EasyMeshBuilder axle0 = new EasyMeshBuilder("axle_0", 0, 60);
        axle0.addBox(-axleLength / 2.0f, -1.0F, -1.0F - (axleDist / 2f), Mth.ceil(axleLength / 2.0f), 2, 2);
        axle0.addBox((-axleLength / 2.0f) + Mth.ceil(axleLength / 2.0f), -1.0F, -1.0F - (axleDist / 2f), Mth.floor(axleLength / 2.0f), 2, 2);

        final EasyMeshBuilder axle1 = new EasyMeshBuilder("axle_1", 0, 60);
        axle1.addBox(-axleLength / 2.0f, -1.0F, -1.0F + (axleDist / 2f), Mth.ceil(axleLength / 2.0f), 2, 2);
        axle1.addBox((-axleLength / 2.0f) + Mth.ceil(axleLength / 2.0f), -1.0F, -1.0F + (axleDist / 2f), Mth.floor(axleLength / 2.0f), 2, 2);

        final EasyMeshBuilder base = new EasyMeshBuilder("base", 0, 0);
        base.addBox((-axleLength / 2.0f) + 2, -2.0f, -1.0f - (axleDist / 2f) - 10, (float) Math.ceil((axleLength - 4.0f) / 2.0f), 1, ((axleDist + 22) / 2) - 1);
        base.addBox((float) ((-axleLength / 2.0f) + 2 + Math.ceil((axleLength - 4.0f) / 2.0f)), -2.0f, -1.0f - (axleDist / 2f) - 10, (float) Math.floor((axleLength - 4) / 2.0f), 1, ((axleDist + 22) / 2) - 1);
        base.addBox((-axleLength / 2.0f) + 2, -2.0f, -1, (float) Math.ceil((axleLength - 4.0f) / 2.0f), 1, ((axleDist + 22) / 2) + 1);
        base.addBox((float) ((-axleLength / 2.0f) + 2 + Math.ceil((axleLength - 4.0f) / 2.0f)), -2.0f, -1, (float) Math.floor((axleLength - 4) / 2.0f), 1, ((axleDist + 22) / 2) + 1);

        final EasyMeshBuilder walls = new EasyMeshBuilder("walls", 0, 0);

        final EasyMeshBuilder rightWalls = new EasyMeshBuilder("rightWalls", 0, 2);
        rightWalls.setRotationAngles(0, Mth.HALF_PI, 0);

        rightWalls.addBox(-32,-10,(axleLength / 2.0f) - 4, 16, 8, 2);
        rightWalls.addBox(-16,-10,(axleLength / 2.0f) - 4, 16, 8, 2);
        rightWalls.addBox(0,-10,(axleLength / 2.0f) - 4, 16, 8, 2);
        rightWalls.addBox(16,-10,(axleLength / 2.0f) - 4, 16, 8, 2);

        final EasyMeshBuilder leftWalls = new EasyMeshBuilder("leftWalls", 0, 2);
        leftWalls.setRotationAngles(0, -Mth.HALF_PI, 0);

        leftWalls.addBox(-32,-10,(axleLength / 2.0f) - 4, 16, 8, 2);
        leftWalls.addBox(-16,-10,(axleLength / 2.0f) - 4, 16, 8, 2);
        leftWalls.addBox(0,-10,(axleLength / 2.0f) - 4, 16, 8, 2);
        leftWalls.addBox(16,-10,(axleLength / 2.0f) - 4, 16, 8, 2);

        final EasyMeshBuilder wallBeams = new EasyMeshBuilder("wallBeams", 0, 3);
        wallBeams.setRotationAngles(0, Mth.HALF_PI, 0);

        wallBeams.addBox(-32, -10, (-axleLength / 2.0f) + 1, 4, 8, 1);
        wallBeams.addBox(-12, -10, (-axleLength / 2.0f) + 1, 4, 8, 1);
        wallBeams.addBox(8, -10, (-axleLength / 2.0f) + 1, 4, 8, 1);
        wallBeams.addBox(28, -10, (-axleLength / 2.0f) + 1, 4, 8, 1);

        wallBeams.addBox(-32, -10, (axleLength / 2.0f) - 2, 4, 8, 1);
        wallBeams.addBox(-12, -10, (axleLength / 2.0f) - 2, 4, 8, 1);
        wallBeams.addBox(8, -10, (axleLength / 2.0f) - 2, 4, 8, 1);
        wallBeams.addBox(28, -10, (axleLength / 2.0f) - 2, 4, 8, 1);

        walls.addChild(wallBeams);
        walls.addChild(leftWalls);
        walls.addChild(rightWalls);

        final EasyMeshBuilder beams = new EasyMeshBuilder("beams", 0, 32);

        beams.addBox((-axleLength / 2.0f) + 2, -25, -32f - 2 * Z_FIGHTING_EPSILON, 1, 15, 4);
        beams.addBox((-axleLength / 2.0f) + 2, -25, -12 + 2 * Z_FIGHTING_EPSILON, 1, 15, 4);
        beams.addBox((-axleLength / 2.0f) + 2, -25, 8 - 2 * Z_FIGHTING_EPSILON, 1, 15, 4);
        beams.addBox((-axleLength / 2.0f) + 2, -25, 28 + 2 * Z_FIGHTING_EPSILON, 1, 15, 4);

        beams.addBox((axleLength / 2.0f) - 3, -25, -32f - 2 * Z_FIGHTING_EPSILON, 1, 15, 4);
        beams.addBox((axleLength / 2.0f) - 3, -25, -12 + 2 * Z_FIGHTING_EPSILON, 1, 15, 4);
        beams.addBox((axleLength / 2.0f) - 3, -25, 8 - 2 * Z_FIGHTING_EPSILON, 1, 15, 4);
        beams.addBox((axleLength / 2.0f) - 3, -25, 28 + 2 * Z_FIGHTING_EPSILON, 1, 15, 4);

        walls.addChild(beams);

        final EasyMeshBuilder backAndFrontWalls = new EasyMeshBuilder("backAndFront", 0, 3);

        backAndFrontWalls.addBox((-axleLength / 2.0f) + 4, -10, -32, axleLength - 8, 8, 1);
        backAndFrontWalls.addBox((-axleLength / 2.0f) + 4, -10, 31, 8, 8, 1);
        backAndFrontWalls.addBox((axleLength / 2.0f) - 12, -10, 31, 8, 8, 1);

        walls.addChild(backAndFrontWalls);

        final EasyMeshBuilder rotating = new EasyMeshBuilder("rotating", 0, 0);
        rotating.setRotationPoint(0, -13, -20);
        rotating.addBox(-8.5f, 0, -35, 1, 2, 36);
        rotating.addBox(7.5f, 0, -35, 1, 2, 36);
        rotating.build(def.getRoot());

        final EasyMeshBuilder wallLeft = new EasyMeshBuilder("beamsAngledLeft", 0, 32);
        wallLeft.setRotationPoint((axleLength / 2.0f) - 2, -25, 0);
        wallLeft.setRotationAngles(0, 0, -Mth.HALF_PI / 2f);
        wallLeft.addBox(-1, -14, -32f - Z_FIGHTING_EPSILON, 1, 14, 4);
        wallLeft.addBox(-1, -14, -12f + Z_FIGHTING_EPSILON, 1, 14, 4);
        wallLeft.addBox(-1, -14, 8f - Z_FIGHTING_EPSILON, 1, 14, 4);
        wallLeft.addBox(-1, -14, 28f + Z_FIGHTING_EPSILON, 1, 14, 4);
        walls.addChild(wallLeft);

        final EasyMeshBuilder wallRight = new EasyMeshBuilder("beamsAngledRight", 0, 32);
        wallRight.setRotationPoint((-axleLength / 2.0f) + 2, -25, 0);
        wallRight.setRotationAngles(0, 0, Mth.HALF_PI / 2f);
        wallRight.addBox(0, -14, -32f - Z_FIGHTING_EPSILON, 1, 14, 4);
        wallRight.addBox(0, -14, -12f + Z_FIGHTING_EPSILON, 1, 14, 4);
        wallRight.addBox(0, -14, 8f - Z_FIGHTING_EPSILON, 1, 14, 4);
        wallRight.addBox(0, -14, 28f + Z_FIGHTING_EPSILON, 1, 14, 4);
        walls.addChild(wallRight);

        final EasyMeshBuilder wallTop = new EasyMeshBuilder("beamsTop", 3, 32);
        wallTop.setRotationPoint(0, -25, 0);
        wallTop.setRotationAngles(Mth.HALF_PI, Mth.HALF_PI, 0);
        wallTop.addBox(-32, (-axleLength / 2.0f) + 2 + Mth.sqrt(2) * 7, (float) (Math.sqrt(2.0f) * 7.0f - 1.0f), 4, 31f - Mth.sqrt(2) * 14, 1);
        wallTop.addBox(-12, (-axleLength / 2.0f) + 2 + Mth.sqrt(2) * 7, (float) (Math.sqrt(2.0f) * 7.0f - 1.0f), 4, 31f - Mth.sqrt(2) * 14, 1);
        wallTop.addBox(8, (-axleLength / 2.0f) + 2 + Mth.sqrt(2) * 7, (float) (Math.sqrt(2.0f) * 7.0f - 1.0f), 4, 31f - Mth.sqrt(2) * 14, 1);
        wallTop.addBox(28, (-axleLength / 2.0f) + 2 + Mth.sqrt(2) * 7, (float) (Math.sqrt(2.0f) * 7.0f - 1.0f), 4, 31f - Mth.sqrt(2) * 14, 1);
        walls.addChild(wallTop);

        final EasyMeshBuilder body = CartModel.createBody(rimLength);
        body.addChild(axle0);
        body.addChild(axle1);
        body.addChild(base);
        body.addChild(walls);
        body.build(def.getRoot());

        return LayerDefinition.create(def, 64, 64);
    }

    public static LayerDefinition createChestLayer() {
        MeshDefinition def = new MeshDefinition();

        EasyMeshBuilder backChest = new EasyMeshBuilder("chest0", 0, 0);
        backChest.setRotationPoint(0, 0, -31);
        createChest(backChest);
        backChest.build(def.getRoot());

        EasyMeshBuilder middleChest = new EasyMeshBuilder("chest1", 0, 0);
        middleChest.setRotationPoint(0, 0, -15);
        createChest(middleChest);
        middleChest.build(def.getRoot());

        EasyMeshBuilder frontChest = new EasyMeshBuilder("chest2", 0, 0);
        frontChest.setRotationPoint(0, 0, 1);
        createChest(frontChest);
        frontChest.build(def.getRoot());

        return LayerDefinition.create(def, 128, 64);
    }

    private static void createChest(EasyMeshBuilder root) {
        EasyMeshBuilder chestBottom = new EasyMeshBuilder("chestBottom", 0, 19);
        chestBottom.setRotationPoint(13, -2, 0);
        chestBottom.setRotationAngles(Mth.PI, Mth.PI, 0);
        chestBottom.addBox(0,0,0, 26, 10, 14);
        root.addChild(chestBottom);

        EasyMeshBuilder chestLid = new EasyMeshBuilder("chestLid", 0, 0);
        chestLid.setRotationPoint(13, -11, 0);
        chestLid.setRotationAngles(Mth.PI, Mth.PI, 0);
        chestLid.addBox(0,0,0, 26, 5, 14);
        root.addChild(chestLid);

        EasyMeshBuilder chestLock = new EasyMeshBuilder("chestLock", 0, 0);
        chestLock.setRotationPoint(14, 3, 14);
        chestLock.setRotationAngles(Mth.PI, Mth.PI, 0);
        chestLock.addBox(0,0,0, 2, 4, 1);
        chestLid.addChild(chestLock);
    }

}