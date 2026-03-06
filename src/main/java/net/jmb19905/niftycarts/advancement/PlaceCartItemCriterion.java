package net.jmb19905.niftycarts.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.jmb19905.niftycarts.item.CartItem;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PlaceCartItemCriterion extends SimpleCriterionTrigger<PlaceCartItemCriterion.@NotNull TriggerInstance> {

    public void trigger(ServerPlayer serverPlayer, ItemStack stack) {
        super.trigger(serverPlayer, ti -> ti.matches(stack));
    }

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item) implements SimpleInstance {

        private static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item)
                ).apply(instance, TriggerInstance::new));

        public static Criterion<@NotNull TriggerInstance> placeCart() {
            return NCCriteriaTriggers.PLACE_CART_ITEM.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty()));
        }

        public static Criterion<@NotNull TriggerInstance> placeCart(CartItem item) {
            return placeCart(ItemPredicate.Builder.item().of(BuiltInRegistries.ITEM, item));
        }

        public static Criterion<@NotNull TriggerInstance> placeCart(ItemPredicate.Builder builder) {
            return NCCriteriaTriggers.PLACE_CART_ITEM.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(builder.build())));
        }

        public boolean matches(ItemStack stack) {
            return item.isEmpty() || item.get().test(stack);
        }
    }

}