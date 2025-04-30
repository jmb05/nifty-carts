package net.jmb19905.niftycarts.client.renderer.entity;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CargoCartRenderState extends CartRenderState {
    public NonNullList<ItemStack> cargo;
    public NonNullList<ItemStackRenderState> cargoStates;
    public long rngSeed;
    public Level level;
}
