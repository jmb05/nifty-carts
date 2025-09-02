package net.jmb19905.niftycarts.entity.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class TestMultiPartEntity extends Entity implements MultiPartEntity {

    private final SubEntity<?>[] subEntities;
    private final ColliderEntity<TestMultiPartEntity> smol;

    public TestMultiPartEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.smol = new ColliderEntity<>(this, 0.5f, 1f);
        this.subEntities = new SubEntity[]{smol};
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 lastPos = new Vec3(smol.getX(), smol.getY(), smol.getZ());
        smol.setPos(getX(), getY(), getZ());
        smol.xo = lastPos.x;
        smol.yo = lastPos.y;
        smol.zo = lastPos.z;
        smol.xOld = lastPos.x;
        smol.yOld = lastPos.y;
        smol.zOld = lastPos.z;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return (entity.canBeCollidedWith() || entity.isPushable()) && !this.isPassengerOfSameVehicle(entity);
    }

    @Override
    public void push(Entity entity) {
        //super.push(entity);
    }

    @Override
    public void push(double d, double e, double f) {
        //super.push(d, e, f);
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {}

    @Override
    public @NotNull SubEntity<?>[] getSubEntities() {
        return subEntities;
    }
}
