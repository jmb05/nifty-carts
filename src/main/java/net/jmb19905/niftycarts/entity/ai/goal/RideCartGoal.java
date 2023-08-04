package net.jmb19905.niftycarts.entity.ai.goal;

import net.jmb19905.niftycarts.entity.AbstractDrawnEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public final class RideCartGoal extends Goal {
    private final Entity mob;

    public RideCartGoal(final Entity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return this.mob.getVehicle() instanceof AbstractDrawnEntity;
    }
}