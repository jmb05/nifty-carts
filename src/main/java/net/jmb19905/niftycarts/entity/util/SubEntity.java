package net.jmb19905.niftycarts.entity.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public abstract class SubEntity<E extends Entity & MultiPartEntity> extends Entity {

    private final E parent;
    private final EntityDimensions size;

    public SubEntity(E parent, float width, float height) {
        super(parent.getType(), parent.level());
        this.size = EntityDimensions.scalable(width, height);
        this.refreshDimensions();
        this.parent = parent;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public @Nullable ItemStack getPickResult() {
        return parent.getPickResult();
    }

    @Override
    public boolean is(Entity entity) {
        return this == entity || this.parent == entity;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull EntityDimensions getDimensions(Pose pose) {
        return this.size;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    public boolean hasCustomHitboxColor() {
        return false;
    }

    public Vector3f getHitboxColor() {
        return new Vector3f(0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {}

}
