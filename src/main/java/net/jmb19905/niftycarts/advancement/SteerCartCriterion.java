package net.jmb19905.niftycarts.advancement;

import com.google.gson.JsonObject;
import net.jmb19905.niftycarts.NiftyCarts;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class SteerCartCriterion extends SimpleCriterionTrigger<SteerCartCriterion.TriggerInstance> {

    private static final ResourceLocation ID = new ResourceLocation(NiftyCarts.MOD_ID, "steer_cart");

    @Override
    protected @NotNull TriggerInstance createInstance(JsonObject jsonObject, ContextAwarePredicate contextAwarePredicate, DeserializationContext deserializationContext) {
        float minDist = jsonObject.getAsJsonPrimitive("distance").getAsFloat();
        return new TriggerInstance(minDist, contextAwarePredicate);
    }

    public TriggerInstance create(float minDist) {
        return new TriggerInstance(minDist, ContextAwarePredicate.ANY);
    }

    public void trigger(ServerPlayer serverPlayer, float distance) {
        super.trigger(serverPlayer, t -> t.matches(distance));
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return ID;
    }

    public static final class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final float minDist;

        public TriggerInstance(float minDist, ContextAwarePredicate contextAwarePredicate) {
            super(ID, contextAwarePredicate);
            this.minDist = minDist;
        }

        public boolean matches(float distance) {
            return distance >= this.minDist;
        }

        @Override
        public @NotNull JsonObject serializeToJson(SerializationContext serializationContext) {
            JsonObject jsonObject = super.serializeToJson(serializationContext);
            jsonObject.addProperty("distance", this.minDist);
            return jsonObject;
        }
    }

}
