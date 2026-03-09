package net.jmb19905.niftycarts.client.renderer.entity.model;

import net.jmb19905.niftycarts.client.renderer.entity.WagonRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class WagonRoofModel extends EntityModel<@NotNull WagonRenderState> {

    private static final float Z_FIGHTING_EPSILON = 0.001f;
    private final ModelPart roof;

    public WagonRoofModel(ModelPart root) {
        super(root);
        this.roof = root.getChild("roof");
    }

    @Override
    public void setupAnim(@NotNull WagonRenderState state) {
        super.setupAnim(state);
        if (state.hasRoof) {
            this.roof.visible = true;
            switch (state.unfurled) {
                case 0 -> {
                    this.roof.getChild("roofUnfurled").visible = true;
                    this.roof.getChild("roofPartUnfurled").visible = false;
                    this.roof.getChild("roofFurled").visible = false;
                }
                case 1 -> {
                    this.roof.getChild("roofUnfurled").visible = false;
                    this.roof.getChild("roofPartUnfurled").visible = true;
                    this.roof.getChild("roofFurled").visible = false;
                }
                default -> {
                    this.roof.getChild("roofUnfurled").visible = false;
                    this.roof.getChild("roofPartUnfurled").visible = false;
                    this.roof.getChild("roofFurled").visible = true;
                }
            }
        } else {
            this.roof.visible = false;
            this.roof.getChild("roofUnfurled").visible = false;
            this.roof.getChild("roofPartUnfurled").visible = false;
            this.roof.getChild("roofFurled").visible = false;
        }
    }

    public static LayerDefinition createRoofLayer() {
        float axleLength = 35;

        final MeshDefinition def = new MeshDefinition();
        final EasyMeshBuilder roof = new EasyMeshBuilder("roof", 0, 0);
        roof.setRotationAngles(0, Mth.HALF_PI, 0);

        final EasyMeshBuilder roofTop = new EasyMeshBuilder("roofTop", 0, 0);
        roofTop.setRotationPoint(0,-25,0);
        roofTop.setRotationAngles(Mth.HALF_PI, 0, 0);
        roofTop.addBox(-32f - 2 * Z_FIGHTING_EPSILON, (-axleLength / 2.0f) + Mth.sqrt(2) * 8,Mth.sqrt(2) * 8 - 2, 24f, 35f - Mth.sqrt(2) * 16, 2);
        roofTop.addBox(8f + 2 * Z_FIGHTING_EPSILON, (-axleLength / 2.0f) + Mth.sqrt(2) * 8,Mth.sqrt(2) * 8 - 2, 24f, 35f - Mth.sqrt(2) * 16, 2);
        roofTop.addBox(-8, (-axleLength / 2.0f) + 1.0f + Mth.sqrt(112.5f), Mth.sqrt(112.5f) - 1, 16, 33f - 2 * Mth.sqrt(112.5f), 1);
        roof.addChild(roofTop);

        final EasyMeshBuilder roofUnfurled = new EasyMeshBuilder("roofUnfurled", 0, 0);
        roofUnfurled.addBox(-32f - 2 * Z_FIGHTING_EPSILON,-25,(-axleLength / 2.0f) - Z_FIGHTING_EPSILON, 24, 15, 2);
        roofUnfurled.addBox(-8,-25,(-axleLength / 2.0f) + 1f, 16, 15, 1);
        roofUnfurled.addBox(8f + 2 * Z_FIGHTING_EPSILON,-25,(-axleLength / 2.0f) - Z_FIGHTING_EPSILON, 24, 15, 2);

        roofUnfurled.addBox(-32f - 2 * Z_FIGHTING_EPSILON,-25,(axleLength / 2.0f) - 2 + Z_FIGHTING_EPSILON, 24, 15, 2);
        roofUnfurled.addBox(-8,-25,(axleLength / 2.0f) - 2f, 16, 15, 1);
        roofUnfurled.addBox(8f + 2 * Z_FIGHTING_EPSILON,-25,(axleLength / 2.0f) - 2 + Z_FIGHTING_EPSILON, 24, 15, 2);

        final EasyMeshBuilder roofBackFlaps = new EasyMeshBuilder("roofBackFlaps", 0, 0);
        roofBackFlaps.setRotationAngles(0, Mth.HALF_PI, 0);
        roofBackFlaps.addBox((-axleLength / 2.0f), -25, -33, 10, 15, 1);
        roofBackFlaps.addBox((axleLength / 2.0f) - 10, -25, -33 + Z_FIGHTING_EPSILON, 10, 15, 1);
        roofBackFlaps.addBox((-axleLength / 2.0f) + Mth.sqrt(2) * 8, -25 - Mth.sqrt(2) * 8, -33 - 2 * Z_FIGHTING_EPSILON, 35f - Mth.sqrt(2) * 16, 10, 1);
        roofUnfurled.addChild(roofBackFlaps);

        final EasyMeshBuilder roofBackFlapsAngledLeft = new EasyMeshBuilder("roofBackFlapsAngledLeft", 0, 0);
        roofBackFlapsAngledLeft.setRotationPoint((-axleLength / 2.0f), -25, 0);
        roofBackFlapsAngledLeft.setRotationAngles(0,0,Mth.HALF_PI / 2.0f);
        roofBackFlapsAngledLeft.addBox(0,-16f,-33f - Z_FIGHTING_EPSILON, 10, 16, 1);
        roofBackFlaps.addChild(roofBackFlapsAngledLeft);

        final EasyMeshBuilder roofBackFlapsAngledRight = new EasyMeshBuilder("roofBackFlapsAngledRight", 0, 0);
        roofBackFlapsAngledRight.setRotationPoint((axleLength / 2.0f), -25, 0);
        roofBackFlapsAngledRight.setRotationAngles(0,0,-Mth.HALF_PI / 2.0f);
        roofBackFlapsAngledRight.addBox(-10,-16f,-33f, 10, 16, 1);
        roofBackFlaps.addChild(roofBackFlapsAngledRight);

        final EasyMeshBuilder roofFrontFlaps = new EasyMeshBuilder("roofFrontFlaps", 0, 0);
        roofFrontFlaps.setRotationAngles(0, Mth.HALF_PI, 0);
        roofFrontFlaps.addBox((-axleLength / 2.0f), -25, 32, 10, 15, 1);
        roofFrontFlaps.addBox((axleLength / 2.0f) - 10, -25, 32 - Z_FIGHTING_EPSILON, 10, 15, 1);
        roofFrontFlaps.addBox((-axleLength / 2.0f) + Mth.sqrt(2) * 8, -25 - Mth.sqrt(2) * 8, 32 + 2 * Z_FIGHTING_EPSILON, 35f - Mth.sqrt(2) * 16, 10, 1);
        roofUnfurled.addChild(roofFrontFlaps);

        final EasyMeshBuilder roofFrontFlapsAngledLeft = new EasyMeshBuilder("roofFrontFlapsAngledLeft", 0, 0);
        roofFrontFlapsAngledLeft.setRotationPoint((-axleLength / 2.0f), -25, 0);
        roofFrontFlapsAngledLeft.setRotationAngles(0,0,Mth.HALF_PI / 2.0f);
        roofFrontFlapsAngledLeft.addBox(0,-16f,32f + Z_FIGHTING_EPSILON, 10, 16, 1);
        roofFrontFlaps.addChild(roofFrontFlapsAngledLeft);

        final EasyMeshBuilder roofFrontFlapsAngledRight = new EasyMeshBuilder("roofFrontFlapsAngledRight", 0, 0);
        roofFrontFlapsAngledRight.setRotationPoint((axleLength / 2.0f), -25, 0);
        roofFrontFlapsAngledRight.setRotationAngles(0,0,-Mth.HALF_PI / 2.0f);
        roofFrontFlapsAngledRight.addBox(-10,-16f,32f, 10, 16, 1);
        roofFrontFlaps.addChild(roofFrontFlapsAngledRight);

        final EasyMeshBuilder roofLeftSlopeMiddle = new EasyMeshBuilder("roofLeftSlopeMiddle", 0, 0);
        roofLeftSlopeMiddle.setRotationPoint(0, -25, (axleLength / 2.0f) - 1);
        roofLeftSlopeMiddle.setRotationAngles(Mth.HALF_PI / 2f, 0, 0);
        roofLeftSlopeMiddle.addBox(-8, -15, -1f, 16, 15, 1);
        roofUnfurled.addChild(roofLeftSlopeMiddle);

        final EasyMeshBuilder roofLeftSlopeOuter = new EasyMeshBuilder("roofLeftSlopeOuter", 0, 0);
        roofLeftSlopeOuter.setRotationPoint(0, -25, (axleLength / 2.0f));
        roofLeftSlopeOuter.setRotationAngles(Mth.HALF_PI / 2f, 0, 0);
        roofLeftSlopeOuter.addBox(-32, -16, -2f, 24, 16, 2);
        roofLeftSlopeOuter.addBox(8, -16, -2f, 24, 16, 2);
        roofUnfurled.addChild(roofLeftSlopeOuter);

        final EasyMeshBuilder roofRightSlopeMiddle = new EasyMeshBuilder("roofRightSlopeMiddle", 0, 0);
        roofRightSlopeMiddle.setRotationPoint(0, -25, (-axleLength / 2.0f) + 1);
        roofRightSlopeMiddle.setRotationAngles(-Mth.HALF_PI / 2f, 0, 0);
        roofRightSlopeMiddle.addBox(-8, -15, 0f, 16, 15, 1);
        roofUnfurled.addChild(roofRightSlopeMiddle);

        final EasyMeshBuilder roofRightSlopeOuter = new EasyMeshBuilder("roofRightSlopeOuter", 0, 0);
        roofRightSlopeOuter.setRotationPoint(0, -25, (-axleLength / 2.0f));
        roofRightSlopeOuter.setRotationAngles(-Mth.HALF_PI / 2f, 0, 0);
        roofRightSlopeOuter.addBox(-32, -16, 0f, 24, 16, 2);
        roofRightSlopeOuter.addBox(8, -16, 0f, 24, 16, 2);
        roofUnfurled.addChild(roofRightSlopeOuter);

        roof.addChild(roofUnfurled);

        final EasyMeshBuilder roofPartUnfurled = new EasyMeshBuilder("roofPartUnfurled", 0, 0);
        roofPartUnfurled.addChild(roofLeftSlopeMiddle);
        roofPartUnfurled.addChild(roofLeftSlopeOuter);
        roofPartUnfurled.addChild(roofRightSlopeMiddle);
        roofPartUnfurled.addChild(roofRightSlopeOuter);

        roofPartUnfurled.addBox(-32f - 2 * Z_FIGHTING_EPSILON,-25,(-axleLength / 2.0f) - Z_FIGHTING_EPSILON, 24, 7, 2);
        roofPartUnfurled.addBox(-8,-25,(-axleLength / 2.0f) + 1f, 16, 7, 1);
        roofPartUnfurled.addBox(8f + 2 * Z_FIGHTING_EPSILON,-25,(-axleLength / 2.0f) - Z_FIGHTING_EPSILON, 24, 7, 2);

        roofPartUnfurled.addBox(-32f - 2 * Z_FIGHTING_EPSILON,-20,(-axleLength / 2.0f) - Z_FIGHTING_EPSILON - 1, 24, 2, 1);
        roofPartUnfurled.addBox(-8,-20,(-axleLength / 2.0f), 16, 2, 1);
        roofPartUnfurled.addBox(8f + 2 * Z_FIGHTING_EPSILON,-20,(-axleLength / 2.0f) - Z_FIGHTING_EPSILON - 1, 24, 2, 1);

        roofPartUnfurled.addBox(-32f - 2 * Z_FIGHTING_EPSILON,-25,(axleLength / 2.0f) - 2 + Z_FIGHTING_EPSILON, 24, 7, 2);
        roofPartUnfurled.addBox(-8,-25,(axleLength / 2.0f) - 2f, 16, 7, 1);
        roofPartUnfurled.addBox(8f + 2 * Z_FIGHTING_EPSILON,-25,(axleLength / 2.0f) - 2 + Z_FIGHTING_EPSILON, 24, 7, 2);

        roofPartUnfurled.addBox(-32f - 2 * Z_FIGHTING_EPSILON,-20,(axleLength / 2.0f) + Z_FIGHTING_EPSILON, 24, 2, 1);
        roofPartUnfurled.addBox(-8,-20,(axleLength / 2.0f) - 1f, 16, 2, 1);
        roofPartUnfurled.addBox(8f + 2 * Z_FIGHTING_EPSILON,-20,(axleLength / 2.0f) + Z_FIGHTING_EPSILON, 24, 2, 1);

        final EasyMeshBuilder roofBackFlapsPartUnfurled = new EasyMeshBuilder("roofBackFlapsPartUnfurled", 0, 0);
        roofBackFlapsPartUnfurled.setRotationAngles(0, Mth.HALF_PI, 0);
        roofBackFlapsPartUnfurled.addBox((-axleLength / 2.0f), -25, -33, 10, 7, 1);
        roofBackFlapsPartUnfurled.addBox((axleLength / 2.0f) - 10, -25, -33 + Z_FIGHTING_EPSILON, 10, 7, 1);
        roofBackFlapsPartUnfurled.addBox((-axleLength / 2.0f), -20, -34, 10, 2, 1);
        roofBackFlapsPartUnfurled.addBox((axleLength / 2.0f) - 10, -20, -34 + Z_FIGHTING_EPSILON, 10, 2, 1);
        roofBackFlapsPartUnfurled.addBox((-axleLength / 2.0f) + Mth.sqrt(2) * 8, -25 - Mth.sqrt(2) * 8, -33 - 2 * Z_FIGHTING_EPSILON, 35f - Mth.sqrt(2) * 16, 10, 1);
        roofPartUnfurled.addChild(roofBackFlapsPartUnfurled);

        roofBackFlapsPartUnfurled.addChild(roofBackFlapsAngledLeft);
        roofBackFlapsPartUnfurled.addChild(roofBackFlapsAngledRight);

        final EasyMeshBuilder roofFrontFlapsPartUnfurled = new EasyMeshBuilder("roofFrontFlapsPartUnfurled", 0, 0);
        roofFrontFlapsPartUnfurled.setRotationAngles(0, Mth.HALF_PI, 0);
        roofFrontFlapsPartUnfurled.addBox((-axleLength / 2.0f), -25, 32, 10, 7, 1);
        roofFrontFlapsPartUnfurled.addBox((axleLength / 2.0f) - 10, -25, 32 - Z_FIGHTING_EPSILON, 10, 7, 1);
        roofFrontFlapsPartUnfurled.addBox((-axleLength / 2.0f), -20, 33, 10, 2, 1);
        roofFrontFlapsPartUnfurled.addBox((axleLength / 2.0f) - 10, -20, 33 - Z_FIGHTING_EPSILON, 10, 2, 1);
        roofFrontFlapsPartUnfurled.addBox((-axleLength / 2.0f) + Mth.sqrt(2) * 8, -25 - Mth.sqrt(2) * 8, 32 + 2 * Z_FIGHTING_EPSILON, 35f - Mth.sqrt(2) * 16, 10, 1);
        roofPartUnfurled.addChild(roofFrontFlapsPartUnfurled);

        roofFrontFlapsPartUnfurled.addChild(roofFrontFlapsAngledLeft);
        roofFrontFlapsPartUnfurled.addChild(roofFrontFlapsAngledRight);

        roof.addChild(roofPartUnfurled);

        final EasyMeshBuilder furled = new EasyMeshBuilder("roofFurled", 0, 0);

        final EasyMeshBuilder roofLeftSlopeMiddleFurled = new EasyMeshBuilder("roofLeftSlopeMiddleFurled", 0, 0);
        roofLeftSlopeMiddleFurled.setRotationPoint(0, -25, (axleLength / 2.0f) - 1);
        roofLeftSlopeMiddleFurled.setRotationAngles(Mth.HALF_PI / 2f, 0, 0);
        roofLeftSlopeMiddleFurled.addBox(-8, -15, -1f, 16, 5, 1);
        roofLeftSlopeMiddleFurled.addBox(-8 ,-13, 0f, 16, 3, 1);
        furled.addChild(roofLeftSlopeMiddleFurled);

        final EasyMeshBuilder roofLeftSlopeOuterFurled = new EasyMeshBuilder("roofLeftSlopeOuterFurled", 0, 0);
        roofLeftSlopeOuterFurled.setRotationPoint(0, -25, (axleLength / 2.0f));
        roofLeftSlopeOuterFurled.setRotationAngles(Mth.HALF_PI / 2f, 0, 0);
        roofLeftSlopeOuterFurled.addBox(-32, -16, -2f, 24, 6, 2);
        roofLeftSlopeOuterFurled.addBox(8, -16, -2f, 24, 6, 2);
        roofLeftSlopeOuterFurled.addBox(-32, -14, 0f, 24, 4, 1);
        roofLeftSlopeOuterFurled.addBox(8, -14, 0f, 24, 4, 1);
        furled.addChild(roofLeftSlopeOuterFurled);

        final EasyMeshBuilder roofRightSlopeMiddleFurled = new EasyMeshBuilder("roofRightSlopeMiddleFurled", 0, 0);
        roofRightSlopeMiddleFurled.setRotationPoint(0, -25, (-axleLength / 2.0f) + 1);
        roofRightSlopeMiddleFurled.setRotationAngles(-Mth.HALF_PI / 2f, 0, 0);
        roofRightSlopeMiddleFurled.addBox(-8, -15, 0f, 16, 5, 1);
        roofRightSlopeMiddleFurled.addBox(-8, -13, -1f, 16, 3, 1);
        furled.addChild(roofRightSlopeMiddleFurled);

        final EasyMeshBuilder roofRightSlopeOuterFurled = new EasyMeshBuilder("roofRightSlopeOuterFurled", 0, 0);
        roofRightSlopeOuterFurled.setRotationPoint(0, -25, (-axleLength / 2.0f));
        roofRightSlopeOuterFurled.setRotationAngles(-Mth.HALF_PI / 2f, 0, 0);
        roofRightSlopeOuterFurled.addBox(-32, -16, 0f, 24, 6, 2);
        roofRightSlopeOuterFurled.addBox(8, -16, 0f, 24, 6, 2);
        roofRightSlopeOuterFurled.addBox(-32, -14, -1f, 24, 4, 1);
        roofRightSlopeOuterFurled.addBox(8, -14, -1f, 24, 4, 1);
        furled.addChild(roofRightSlopeOuterFurled);

        final EasyMeshBuilder roofBackFlapsFurled = new EasyMeshBuilder("roofBackFlapsFurled", 0, 0);
        roofBackFlapsFurled.setRotationAngles(0, Mth.HALF_PI, 0);
        roofBackFlapsFurled.addBox((-axleLength / 2.0f) + Mth.sqrt(2) * 8, -25 - Mth.sqrt(2) * 8, -33 - 1 * Z_FIGHTING_EPSILON, 35f - Mth.sqrt(2) * 16, 5, 1);
        roofBackFlapsFurled.addBox((-axleLength / 2.0f) - 3f + Mth.sqrt(2) * 8, -23 - Mth.sqrt(2) * 8 + Z_FIGHTING_EPSILON, -34 - 2 * Z_FIGHTING_EPSILON, 35f + 6f - Mth.sqrt(2) * 16, 3, 2);
        furled.addChild(roofBackFlapsFurled);

        final EasyMeshBuilder roofFrontFlapsFurled = new EasyMeshBuilder("roofFrontFlapsFurled", 0, 0);
        roofFrontFlapsFurled.setRotationAngles(0, Mth.HALF_PI, 0);
        roofFrontFlapsFurled.addBox((-axleLength / 2.0f) + Mth.sqrt(2) * 8, -25 - Mth.sqrt(2) * 8, 32 + 1 * Z_FIGHTING_EPSILON, 35f - Mth.sqrt(2) * 16, 5, 1);
        roofFrontFlapsFurled.addBox((-axleLength / 2.0f) - 3f + Mth.sqrt(2) * 8, -23 - Mth.sqrt(2) * 8 + Z_FIGHTING_EPSILON, 32 + 2 * Z_FIGHTING_EPSILON, 35f + 6f - Mth.sqrt(2) * 16, 3, 2);
        furled.addChild(roofFrontFlapsFurled);

        roof.addChild(furled);

        roof.build(def.getRoot());

        return LayerDefinition.create(def, 16, 16);
    }

}
