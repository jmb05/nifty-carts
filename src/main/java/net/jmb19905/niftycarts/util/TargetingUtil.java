package net.jmb19905.niftycarts.util;

import net.jmb19905.niftycarts.entity.AbstractDrawnEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class TargetingUtil {

    @Nullable
    public static  <T extends Entity> T getNearestEntity(List<? extends T> list, TargetingUtil.Conditions<T> targetingConditions, @Nullable Entity entity, double d, double e, double f) {
        double g = -1.0;
        T livingEntity2 = null;
        for (T livingEntity3 : list) {
            if (!targetingConditions.test(entity, livingEntity3)) continue;
            double h = livingEntity3.distanceToSqr(d, e, f);
            if (g != -1.0 && !(h < g)) continue;
            g = h;
            livingEntity2 = livingEntity3;
        }
        return livingEntity2;
    }

    public static class Conditions<T extends Entity> {
        private static final double MIN_VISIBILITY_DISTANCE_FOR_INVISIBLE_TARGET = 2.0;
        private double range = -1.0;
        private boolean checkLineOfSight = true;
        private boolean testInvisible = true;
        @Nullable
        private Predicate<AbstractDrawnEntity> selector;

        public Conditions() {}

        public Conditions<T> copy() {
            Conditions<T> targetingConditions = new Conditions<>();
            targetingConditions.range = this.range;
            targetingConditions.checkLineOfSight = this.checkLineOfSight;
            targetingConditions.testInvisible = this.testInvisible;
            targetingConditions.selector = this.selector;
            return targetingConditions;
        }

        public Conditions<T> range(double d) {
            this.range = d;
            return this;
        }

        public Conditions<T> ignoreLineOfSight() {
            this.checkLineOfSight = false;
            return this;
        }

        public Conditions<T> ignoreInvisibilityTesting() {
            this.testInvisible = false;
            return this;
        }

        public Conditions<T> selector(Predicate<AbstractDrawnEntity> predicate) {
            this.selector = predicate;
            return this;
        }

        public boolean test(@Nullable Entity entity, T entity2) {
            if (entity == entity2) {
                return false;
            }
            if (this.selector != null && !this.selector.test((AbstractDrawnEntity) entity2)) {
                return false;
            }
            if (entity != null) {
                if (this.range > 0.0) {
                    double e = Math.max(this.range, MIN_VISIBILITY_DISTANCE_FOR_INVISIBLE_TARGET);
                    double f = entity.distanceToSqr(entity2.getX(), entity2.getY(), entity2.getZ());
                    if (f > e * e) {
                        return false;
                    }
                }
                return !this.checkLineOfSight || !(entity instanceof Mob) || ((Mob) entity).getSensing().hasLineOfSight(entity2);
            }
            return true;
        }
    }
    
}
