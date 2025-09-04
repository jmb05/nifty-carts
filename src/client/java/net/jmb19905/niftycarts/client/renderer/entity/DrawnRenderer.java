package net.jmb19905.niftycarts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.jmb19905.niftycarts.client.mixin.ModelPartMixin;
import net.jmb19905.niftycarts.entity.AbstractDrawnEntity;
import net.jmb19905.niftycarts.entity.ReaperCartEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public abstract class DrawnRenderer<T extends AbstractDrawnEntity, S extends CartRenderState, M extends EntityModel<S>> extends EntityRenderer<T, S> {
    protected M model;

    private final ModelPart flag;
    private final ModelPart pole;
    private final ModelPart bar;

    protected DrawnRenderer(final EntityRendererProvider.Context renderManager, final M model) {
        super(renderManager);
        this.model = model;
        ModelPart banner = renderManager.bakeLayer(ModelLayers.STANDING_BANNER);
        this.pole = banner.getChild("pole");
        this.bar = banner.getChild("bar");
        ModelPart flag = renderManager.bakeLayer(ModelLayers.STANDING_BANNER_FLAG);
        this.flag = flag.getChild("flag");
    }

    @Override
    public abstract @NotNull S createRenderState();

    @Override
    public void extractRenderState(T entity, S state, float delta) {
        super.extractRenderState(entity, state, delta);
        AbstractDrawnEntity.RenderInfo info = entity.getInfo(delta);
        state.pitch = info.getPitch();
        state.yaw = info.getYaw();
        state.wheelRotation0 = entity.getWheelRotation(0);
        state.wheelRotation1 = entity.getWheelRotation(1);
        state.wheelRotationInc0 = entity.getWheelRotationIncrement(0);
        state.wheelRotationInc1 = entity.getWheelRotationIncrement(1);
        state.timeSinceHit = entity.getTimeSinceHit();
        state.damage = entity.getDamageTaken();
        state.forward = entity.getForwardDirection();
        state.bannerColor = entity.getBannerColor();
        state.bannerPattern = entity.getBannerPattern();
        state.woodType = entity.getWoodType();
        if (entity instanceof ReaperCartEntity reaper) {
            state.folded = reaper.isFolded();
        }
    }

    public abstract ResourceLocation getTextureLocation(S state);

    @Override
    public void render(S state, PoseStack stack, MultiBufferSource source, int light) {
        stack.pushPose();
        this.setupRotation(state, stack);

        this.model.setupAnim(state);
        final VertexConsumer buf = source.getBuffer(this.model.renderType(this.getTextureLocation(state)));
        this.model.renderToBuffer(stack, buf, light, OverlayTexture.NO_OVERLAY);
        this.renderContents(state, stack, source, light);

        stack.popPose();
    }

    protected abstract void renderContents(S state, final PoseStack stack, final MultiBufferSource source, final int packedLight);

    public void setupRotation(S state, final PoseStack stack) {
        stack.mulPose(Axis.YP.rotationDegrees(180.0F - state.yaw));
        final float time = state.timeSinceHit - state.delta;
        if (time > 0.0F) {
            final double center = 1.2D;
            stack.translate(0.0D, center, 0.0D);
            final float damage = Math.max(state.damage - state.delta, 0.0F);
            final float angle = Mth.sin(time) * time * damage / 60.0F;
            stack.mulPose(Axis.ZP.rotationDegrees(angle * state.forward));
            stack.translate(0.0D, -center, 0.0D);
            stack.translate(0.0D, angle / 32.0F, 0.0D);
        }
        stack.scale(-1.0F, -1.0F, 1.0F);
    }

    protected void renderBanner(S state, final PoseStack stack, final MultiBufferSource source, final int packedLight) {
        stack.pushPose();
        stack.mulPose(Axis.YP.rotationDegrees(90.0F));
        final float scale = 2.0F / 3.0F;
        stack.scale(scale, scale, scale);
        VertexConsumer consumer = ModelBakery.BANNER_BASE.buffer(source, RenderType::entitySolid);
        this.pole.zRot = -0.3f;
        this.pole.x = 17.48f;
        this.pole.y = 12.43f;
        this.pole.render(stack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        this.bar.x = -4.0F;
        this.bar.y = 16.01F;
        this.bar.z = 0.01F;
        this.bar.render(stack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        this.flag.x = -4.0F;
        this.flag.y = -26.0F;
        this.flag.z = 1.5F;
        float k = ((float)Math.floorMod((int) ((state.x * 7 + state.y * 9 + state.z * 13) + state.ageInTicks), 100) + state.delta) / 100.0F;
        this.flag.xRot = (0.01F * Mth.cos(Mth.TWO_PI * k)) * Mth.PI;
        BannerRenderer.renderPatterns(stack, source, packedLight, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, state.bannerColor, state.bannerPattern);
        stack.popPose();
    }

    protected void attach(final ModelPart bone, final ModelPart attachment, final Consumer<PoseStack> function, final PoseStack stack) {
        stack.pushPose();
        bone.translateAndRotate(stack);
        if (bone == attachment) {
            function.accept(stack);
        } else {
            final Map<String, ModelPart> childModels;
            childModels = ((ModelPartMixin) ((Object) bone)).getChildren();
            for (final ModelPart child : childModels.values()) {
                this.attach(child, attachment, function, stack);
            }
        }
        stack.popPose();
    }
}
