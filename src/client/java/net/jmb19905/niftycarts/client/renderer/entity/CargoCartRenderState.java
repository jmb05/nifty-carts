package net.jmb19905.niftycarts.client.renderer.entity;

import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CargoCartRenderState extends CartRenderState {
    public NonNullList<@NotNull ItemStack> cargo;
    public NonNullList<@NotNull ItemStackRenderState> cargoStates;
    public long rngSeed;
    public Level level;
    public boolean extraWheel;
    public boolean flowerBasket;
    public ArmorStandRenderState armorRenderState;
    public ArmorStandRenderState armorRenderState2;
}
