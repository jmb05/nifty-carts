package net.jmb19905.niftycarts.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.*;

public record SimpleBlockPredicate(Optional<TagKey<Block>> blockTag,
                                   Optional<HolderSet<Block>> blocks) implements Predicate<BlockState> {

    public static final Codec<SimpleBlockPredicate> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    TagKey.codec(BuiltInRegistries.BLOCK.key()).optionalFieldOf("tag").forGetter(SimpleBlockPredicate::blockTag),
                    RegistryCodecs.homogeneousList(BuiltInRegistries.BLOCK.key()).optionalFieldOf("blocks").forGetter(SimpleBlockPredicate::blocks)
            ).apply(instance, SimpleBlockPredicate::new));

    @Override
    public boolean test(BlockState block) {
        return (blockTag.isEmpty() || block.is(blockTag.get())) && (blocks.isEmpty() || block.is(blocks.get()));
    }

    public static class Builder {
        @Nullable
        private HolderSet<Block> blocks;
        @Nullable
        private TagKey<Block> tag;

        private Builder() {
        }

        public static Builder block() {
            return new Builder();
        }

        public Builder of(Block... blocks) {
            this.blocks = HolderSet.direct(Arrays.stream(blocks).map(Block::builtInRegistryHolder).toList());
            return this;
        }

        public Builder of(TagKey<Block> tag) {
            this.tag = tag;
            return this;
        }

        public SimpleBlockPredicate build() {
            return new SimpleBlockPredicate(Optional.ofNullable(this.tag), Optional.ofNullable(this.blocks));
        }

    }

}
