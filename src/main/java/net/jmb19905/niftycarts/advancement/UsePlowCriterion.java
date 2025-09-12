package net.jmb19905.niftycarts.advancement;

import com.google.gson.JsonObject;
import net.jmb19905.niftycarts.NiftyCarts;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class UsePlowCriterion extends SimpleCriterionTrigger<UsePlowCriterion.TriggerInstance> {

    public static final ResourceLocation ID = new ResourceLocation(NiftyCarts.MOD_ID, "use_plow");

    @Override
    protected @NotNull TriggerInstance createInstance(JsonObject jsonObject, ContextAwarePredicate contextAwarePredicate, DeserializationContext deserializationContext) {
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
        return new TriggerInstance(itemPredicate, contextAwarePredicate);
    }

    public TriggerInstance create(ItemPredicate itemPredicate) {
        return new TriggerInstance(itemPredicate, ContextAwarePredicate.create());
    }

    public void trigger(ServerPlayer serverPlayer, ItemStack stack) {
        super.trigger(serverPlayer, ti -> ti.matches(stack));
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return ID;
    }

    public static final class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ItemPredicate item;

        public TriggerInstance(ItemPredicate item, ContextAwarePredicate contextAwarePredicate) {
            super(ID, contextAwarePredicate);
            this.item = item;
        }

        public boolean matches(ItemStack stack) {
            return item.matches(stack);
        }

        @Override
        public @NotNull JsonObject serializeToJson(SerializationContext serializationContext) {
            JsonObject jsonObject = super.serializeToJson(serializationContext);
            jsonObject.add("item", this.item.serializeToJson());
            return jsonObject;
        }
    }

}
