package net.jmb19905.niftycarts.client.mixin;

import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.client.NiftyCartsClient;
import net.jmb19905.niftycarts.client.renderer.texture.AssembledTexture;
import net.jmb19905.niftycarts.client.renderer.texture.AssembledTextureFactory;
import net.jmb19905.niftycarts.client.renderer.texture.CartMaterial;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelManager.class)
public abstract class ModelManagerMixin {

    @Inject(method = "apply", at = @At(value = "TAIL"))
    private void apply(ModelManager.ReloadState reloadState, CallbackInfo ci){
        AssembledTextureFactory factory = new AssembledTextureFactory();
        CartMaterial composterSide = new CartMaterial(Identifier.withDefaultNamespace("block/composter_side"), 16)
                .fill(16, 47, 44, 5, CartMaterial.R0, -2, 1)
                .fill(16, 54, 38, 5, CartMaterial.R0, -2, -6);
        CartMaterial composterTop = new CartMaterial(Identifier.withDefaultNamespace("block/composter_top"), 16)
                .fill(18, 45, 10, 2, CartMaterial.R0, -2, 3)
                .fill(28, 45, 10, 2, CartMaterial.R0, 10, 3)
                .fill(18, 52, 8, 2, CartMaterial.R0, 0, -4)
                .fill(26, 52, 9, 2, CartMaterial.R0, 11, -4);
        CartMaterial stone = new CartMaterial(Identifier.withDefaultNamespace("block/stone"), 16)
                .fill(62, 55, 2, 9);
        CartMaterial dirt = new CartMaterial(Identifier.withDefaultNamespace("block/dirt"), 16)
                .fill(0, 45, 16, 17);
        for (WoodType type : NiftyCarts.VANILLA_WOOD_TYPES) {
            String logName = NiftyCartsClient.LOG_NAME_OVERRIDE.getOrDefault(type, "log");
            factory.add(new AssembledTexture(NiftyCarts.resLoc("textures/entity/" + type.name() + "_animal_cart.png"),64, 64)
                .add(new CartMaterial(Identifier.withDefaultNamespace("block/" + type.name() + "_planks"), 16)
                        .fill(0, 0, 60, 38, CartMaterial.R0, 0, 2)
                        .fill(0, 28, 20, 33, CartMaterial.R90, 4, -2)
                        .fill(12, 30, 8, 31, CartMaterial.R270, 0, 4)
                )
                .add(new CartMaterial(Identifier.withDefaultNamespace("block/stripped_" + type.name() + "_" + logName), 16)
                        .fill(54, 54, 10, 10, CartMaterial.R0, 0, 2)
                )
                .add(new CartMaterial(Identifier.withDefaultNamespace("block/" + type.name() + "_" + logName), 16)
                        .fill(0, 21, 60, 4, CartMaterial.R90)
                        .fill(46, 60, 8, 4, CartMaterial.R90)
                )
                .add(stone)
            )
                .add(new AssembledTexture(NiftyCarts.resLoc("textures/entity/" + type.name() + "_plow.png"), 64, 64)
                        .add(new CartMaterial(Identifier.withDefaultNamespace("block/" + type.name() + "_planks"), 16)
                                .fill(0, 0, 64, 32, CartMaterial.R90)
                                .fill(0, 8, 42, 3, CartMaterial.R0, 0, 1)
                                .fill(0, 27, 34, 3, CartMaterial.R0, 0, 2)
                        )
                        .add(new CartMaterial(Identifier.withDefaultNamespace("block/stripped_" + type.name() + "_" + logName), 16)
                                .fill(54, 54, 10, 10, CartMaterial.R0, 2, 0)
                        )
                        .add(new CartMaterial(Identifier.withDefaultNamespace("block/" + type.name() + "_" + logName), 16)
                                .fill(0, 0, 54, 4, CartMaterial.R90)
                                .fill(46, 60, 8, 4, CartMaterial.R90)
                        )
                        .add(stone)
                )
                .add(new AssembledTexture(NiftyCarts.resLoc("textures/entity/" + type.name() + "_wagon.png"), 64, 64)
                        .add(new CartMaterial(Identifier.withDefaultNamespace("block/" + type.name() + "_planks"), 16)
                                .fill(0, 0, 64, 48)
                        )
                        .add(new CartMaterial(Identifier.withDefaultNamespace("block/stripped_" + type.name() + "_" + logName), 16)
                                .fill(54, 53, 10, 11, CartMaterial.R0, 0, 2)
                                .fill(0, 32, 13, 19, CartMaterial.R0, 1, 0)
                        )
                        .add(new CartMaterial(Identifier.withDefaultNamespace("block/" + type.name() + "_" + logName), 16)
                                .fill(0, 60, 54, 4, CartMaterial.R90)
                        )
                        .add(new CartMaterial(Identifier.withDefaultNamespace("block/stone"), 16)
                                .fill(62, 54, 2, 10)
                        )
                )
                .add(new AssembledTexture(NiftyCarts.resLoc("textures/entity/" + type.name() + "_seed_drill.png"), 64, 64)
                        .add(new CartMaterial(Identifier.withDefaultNamespace("block/" + type.name() + "_planks"), 16)
                                .fill(0, 0, 64, 32, CartMaterial.R90)
                                .fill(0, 8, 64, 3, CartMaterial.R0, 0, 1)
                                .fill(0, 27, 34, 3, CartMaterial.R0, 0, 2)
                        )
                        .add(new CartMaterial(Identifier.withDefaultNamespace("block/stripped_" + type.name() + "_" + logName), 16)
                                .fill(54, 54, 10, 10, CartMaterial.R0, 2, 0)
                        )
                        .add(new CartMaterial(Identifier.withDefaultNamespace("block/" + type.name() + "_" + logName), 16)
                                .fill(0, 0, 64, 4, CartMaterial.R90)
                                .fill(46, 60, 8, 4, CartMaterial.R90)
                        )
                        .add(new CartMaterial(Identifier.withDefaultNamespace("block/stone"), 16)
                                .fill(62, 55, 2, 9)
                                .fill(0, 57, 8, 7, CartMaterial.R0)
                        )
                )
                .add(new AssembledTexture(NiftyCarts.resLoc("textures/entity/" + type.name() + "_reaper.png"), 64, 64)
                        .add(new CartMaterial(Identifier.withDefaultNamespace("block/" + type.name() + "_planks"), 16)
                                .fill(0, 0, 64, 32, CartMaterial.R90)
                                .fill(0, 8, 46, 4, CartMaterial.R0, 0, 1)
                                .fill(0, 27, 34, 3, CartMaterial.R0, 0, 2)
                        )
                        .add(new CartMaterial(Identifier.withDefaultNamespace("block/stripped_" + type.name() + "_" + logName), 16)
                                .fill(54, 54, 10, 10, CartMaterial.R0, 2, 0)
                        )
                        .add(new CartMaterial(Identifier.withDefaultNamespace("block/" + type.name() + "_" + logName), 16)
                                .fill(0, 0, 64, 4, CartMaterial.R90)
                                .fill(32, 12, 8, 17, CartMaterial.R0)
                                .fill(46, 60, 8, 4, CartMaterial.R90)
                        )
                        .add(new CartMaterial(Identifier.withDefaultNamespace("block/stone"), 16)
                                .fill(62, 55, 2, 9)
                                .fill(0, 32, 64, 16)
                        )
                )
                .add(new AssembledTexture(NiftyCarts.resLoc("textures/entity/" + type.name() + "_supply_cart.png"), 64, 64)
                        .add(new CartMaterial(Identifier.withDefaultNamespace("block/" + type.name() + "_planks"), 16)
                                .fill(0, 0, 60, 45, CartMaterial.R0, 0, 2)
                                .fill(0, 27, 60, 8, CartMaterial.R0, 0, 1)
                        )
                        .add(new CartMaterial(Identifier.withDefaultNamespace("block/stripped_" + type.name() + "_" + logName), 16)
                                .fill(54, 54, 10, 10, CartMaterial.R0, 0, 2)
                        )
                        .add(new CartMaterial(Identifier.withDefaultNamespace("block/" + type.name() + "_" + logName), 16)
                                .fill(0, 23, 54, 4, CartMaterial.R90)
                                .fill(46, 60, 8, 4, CartMaterial.R90)
                        )
                        .add(stone)
                        .add(composterSide)
                        .add(composterTop)
                        .add(dirt)
                )
                .add(new AssembledTexture(NiftyCarts.resLoc("textures/entity/" + type.name() + "_hand_cart.png"), 64, 64)
                        .add(new CartMaterial(Identifier.withDefaultNamespace("block/" + type.name() + "_planks"), 16)
                                .fill(0,0, 52, 43, CartMaterial.R0, 0, 2)
                                .fill(0, 24, 42, 4, CartMaterial.R0, 0, 1)
                        )
                        .add(new CartMaterial(Identifier.withDefaultNamespace("block/stripped_" + type.name() + "_" + logName), 16)
                                .fill(54, 54, 10, 10, CartMaterial.R0, 0, 2)
                        )
                        .add(new CartMaterial(Identifier.withDefaultNamespace("block/" + type.name() + "_" + logName), 16)
                                .fill(0, 20, 46, 4, CartMaterial.R90)
                                .fill(46, 60, 8, 4, CartMaterial.R90)
                        )
                        .add(stone)
                        .add(composterSide)
                        .add(composterTop)
                        .add(dirt)
            );
            for (DyeColor color : DyeColor.values()) {
                factory.add(new AssembledTexture(NiftyCarts.resLoc("textures/entity/wagon_roof_" + color.getName() + ".png"), 16, 16)
                        .add(new CartMaterial(Identifier.withDefaultNamespace("block/" + color.getName() + "_wool"), 16)
                                .fill(0, 0, 16, 16)));
            }
        }
        factory.bake();
    }

}
