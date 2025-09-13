package net.jmb19905.niftycarts.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ReaperHarvestCriterion extends SimpleCriterionTrigger<ReaperHarvestCriterion.TriggerInstance>  {

    public void trigger(ServerPlayer serverPlayer, BlockState state) {
        super.trigger(serverPlayer, t -> t.matches(state));
    }

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<SimpleBlockPredicate> block) implements SimpleInstance {

        private static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                    EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                    SimpleBlockPredicate.CODEC.optionalFieldOf("block").forGetter(TriggerInstance::block)
                ).apply(instance, TriggerInstance::new));

        public static Criterion<TriggerInstance> reaperHarvest() {
            return NCCriteriaTriggers.REAPER_HARVEST.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty()));
        }

        public static Criterion<TriggerInstance> reaperHarvest(SimpleBlockPredicate.Builder builder) {
            return NCCriteriaTriggers.REAPER_HARVEST.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(builder.build())));
        }

        public boolean matches(BlockState state) {
            return block.isEmpty() || block.get().test(state);
        }
    }

}
