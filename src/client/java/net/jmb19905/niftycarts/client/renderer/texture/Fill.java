package net.jmb19905.niftycarts.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public record Fill(int x, int y, int width, int height, int[][] rot, int u, int v) {

    @SuppressWarnings("resource")
    void fill(final NativeImage image, final TextureAtlasSprite sprite, final int resolution, final int outResolution) {
        final int r = outResolution / resolution;
        final int x1 = (this.x + this.width) * resolution;
        final int y1 = (this.y + this.height) * resolution;
        final int u0 = this.u * resolution;
        final int v0 = this.v * resolution;
        for (int y = this.y * resolution; y < y1; y++) {
            for (int dy = 0; dy < r; dy++) {
                for (int x = this.x * resolution; x < x1; x++) {
                    for (int dx = 0; dx < r; dx++) {
                        final int u1 = x * this.rot[0][0] + y * this.rot[0][1] + Math.min(this.rot[0][0], this.rot[0][1]) + u0;
                        final int v1 = x * this.rot[1][0] + y * this.rot[1][1] + Math.min(this.rot[1][0], this.rot[1][1]) + v0;
                        final int rgba = sprite.contents().originalImage.getPixelRGBA(Math.floorMod(u1, sprite.contents().width()), Math.floorMod(v1, sprite.contents().height()));
                        //final int rgba = sprite.getPixelRGBA(0, Math.floorMod(u1, sprite.contents().width()), Math.floorMod(v1, sprite.contents().height()));
                        image.setPixelRGBA(r * x + dx, r * y + dy, rgba);
                    }
                }
            }
        }
    }
}
