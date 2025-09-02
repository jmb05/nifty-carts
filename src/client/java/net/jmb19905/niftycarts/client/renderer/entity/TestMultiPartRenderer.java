package net.jmb19905.niftycarts.client.renderer.entity;

import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.entity.util.TestMultiPartEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TestMultiPartRenderer extends EntityRenderer<TestMultiPartEntity> {

    public TestMultiPartRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(TestMultiPartEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NiftyCarts.MOD_ID, "test_multi_part");
    }
}
