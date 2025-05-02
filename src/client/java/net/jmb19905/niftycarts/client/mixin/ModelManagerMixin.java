package net.jmb19905.niftycarts.client.mixin;

import com.google.common.collect.ImmutableMap;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.client.renderer.texture.AssembledTexture;
import net.jmb19905.niftycarts.client.renderer.texture.AssembledTextureFactory;
import net.jmb19905.niftycarts.client.renderer.texture.Material;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelManager.class)
public abstract class ModelManagerMixin {

    @Unique
    private static final ImmutableMap<WoodType, String> LOG_NAME_OVERRIDE = ImmutableMap.of(
            WoodType.CRIMSON, "stem",
            WoodType.WARPED, "stem",
            WoodType.BAMBOO, "block"
    );

    @Inject(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V"))
    private void apply(ModelManager.ReloadState reloadState, ProfilerFiller profilerFiller, CallbackInfo ci){
        AssembledTextureFactory factory = new AssembledTextureFactory();
        Material composterSide = new Material(ResourceLocation.withDefaultNamespace("block/composter_side"), 16)
                .fill(16, 47, 44, 5, Material.R0, -2, 1)
                .fill(16, 54, 38, 5, Material.R0, -2, -6);
        Material composterTop = new Material(ResourceLocation.withDefaultNamespace("block/composter_top"), 16)
                .fill(18, 45, 10, 2, Material.R0, -2, 3)
                .fill(28, 45, 10, 2, Material.R0, 10, 3)
                .fill(18, 52, 8, 2, Material.R0, 0, -4)
                .fill(26, 52, 9, 2, Material.R0, 11, -4);
        Material stone = new Material(ResourceLocation.withDefaultNamespace("block/stone"), 16)
                .fill(62, 55, 2, 9);
        Material dirt = new Material(ResourceLocation.withDefaultNamespace("block/dirt"), 16)
                .fill(0, 45, 16, 17);
        for (WoodType type : NiftyCarts.VANILLA_WOOD_TYPES) {
            String logName = LOG_NAME_OVERRIDE.getOrDefault(type, "log");
            factory.add(new AssembledTexture(NiftyCarts.resLoc("textures/entity/" + type.name() + "_animal_cart.png"),64, 64)
                .add(new Material(ResourceLocation.withDefaultNamespace("block/" + type.name() + "_planks"), 16)
                        .fill(0, 0, 60, 38, Material.R0, 0, 2)
                        .fill(0, 28, 20, 33, Material.R90, 4, -2)
                        .fill(12, 30, 8, 31, Material.R270, 0, 4)
                )
                .add(new Material(ResourceLocation.withDefaultNamespace("block/stripped_" + type.name() + "_" + logName), 16)
                        .fill(54, 54, 10, 10, Material.R0, 0, 2)
                )
                .add(new Material(ResourceLocation.withDefaultNamespace("block/" + type.name() + "_" + logName), 16)
                        .fill(0, 21, 60, 4, Material.R90)
                        .fill(46, 60, 8, 4, Material.R90)
                )
                .add(stone)
            )
                .add(new AssembledTexture(NiftyCarts.resLoc("textures/entity/" + type.name() + "_plow.png"), 64, 64)
                        .add(new Material(ResourceLocation.withDefaultNamespace("block/" + type.name() + "_planks"), 16)
                                .fill(0, 0, 64, 32, Material.R90)
                                .fill(0, 8, 42, 3, Material.R0, 0, 1)
                                .fill(0, 27, 34, 3, Material.R0, 0, 2)
                        )
                        .add(new Material(ResourceLocation.withDefaultNamespace("block/stripped_" + type.name() + "_" + logName), 16)
                                .fill(54, 54, 10, 10, Material.R0, 2, 0)
                        )
                        .add(new Material(ResourceLocation.withDefaultNamespace("block/" + type.name() + "_" + logName), 16)
                                .fill(0, 0, 54, 4, Material.R90)
                                .fill(46, 60, 8, 4, Material.R90)
                        )
                        .add(stone)
                )
                .add(new AssembledTexture(NiftyCarts.resLoc("textures/entity/" + type.name() + "_seed_drill.png"), 64, 64)
                        .add(new Material(ResourceLocation.withDefaultNamespace("block/" + type.name() + "_planks"), 16)
                                .fill(0, 0, 64, 32, Material.R90)
                                .fill(0, 8, 64, 3, Material.R0, 0, 1)
                                .fill(0, 27, 34, 3, Material.R0, 0, 2)
                        )
                        .add(new Material(ResourceLocation.withDefaultNamespace("block/stripped_" + type.name() + "_" + logName), 16)
                                .fill(54, 54, 10, 10, Material.R0, 2, 0)
                        )
                        .add(new Material(ResourceLocation.withDefaultNamespace("block/" + type.name() + "_" + logName), 16)
                                .fill(0, 0, 64, 4, Material.R90)
                                .fill(46, 60, 8, 4, Material.R90)
                        )
                        .add(new Material(ResourceLocation.withDefaultNamespace("block/stone"), 16)
                                .fill(62, 55, 2, 9)
                                .fill(0, 57, 8, 7, Material.R0)
                        )
                )
                .add(new AssembledTexture(NiftyCarts.resLoc("textures/entity/" + type.name() + "_reaper.png"), 64, 64)
                        .add(new Material(ResourceLocation.withDefaultNamespace("block/" + type.name() + "_planks"), 16)
                                .fill(0, 0, 64, 32, Material.R90)
                                .fill(0, 8, 46, 4, Material.R0, 0, 1)
                                .fill(0, 27, 34, 3, Material.R0, 0, 2)
                        )
                        .add(new Material(ResourceLocation.withDefaultNamespace("block/stripped_" + type.name() + "_" + logName), 16)
                                .fill(54, 54, 10, 10, Material.R0, 2, 0)
                        )
                        .add(new Material(ResourceLocation.withDefaultNamespace("block/" + type.name() + "_" + logName), 16)
                                .fill(0, 0, 64, 4, Material.R90)
                                .fill(32, 12, 8, 17, Material.R0)
                                .fill(46, 60, 8, 4, Material.R90)
                        )
                        .add(new Material(ResourceLocation.withDefaultNamespace("block/stone"), 16)
                                .fill(62, 55, 2, 9)
                                .fill(0, 32, 64, 16)
                        )
                )
                .add(new AssembledTexture(NiftyCarts.resLoc("textures/entity/" + type.name() + "_supply_cart.png"), 64, 64)
                        .add(new Material(ResourceLocation.withDefaultNamespace("block/" + type.name() + "_planks"), 16)
                                .fill(0, 0, 60, 45, Material.R0, 0, 2)
                                .fill(0, 27, 60, 8, Material.R0, 0, 1)
                        )
                        .add(new Material(ResourceLocation.withDefaultNamespace("block/stripped_" + type.name() + "_" + logName), 16)
                                .fill(54, 54, 10, 10, Material.R0, 0, 2)
                        )
                        .add(new Material(ResourceLocation.withDefaultNamespace("block/" + type.name() + "_" + logName), 16)
                                .fill(0, 23, 54, 4, Material.R90)
                                .fill(46, 60, 8, 4, Material.R90)
                        )
                        .add(stone)
                        .add(composterSide)
                        .add(composterTop)
                        .add(dirt)
                )
                .add(new AssembledTexture(NiftyCarts.resLoc("textures/entity/" + type.name() + "_hand_cart.png"), 64, 64)
                        .add(new Material(ResourceLocation.withDefaultNamespace("block/" + type.name() + "_planks"), 16)
                                .fill(0,0, 52, 43, Material.R0, 0, 2)
                                .fill(0, 24, 42, 4, Material.R0, 0, 1)
                        )
                        .add(new Material(ResourceLocation.withDefaultNamespace("block/stripped_" + type.name() + "_" + logName), 16)
                                .fill(54, 54, 10, 10, Material.R0, 0, 2)
                        )
                        .add(new Material(ResourceLocation.withDefaultNamespace("block/" + type.name() + "_" + logName), 16)
                                .fill(0, 20, 46, 4, Material.R90)
                                .fill(46, 60, 8, 4, Material.R90)
                        )
                        .add(stone)
                        .add(composterSide)
                        .add(composterTop)
                        .add(dirt)
            );
        }
        factory.bake();
    }

}
