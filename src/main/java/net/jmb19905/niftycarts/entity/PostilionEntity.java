package net.jmb19905.niftycarts.entity;

import net.jmb19905.niftycarts.util.NiftyWorld;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class PostilionEntity extends DummyLivingEntity {
    public PostilionEntity(EntityType<? extends LivingEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public float getMyRidingOffset(Entity entity) {
        return 0.125f;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            final LivingEntity coachman = this.getCoachman();
            if (coachman != null) {
                this.setYRot(coachman.getYRot());
                this.yRotO = this.getYRot();
                this.setXRot(coachman.getXRot() * 0.5F);
                this.zza = coachman.zza;
                this.xxa = 0.0F;
            } else {
                this.discard();
            }
        }
    }

    @Nullable
    private LivingEntity getCoachman() {
        final Entity mount = this.getVehicle();
        if (mount != null) {
            return NiftyWorld.get(this.level()).getDrawn(mount)
                    .map(AbstractDrawnEntity::getControllingPassenger).orElse(null);
        }
        return null;
    }

}
