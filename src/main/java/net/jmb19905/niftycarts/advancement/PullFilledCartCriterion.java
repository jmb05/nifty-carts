package net.jmb19905.niftycarts.advancement;

import com.google.gson.JsonObject;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.entity.AbstractDrawnInventoryEntity;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class PullFilledCartCriterion extends SimpleCriterionTrigger<PullFilledCartCriterion.TriggerInstance> {

    private static final ResourceLocation ID = new ResourceLocation(NiftyCarts.MOD_ID, "pull_filled_cart");

    @Override
    protected @NotNull TriggerInstance createInstance(JsonObject jsonObject, ContextAwarePredicate contextAwarePredicate, DeserializationContext deserializationContext) {
        int fillCount = jsonObject.getAsJsonPrimitive("fill").getAsInt();
        String entityTypeId = jsonObject.getAsJsonPrimitive("cart").getAsString();
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(entityTypeId));
        return new TriggerInstance(type, fillCount, contextAwarePredicate);
    }

    public TriggerInstance create(EntityType<?> type, float fillPercentage) {
        return new TriggerInstance(type, fillPercentage, ContextAwarePredicate.ANY);
    }

    public void trigger(ServerPlayer serverPlayer, AbstractDrawnInventoryEntity entity, float fillPercentage) {
        super.trigger(serverPlayer, t -> t.matches(entity, fillPercentage));
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return ID;
    }

    public static final class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final EntityType<?> type;
        private final float fillPercentage;

        public TriggerInstance(EntityType<?> type, float fillPercentage, ContextAwarePredicate contextAwarePredicate) {
            super(ID, contextAwarePredicate);
            this.type = type;
            this.fillPercentage = fillPercentage;
        }

        private boolean matches(AbstractDrawnInventoryEntity entity, float fillPercentage) {
            return entity.getType() == this.type && fillPercentage >= this.fillPercentage;
        }

        @Override
        public @NotNull JsonObject serializeToJson(SerializationContext serializationContext) {
            JsonObject json = super.serializeToJson(serializationContext);
            json.addProperty("cart", BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
            json.addProperty("fill", this.fillPercentage);
            return json;
        }
    }

}
