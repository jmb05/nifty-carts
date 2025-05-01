package net.jmb19905.niftycarts.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;

public class AssembledTexture {
    private final ResourceLocation id;
    private final int width;
    private final int height;

    private final ObjectList<Material> materials = new ObjectArrayList<>();

    public AssembledTexture(ResourceLocation id, final int width, final int height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public AssembledTexture add(final Material material) {
        this.materials.add(material);
        return this;
    }

    public ResourceLocation getId() {
        return id;
    }

    AbstractTexture assemble(final ModelManager sprites) {
        final PreparedMaterial[] prepared = new PreparedMaterial[this.materials.size()];
        int resolution = 1;
        for (final ObjectListIterator<Material> it = this.materials.iterator(); it.hasNext(); ) {
            final int i = it.nextIndex();
            final PreparedMaterial p = it.next().prepare(sprites);
            prepared[i] = p;
            resolution = Math.max(resolution, p.getResolution());
        }
        final NativeImage image = new NativeImage(this.width * resolution, this.height * resolution, true);
        for (final PreparedMaterial p : prepared) p.draw(image, resolution);
        return new DynamicTexture(id::toString, image);
    }
}
