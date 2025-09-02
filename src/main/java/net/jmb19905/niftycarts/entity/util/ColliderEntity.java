package net.jmb19905.niftycarts.entity.util;

import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;

public class ColliderEntity<E extends Entity & MultiPartEntity> extends SubEntity<E> {

    public ColliderEntity(E parent, float width, float height) {
        super(parent, width, height);
    }

    @Override
    public boolean hasCustomHitboxColor() {
        return true;
    }

    @Override
    public Vector3f getHitboxColor() {
        return new Vector3f(1f, 0f, 0f);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return (entity.canBeCollidedWith() || entity.isPushable()) && !this.isPassengerOfSameVehicle(entity);
    }
}
