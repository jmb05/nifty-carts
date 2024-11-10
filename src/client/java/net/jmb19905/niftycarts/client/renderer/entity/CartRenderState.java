package net.jmb19905.niftycarts.client.renderer.entity;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.properties.WoodType;

public class CartRenderState extends EntityRenderState {
    public float pitch;
    public float yaw;
    public double wheelRotation0;
    public double wheelRotationInc0;
    public double wheelRotation1;
    public double wheelRotationInc1;
    public float timeSinceHit;
    public float delta;
    public float damage;
    public int forward;
    public DyeColor bannerColor;
    public BannerPatternLayers bannerPattern;
    public WoodType woodType;
}
