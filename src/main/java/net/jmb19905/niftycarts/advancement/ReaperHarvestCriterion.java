package net.jmb19905.niftycarts.advancement;

import com.google.gson.JsonObject;
import net.jmb19905.niftycarts.NiftyCarts;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ReaperHarvestCriterion extends SimpleCriterionTrigger<ReaperHarvestCriterion.TriggerInstance>  {

    private static final ResourceLocation ID = new ResourceLocation(NiftyCarts.MOD_ID, "reaper_harvest");

    @Override
    protected @NotNull TriggerInstance createInstance(JsonObject jsonObject, ContextAwarePredicate contextAwarePredicate, DeserializationContext deserializationContext) {
        SimpleBlockPredicate predicate = SimpleBlockPredicate.deserializeJson(jsonObject.get("block"));
        return new TriggerInstance(predicate, contextAwarePredicate);
    }

    public TriggerInstance create(SimpleBlockPredicate predicate) {
        return new TriggerInstance(predicate, ContextAwarePredicate.create());
    }

    public void trigger(ServerPlayer serverPlayer, BlockState state) {
        super.trigger(serverPlayer, t -> t.matches(state));
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return ID;
    }

    public static final class TriggerInstance extends AbstractCriterionTriggerInstance {

        private final SimpleBlockPredicate predicate;

        public TriggerInstance(SimpleBlockPredicate predicate, ContextAwarePredicate contextAwarePredicate) {
            super(ID, contextAwarePredicate);
            this.predicate = predicate;
        }

        public boolean matches(BlockState state) {
            return predicate.test(state);
        }

        @Override
        public @NotNull JsonObject serializeToJson(SerializationContext serializationContext) {
            JsonObject jsonObject = super.serializeToJson(serializationContext);
            jsonObject.add("block", this.predicate.serializeJson());
            return jsonObject;
        }
    }

}
