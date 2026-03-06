package net.jmb19905.niftycarts.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.jmb19905.niftycarts.entity.AbstractDrawnEntity;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.*;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PullCartCriterion extends SimpleCriterionTrigger<PullCartCriterion.@NotNull TriggerInstance> {

    public void trigger(ServerPlayer serverPlayer, AbstractDrawnEntity entity, float dist, float fillLevel) {
        super.trigger(serverPlayer, t -> t.matches(entity, dist, fillLevel));
    }

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<EntityTypePredicate> type, Optional<Float> minDist, Optional<Integer> minPassengerCount, Optional<Float> fillLevel) implements SimpleInstance {

        private static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        EntityTypePredicate.CODEC.optionalFieldOf("type").forGetter(TriggerInstance::type),
                        Codec.FLOAT.optionalFieldOf("distance").forGetter(TriggerInstance::minDist),
                        Codec.INT.optionalFieldOf("passengers").forGetter(TriggerInstance::minPassengerCount),
                        Codec.FLOAT.optionalFieldOf("fill").forGetter(TriggerInstance::fillLevel)
                ).apply(instance, TriggerInstance::new));

        public static Criterion<@NotNull TriggerInstance> pullCart(EntityTypePredicate type) {
            return NCCriteriaTriggers.PULL_CART.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(type), Optional.empty(), Optional.empty(), Optional.empty()));
        }

        public static Criterion<@NotNull TriggerInstance> pullCartFill(EntityTypePredicate type, float fillLevel) {
            return NCCriteriaTriggers.PULL_CART.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(type), Optional.empty(), Optional.empty(), Optional.of(fillLevel)));
        }

        public static Criterion<@NotNull TriggerInstance> pullCartDist(EntityTypePredicate type, float minDist) {
            return NCCriteriaTriggers.PULL_CART.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(type), Optional.of(minDist), Optional.empty(), Optional.empty()));
        }

        public static Criterion<@NotNull TriggerInstance> pullCart(EntityTypePredicate type, float minDist, int minPassengerCount) {
            return NCCriteriaTriggers.PULL_CART.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(type), Optional.of(minDist), Optional.of(minPassengerCount), Optional.empty()));
        }

        public boolean matches(AbstractDrawnEntity entity, float distance, float fillLevel) {
            return (this.type.isEmpty() || this.type.get().matches(entity.getType()))
                    && (this.minDist.isEmpty() || distance >= this.minDist.get())
                    && (this.minPassengerCount.isEmpty() || entity.getPassengers().size() >= this.minPassengerCount.get())
                    && (this.fillLevel.isEmpty() || fillLevel >= this.fillLevel.get());
        }
    }

}