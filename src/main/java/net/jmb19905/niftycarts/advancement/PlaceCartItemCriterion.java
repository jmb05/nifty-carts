package net.jmb19905.niftycarts.advancement;

import com.google.gson.JsonObject;
import net.jmb19905.niftycarts.NiftyCarts;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlaceCartItemCriterion extends SimpleCriterionTrigger<PlaceCartItemCriterion.TriggerInstance> {

    private static final ResourceLocation ID = new ResourceLocation(NiftyCarts.MOD_ID, "place_cart");

    @Override
    protected @NotNull TriggerInstance createInstance(JsonObject jsonObject, ContextAwarePredicate contextAwarePredicate, DeserializationContext deserializationContext) {
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
        return new TriggerInstance(itemPredicate, contextAwarePredicate);
    }

    public PlaceCartItemCriterion.TriggerInstance create(ItemPredicate itemPredicate) {
        return new PlaceCartItemCriterion.TriggerInstance(itemPredicate, ContextAwarePredicate.create());
    }

    public void trigger(ServerPlayer serverPlayer, ItemStack stack) {
        super.trigger(serverPlayer, ti -> ti.matches(stack));
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return ID;
    }

    public static final class TriggerInstance extends AbstractCriterionTriggerInstance {

        private final ItemPredicate predicate;

        public TriggerInstance(ItemPredicate predicate, ContextAwarePredicate contextAwarePredicate) {
            super(ID, contextAwarePredicate);
            this.predicate = predicate;
        }

        public boolean matches(ItemStack stack) {
            return predicate.matches(stack);
        }

        @Override
        public @NotNull JsonObject serializeToJson(SerializationContext serializationContext) {
            JsonObject jsonObject = super.serializeToJson(serializationContext);
            jsonObject.add("item", this.predicate.serializeToJson());
            return jsonObject;
        }
    }

}
