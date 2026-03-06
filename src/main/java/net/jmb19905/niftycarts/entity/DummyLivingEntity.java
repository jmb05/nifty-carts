package net.jmb19905.niftycarts.entity;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class DummyLivingEntity extends LivingEntity {

    protected DummyLivingEntity(final EntityType<? extends @NotNull LivingEntity> type, final Level world) {
        super(type, world);
        this.setSilent(true);
        this.setNoGravity(true);
        this.setInvulnerable(true);
        this.setInvisible(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
    }

    @Override
    public @NotNull ItemStack getItemBySlot(final @NotNull EquipmentSlot slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(final @NotNull EquipmentSlot slotIn, final @NotNull ItemStack stack) {
    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public boolean ignoreExplosion(@NotNull Explosion explosion) {
        return true;
    }

    @Override
    public @NotNull PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    /*@Override
    public boolean canBreatheUnderwater() {
        return true;
    }*/

    @Override
    public boolean isEffectiveAi() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void rideTick() {
        super.rideTick();
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean attackable() {
        return false;
    }

    @Override
    public boolean canAttackType(final @NotNull EntityType<?> type) {
        return false;
    }

    @Override
    public boolean canAttack(final @NotNull LivingEntity living) {
        return false;
    }

    @Override
    public boolean isAffectedByPotions() {
        return false;
    }

    @Override
    public void kill(@NotNull ServerLevel serverLevel) {
        this.discard();
    }

    @Override
    public void push(@NotNull Entity p_21294_) {
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    protected void doWaterSplashEffect() {
    }

    @Override
    public boolean addEffect(final @NotNull MobEffectInstance effect, @Nullable Entity entity) {
        return false;
    }

    @Override
    protected void updateEffectVisibility() {
        super.updateEffectVisibility();
    }

    @Override
    protected void updateInvisibilityStatus() {
        this.setInvisible(true);
    }
}
