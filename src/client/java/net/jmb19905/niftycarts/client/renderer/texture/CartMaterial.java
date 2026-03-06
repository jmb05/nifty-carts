package net.jmb19905.niftycarts.client.renderer.texture;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.Identifier;

@SuppressWarnings("unused")
public class CartMaterial {
    public static final int[][] R0 = {{1, 0}, {0, 1}};

    public static final int[][] R90 = {{0, -1}, {1, 0}};

    public static final int[][] R180 = {{-1, 0}, {-1, 0}};

    public static final int[][] R270 = {{0, 1}, {-1, 0}};

    private final Material sprite;

    private final int size;

    private final ObjectList<Fill> fills = new ObjectArrayList<>();

    public CartMaterial(Identifier sprite, final int size) {
        this(new Material(TextureAtlas.LOCATION_BLOCKS, sprite), size);
    }

    public CartMaterial(final Material sprite, final int size) {
        this.sprite = sprite;
        this.size = size;
    }

    public CartMaterial fill(final int x, final int y, final int width, final int height) {
        return this.fill(x, y, width, height, R0);
    }

    public CartMaterial fill(final int x, final int y, final int width, final int height, final int[][] rot) {
        return this.fill(x, y, width, height, rot, 0, 0);
    }

    public CartMaterial fill(final int x, final int y, final int width, final int height, final int[][] rot, final int u, final int v) {
        this.fills.add(new Fill(x, y, width, height, rot, u, v));
        return this;
    }

    PreparedMaterial prepare(final AtlasManager sprites) {
        final TextureAtlasSprite sprite = sprites.get(this.sprite);
        return new PreparedMaterial(this.fills, sprite, sprite.contents().width() / this.size);
    }
}
