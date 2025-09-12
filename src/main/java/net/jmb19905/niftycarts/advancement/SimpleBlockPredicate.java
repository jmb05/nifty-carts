package net.jmb19905.niftycarts.advancement;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class SimpleBlockPredicate implements Predicate<BlockState> {

    public static final SimpleBlockPredicate ANY = new SimpleBlockPredicate(null, null);

    @Nullable
    private final TagKey<Block> blockTag;
    @Nullable
    private final Set<Block> blocks;

    public SimpleBlockPredicate(@Nullable TagKey<Block> blockTag, @Nullable Set<Block> blocks) {
        this.blockTag = blockTag;
        this.blocks = blocks;
    }

    @Override
    public boolean test(BlockState block) {
        if (blockTag == null && blocks == null) return true;
        return block.is(blockTag) || (blocks != null && blocks.contains(block.getBlock()));
    }

    public JsonElement serializeJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonObject = new JsonObject();
            if (blocks != null) {
                JsonArray array = new JsonArray();
                blocks.forEach(b -> {
                    var id = BuiltInRegistries.BLOCK.getKey(b);
                    array.add(id.toString());
                });
            }

            if (blockTag != null) {
                jsonObject.addProperty("tag", blockTag.location().toString());
            }
            return jsonObject;
        }
    }

    public static SimpleBlockPredicate deserializeJson(JsonElement json) {
        if (json.isJsonObject()) {
            JsonObject object = json.getAsJsonObject();
            TagKey<Block> tag = null;
            if (object.has("tag")) {
                String tagId = object.getAsJsonPrimitive("tag").getAsString();
                tag = TagKey.create(Registries.BLOCK, new ResourceLocation(tagId));
            }
            Set<Block> blocks = null;
            if (object.has("blocks")) {
                var array = object.getAsJsonArray("blocks");
                Set<Block> blocks2 = new HashSet<>();
                array.forEach(j -> {
                    String blockId = j.getAsString();
                    blocks2.add(BuiltInRegistries.BLOCK.get(new ResourceLocation(blockId)));
                });
                blocks = ImmutableSet.copyOf(blocks2);

            }
            if (tag == null && blocks == null) return SimpleBlockPredicate.ANY;
            return new SimpleBlockPredicate(tag, blocks);
        }
        return SimpleBlockPredicate.ANY;
    }

    public static class Builder {
        @Nullable
        private Set<Block> blocks;
        @Nullable
        private TagKey<Block> tag;

        private Builder() {}

        public static Builder block() {
            return new Builder();
        }

        public Builder of(Block... blocks) {
            this.blocks = Arrays.stream(blocks).collect(ImmutableSet.toImmutableSet());
            return this;
        }

        public Builder of(TagKey<Block> tag) {
            this.tag = tag;
            return this;
        }

        public SimpleBlockPredicate build() {
            return new SimpleBlockPredicate(this.tag, this.blocks);
        }

    }

}
