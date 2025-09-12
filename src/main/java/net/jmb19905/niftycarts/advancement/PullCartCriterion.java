package net.jmb19905.niftycarts.advancement;

import com.google.gson.JsonObject;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.entity.AbstractDrawnEntity;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class PullCartCriterion<T extends AbstractDrawnEntity> extends SimpleCriterionTrigger<PullCartCriterion<T>.TriggerInstance> {

    private final String cartName;

    public PullCartCriterion(String cartName) {
        this.cartName = cartName;
    }

    @Override
    protected @NotNull TriggerInstance createInstance(JsonObject jsonObject, ContextAwarePredicate contextAwarePredicate, DeserializationContext deserializationContext) {
        float minDist = jsonObject.getAsJsonPrimitive("distance").getAsFloat();
        int minPassengerCount = jsonObject.getAsJsonPrimitive("passengers").getAsInt();
        return new TriggerInstance(minDist, minPassengerCount, contextAwarePredicate);
    }

    public TriggerInstance create(float minDist, int minPassengerCount) {
        return new TriggerInstance(minDist, minPassengerCount, ContextAwarePredicate.ANY);
    }

    public void trigger(ServerPlayer serverPlayer, AbstractDrawnEntity entity, float dist) {
        super.trigger(serverPlayer, t -> t.matches(entity, dist));
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return new ResourceLocation(NiftyCarts.MOD_ID, "pull_cart_" + cartName);
    }

    public final class TriggerInstance extends AbstractCriterionTriggerInstance {

        private final float minDistance;
        private final int minPassengerCount;

        public TriggerInstance(float minDistance, int minPassengerCount, ContextAwarePredicate contextAwarePredicate) {
            super(getId(), contextAwarePredicate);
            this.minDistance = minDistance;
            this.minPassengerCount = minPassengerCount;
        }

        public boolean matches(AbstractDrawnEntity entity, float distance) {
            return distance >= minDistance && entity.getPassengers().size() >= this.minPassengerCount;
        }

        @Override
        public @NotNull JsonObject serializeToJson(SerializationContext serializationContext) {
            JsonObject jsonObject = super.serializeToJson(serializationContext);
            jsonObject.addProperty("distance", this.minDistance);
            jsonObject.addProperty("passengers", this.minPassengerCount);
            return jsonObject;
        }
    }

}
