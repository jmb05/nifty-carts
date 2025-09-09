package net.jmb19905.niftycarts.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public record PreparedMaterial(ObjectList<Fill> fills, TextureAtlasSprite sprite, int resolution) {

    void draw(final NativeImage image, final int resolution) {
        for (final Fill m : this.fills) {
            m.fill(image, this.sprite, this.resolution, resolution);
        }
    }
}
