package net.jmb19905.niftycarts.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class UsePlowCriterion extends SimpleCriterionTrigger<UsePlowCriterion.@NotNull TriggerInstance> {

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer serverPlayer, ItemStack stack) {
        super.trigger(serverPlayer, ti -> ti.matches(stack));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item) implements SimpleInstance {

        private static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item)
                ).apply(instance, TriggerInstance::new));

        public static Criterion<@NotNull TriggerInstance> usePlow() {
            return NCCriteriaTriggers.USE_PLOW.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty()));
        }

        public static Criterion<@NotNull TriggerInstance> usePlow(ItemPredicate.Builder builder) {
            return NCCriteriaTriggers.USE_PLOW.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(builder.build())));
        }

        public boolean matches(ItemStack stack) {
            return item.isEmpty() || item.get().test(stack);
        }
    }

}