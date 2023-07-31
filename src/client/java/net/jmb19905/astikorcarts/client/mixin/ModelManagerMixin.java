package net.jmb19905.astikorcarts.client.mixin;

import net.jmb19905.astikorcarts.AstikorCarts;
import net.jmb19905.astikorcarts.client.renderer.texture.AssembledTexture;
import net.jmb19905.astikorcarts.client.renderer.texture.AssembledTextureFactory;
import net.jmb19905.astikorcarts.client.renderer.texture.Material;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelManager.class)
public abstract class ModelManagerMixin {

    @Inject(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V"))
    private void apply(ModelManager.ReloadState reloadState, ProfilerFiller profilerFiller, CallbackInfo ci){
        new AssembledTextureFactory()
                .add(new ResourceLocation(AstikorCarts.MOD_ID, "textures/entity/animal_cart.png"), new AssembledTexture(64, 64)
                        .add(new Material(new ResourceLocation("block/oak_planks"), 16)
                                .fill(0, 0, 60, 38, Material.R0, 0, 2)
                                .fill(0, 28, 20, 33, Material.R90, 4, -2)
                                .fill(12, 30, 8, 31, Material.R270, 0, 4)
                        )
                        .add(new Material(new ResourceLocation("block/stripped_spruce_log"), 16)
                                .fill(54, 54, 10, 10, Material.R0, 0, 2)
                        )
                        .add(new Material(new ResourceLocation("block/oak_log"), 16)
                                .fill(0, 21, 60, 4, Material.R90)
                                .fill(46, 60, 8, 4, Material.R90)
                        )
                        .add(new Material(new ResourceLocation("block/stone"), 16)
                                .fill(62, 55, 2, 9)
                        )
                )
                .add(new ResourceLocation(AstikorCarts.MOD_ID, "textures/entity/plow.png"), new AssembledTexture(64, 64)
                        .add(new Material(new ResourceLocation("block/oak_planks"), 16)
                                .fill(0, 0, 64, 32, Material.R90)
                                .fill(0, 8, 42, 3, Material.R0, 0, 1)
                                .fill(0, 27, 34, 3, Material.R0, 0, 2)
                        )
                        .add(new Material(new ResourceLocation("block/stripped_spruce_log"), 16)
                                .fill(54, 54, 10, 10, Material.R0, 2, 0)
                        )
                        .add(new Material(new ResourceLocation("block/oak_log"), 16)
                                .fill(0, 0, 54, 4, Material.R90)
                                .fill(46, 60, 8, 4, Material.R90)
                        )
                        .add(new Material(new ResourceLocation("block/stone"), 16)
                                .fill(62, 55, 2, 9)
                        )
                )
                .add(new ResourceLocation(AstikorCarts.MOD_ID, "textures/entity/supply_cart.png"), new AssembledTexture(64, 64)
                        .add(new Material(new ResourceLocation("block/oak_planks"), 16)
                                .fill(0, 0, 60, 45, Material.R0, 0, 2)
                                .fill(0, 27, 60, 8, Material.R0, 0, 1)
                        )
                        .add(new Material(new ResourceLocation("block/stripped_spruce_log"), 16)
                                .fill(54, 54, 10, 10, Material.R0, 0, 2)
                        )
                        .add(new Material(new ResourceLocation("block/oak_log"), 16)
                                .fill(0, 23, 54, 4, Material.R90)
                                .fill(46, 60, 8, 4, Material.R90)
                        )
                        .add(new Material(new ResourceLocation("block/stone"), 16)
                                .fill(62, 55, 2, 9)
                        )
                        .add(new Material(new ResourceLocation("block/composter_side"), 16)
                                .fill(16, 47, 44, 5, Material.R0, -2, 1)
                                .fill(16, 54, 38, 5, Material.R0, -2, -6)
                        )
                        .add(new Material(new ResourceLocation("block/composter_top"), 16)
                                .fill(18, 45, 10, 2, Material.R0, -2, 3)
                                .fill(28, 45, 10, 2, Material.R0, 10, 3)
                                .fill(18, 52, 8, 2, Material.R0, 0, -4)
                                .fill(26, 52, 9, 2, Material.R0, 11, -4)
                        )
                        .add(new Material(new ResourceLocation("block/dirt"), 16)
                                .fill(0, 45, 16, 17)
                        )
                ).bake();
    }

}
