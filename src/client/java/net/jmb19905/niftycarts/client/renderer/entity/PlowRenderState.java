package net.jmb19905.niftycarts.client.renderer.entity;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PlowRenderState extends CartRenderState {
    public boolean plowing;
    public NonNullList<ItemStack> items;
    public Level level;
}
