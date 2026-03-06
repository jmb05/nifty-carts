package net.jmb19905.niftycarts.client.renderer.entity;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class PlowRenderState extends CartRenderState {
    public boolean plowing;
    public NonNullList<@NotNull ItemStack> items;
    public NonNullList<@NotNull ItemStackRenderState> itemStates;
    public Level level;
}
