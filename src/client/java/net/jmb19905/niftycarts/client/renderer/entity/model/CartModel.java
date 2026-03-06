package net.jmb19905.niftycarts.client.renderer.entity.model;

import net.jmb19905.niftycarts.client.renderer.entity.CartRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.model.object.banner.BannerModel;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public abstract class CartModel<T extends CartRenderState> extends EntityModel<@NotNull T> {
    protected final ModelPart body;
    private final BannerModel bannerModel;
    private final BannerFlagModel flagModel;
    private final ModelPart flag;
    private final ModelPart pole;
    private final ModelPart bar;
    @Nullable
    protected final ModelPart rotating;
    protected final Set<ModelPart> leftWheels = new HashSet<>();
    protected final Set<ModelPart> rightWheels = new HashSet<>();

    protected CartModel(final ModelPart root, BannerModel bannerModel, BannerFlagModel flagModel) {
        this(root, 1, bannerModel, flagModel);
    }

    protected CartModel(final ModelPart root, int axleCount, BannerModel bannerModel, BannerFlagModel flagModel) {
        super(root);
        this.body = root.getChild("body");
        this.bannerModel = bannerModel;
        this.flagModel = flagModel;
        if (root.hasChild("rotating"))
            this.rotating = root.getChild("rotating");
        else this.rotating = null;
        for (int i = 0; i < axleCount; i++) {
            this.leftWheels.add(root.getChild("leftWheel_" + i));
            this.rightWheels.add(root.getChild("rightWheel_" + i));
        }
        this.pole = bannerModel.getChildPart("pole");
        this.bar = bannerModel.getChildPart("bar");
        this.flag = flagModel.getChildPart("flag");
    }

    public ModelPart getBody() {
        return this.body;
    }

    public BannerModel getBannerModel() {
        return bannerModel;
    }

    public BannerFlagModel getFlagModel() {
        return flagModel;
    }

    @Override
    public void setupAnim(T state) {
        if (rotating == null) {
            this.body.xRot = (float) Math.toRadians(state.pitch);
        } else {
            this.rotating.xRot = (float) Math.toRadians(state.pitch);
        }
        this.rightWheels.forEach(part -> part.xRot = (float) (state.wheelRotation0 + state.wheelRotationInc0 * state.delta));
        this.leftWheels.forEach(part -> part.xRot = (float) (state.wheelRotation1 + state.wheelRotationInc1 * state.delta));
        final float time = state.timeSinceHit - state.delta;
        final float rot;
        if (time > 0.0F) {
            final float damage = Math.max(state.damage - state.delta, 0.0F) * 0.5f;
            rot = (float) Math.toRadians(Mth.sin(time) * time * damage / 40.0F * -state.forward);
        } else {
            rot = 0.0F;
        }
        this.rightWheels.forEach(part -> part.zRot = rot);
        this.leftWheels.forEach(part -> part.zRot = rot);
        this.pole.zRot = -0.3f;
        this.pole.x = 17.48f;
        this.pole.y = 12.43f;
        this.bar.x = -4.0F;
        this.bar.y = 16.01F;
        this.bar.z = 0.01F;
        this.flag.x = -4.0F;
        this.flag.y = -26.0F;
        this.flag.z = 1.5F;
        float k = ((float)Math.floorMod((int) ((state.x * 7 + state.y * 9 + state.z * 13) + state.ageInTicks), 100) + state.delta) / 100.0F;
        this.flag.xRot = (0.01F * Mth.cos(Mth.TWO_PI * k)) * Mth.PI;
    }

    protected static EasyMeshBuilder createWheel(String name, float xOffset, float yOffset, float rimLength, float axleDist, float boxOffset, int axle, int maxAxle) {
        float angle = Mth.PI / 8f;
        float wheelRadius = (rimLength / 2f) / (Mth.sin(angle) / Mth.cos(angle));
        final EasyMeshBuilder wheel = new EasyMeshBuilder(name, 46, 60);
        float d = (float) axle - ((maxAxle - 1) / 2f);
        wheel.setRotationPoint(xOffset, yOffset - wheelRadius,1.0F + axleDist * d);
        wheel.addBox(boxOffset, -1, -1, 2, 2, 2);
        for (int i = 0; i < 8; i++) {
            final EasyMeshBuilder rim = new EasyMeshBuilder("rim_" + i, 58, 64 - ((int) rimLength + 1));
            float epsilon = 0.001f * i % 2;
            rim.addBox(boxOffset + epsilon, -rimLength / 2f, wheelRadius - 1, 2, rimLength, 1);
            rim.xRot = i * (float) Math.PI / 4.0F;
            wheel.addChild(rim);

            final EasyMeshBuilder spoke = new EasyMeshBuilder("spoke_" + i, 54, 64 - Mth.ceil(wheelRadius - 1));
            spoke.addBox(0.5f + boxOffset, 1.0F, -0.5F, 1,  wheelRadius - 2, 1);
            spoke.xRot = i * (float) Math.PI / 4.0F;
            wheel.addChild(spoke);
        }
        return wheel;
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

    public static EasyMeshBuilder createBody(int rimLength) {
        final EasyMeshBuilder body = new EasyMeshBuilder("body");
        float angle = Mth.PI / 8f;
        float wheelRadius = (rimLength / 2f) / (Mth.sin(angle) / Mth.cos(angle));
        body.setRotationPoint(0.0F, -wheelRadius, 1.0F);
        return body;
    }
}