package net.jmb19905.niftycarts.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.world.entity.decoration.PaintingVariant;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class CargoRenderUtil {

    public static void renderPainting(final PaintingVariant painting, final PoseStack stack, final VertexConsumer buf, final int packedLight) {
        final PaintingTextureManager uploader = Minecraft.getInstance().getPaintingTextures();
        final int width = painting.getWidth();
        final int height = painting.getHeight();
        final TextureAtlasSprite art = uploader.get(painting);
        final TextureAtlasSprite back = uploader.getBackSprite();
        final Matrix4f model = stack.last().pose();
        final Matrix3f normal = stack.last().normal();
        final int blockWidth = width / 16;
        final int blockHeight = height / 16;
        final float offsetX = -blockWidth / 2.0F;
        final float offsetY = -blockHeight / 2.0F;
        final float depth = 0.5F / 16.0F;
        final float bu0 = back.getU0();
        final float bu1 = back.getU1();
        final float bv0 = back.getV0();
        final float bv1 = back.getV1();
        final float bup = back.getU(0.0625f);
        final float bvp = back.getV(0.0625f);
        final float uvX = 1.0f / blockWidth;
        final float uvY = 1.0f / blockHeight;
        for (int x = 0; x < blockWidth; ++x) {
            for (int y = 0; y < blockHeight; ++y) {
                final float x1 = offsetX + (x + 1);
                final float x0 = offsetX + x;
                final float y1 = offsetY + (y + 1);
                final float y0 = offsetY + y;
                final float u0 = art.getU(uvX * (blockWidth - x));
                final float u1 = art.getU(uvX * (blockWidth - x - 1));
                final float v0 = art.getV(uvY * (blockHeight - y));
                final float v1 = art.getV(uvY * (blockHeight - y - 1));
                vert(model, normal, buf, x1, y0, u1, v0, -depth, 0, 0, -1, packedLight);
                vert(model, normal, buf, x0, y0, u0, v0, -depth, 0, 0, -1, packedLight);
                vert(model, normal, buf, x0, y1, u0, v1, -depth, 0, 0, -1, packedLight);
                vert(model, normal, buf, x1, y1, u1, v1, -depth, 0, 0, -1, packedLight);

                vert(model, normal, buf, x1, y1, bu0, bv0, depth, 0, 0, 1, packedLight);
                vert(model, normal, buf, x0, y1, bu1, bv0, depth, 0, 0, 1, packedLight);
                vert(model, normal, buf, x0, y0, bu1, bv1, depth, 0, 0, 1, packedLight);
                vert(model, normal, buf, x1, y0, bu0, bv1, depth, 0, 0, 1, packedLight);

                vert(model, normal, buf, x1, y1, bu0, bv0, -depth, 0, 1, 0, packedLight);
                vert(model, normal, buf, x0, y1, bu1, bv0, -depth, 0, 1, 0, packedLight);
                vert(model, normal, buf, x0, y1, bu1, bvp, depth, 0, 1, 0, packedLight);
                vert(model, normal, buf, x1, y1, bu0, bvp, depth, 0, 1, 0, packedLight);

                vert(model, normal, buf, x1, y0, bu0, bv0, depth, 0, -1, 0, packedLight);
                vert(model, normal, buf, x0, y0, bu1, bv0, depth, 0, -1, 0, packedLight);
                vert(model, normal, buf, x0, y0, bu1, bvp, -depth, 0, -1, 0, packedLight);
                vert(model, normal, buf, x1, y0, bu0, bvp, -depth, 0, -1, 0, packedLight);

                vert(model, normal, buf, x1, y1, bup, bv0, depth, -1, 0, 0, packedLight);
                vert(model, normal, buf, x1, y0, bup, bv1, depth, -1, 0, 0, packedLight);
                vert(model, normal, buf, x1, y0, bu0, bv1, -depth, -1, 0, 0, packedLight);
                vert(model, normal, buf, x1, y1, bu0, bv0, -depth, -1, 0, 0, packedLight);

                vert(model, normal, buf, x0, y1, bup, bv0, -depth, 1, 0, 0, packedLight);
                vert(model, normal, buf, x0, y0, bup, bv1, -depth, 1, 0, 0, packedLight);
                vert(model, normal, buf, x0, y0, bu0, bv1, depth, 1, 0, 0, packedLight);
                vert(model, normal, buf, x0, y1, bu0, bv0, depth, 1, 0, 0, packedLight);
            }
        }
    }

    private static void vert(final Matrix4f stack, final Matrix3f normal, final VertexConsumer buf, final float x, final float y, final float u, final float v, final float z, final int nx, final int ny, final int nz, final int packedLight) {
        buf.vertex(stack, x, y, z).color(0xFF, 0xFF, 0xFF, 0xFF).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, nx, ny, nz).endVertex();
    }

}
