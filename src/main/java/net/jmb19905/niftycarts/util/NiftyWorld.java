package net.jmb19905.niftycarts.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.entity.AbstractDrawnEntity;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class NiftyWorld extends SavedData {

    public static final Codec<NiftyWorld> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(EntityWithId.CODEC
                    .listOf()
                    .optionalFieldOf("drawnList", List.of()).forGetter((world) ->
                        world.pulling.int2ObjectEntrySet()
                            .stream()
                            .map(entry ->
                                    new EntityWithId(entry.getIntKey(), entry.getValue()))
                            .toList())).apply(instance, NiftyWorld::new));
    public static final SavedDataType<@NotNull NiftyWorld> TYPE = new SavedDataType<>(
            NiftyCarts.MOD_ID + "_world",
            NiftyWorld::new,
            CODEC,
            DataFixTypes.LEVEL
    );

    private static NiftyWorld clientInstance = null;

    private final Int2ObjectMap<UUID> pulling = new Int2ObjectOpenHashMap<>();

    public NiftyWorld() {}

    private NiftyWorld(List<EntityWithId> entityWithIds) {
        entityWithIds.forEach(entityWithId ->
                pulling.put(entityWithId.id, entityWithId.entityId));
    }

    public void addPulling(final AbstractDrawnEntity drawn) {
        @Nullable final Entity pulling = drawn.getPulling();
        if (pulling != null) {
            setPulling(pulling.getId(), drawn);
        }
    }

    public void setPulling(int pullId, final AbstractDrawnEntity drawn) {
        this.pulling.put(pullId, drawn.getUUID());
        setDirty();
    }

    public Optional<AbstractDrawnEntity> getDrawn(final Entity e) {
        if (e == null) return Optional.empty();
        var drawnId = this.pulling.get(e.getId());
        if (drawnId == null) return Optional.empty();
        Entity entity = e.level().getEntity(drawnId);
        if (!(entity instanceof AbstractDrawnEntity drawn)) return Optional.empty();
        return Optional.of(drawn);
    }

    public boolean isPulling(final Entity e) {
        return this.pulling.containsKey(e.getId());
    }

    public void tick(Level level) {
        final Iterator<Integer> it = this.pulling.keySet().iterator();
        while (it.hasNext()) {
            final int pullId = it.next();
            final UUID cartId = this.pulling.get(pullId);
            Entity e = level.getEntity(cartId);
            if (!(e instanceof AbstractDrawnEntity cart)) {
                it.remove();
                continue;
            }
            if (cart.shouldStopPulledTick()) {
                it.remove();
                setDirty();
            } else {
                if (!(cart.getPulling() instanceof AbstractDrawnEntity)) {
                    cart.pulledTick();
                }
            }
        }
    }

    public Optional<Entity> getCurrentlyPulling(AbstractDrawnEntity drawn) {
        OptionalInt id = pulling.keySet().intStream()
                .filter(pullID -> pulling.get(pullID) == drawn.getUUID())
                .findFirst();
        if (id.isEmpty()) return Optional.empty();
        return Optional.ofNullable(drawn.level().getEntity(id.getAsInt()));
    }

    public Int2ObjectMap<UUID> getPulling() {
        return pulling;
    }

    public static NiftyWorld get(Level level) {
        if (level.isClientSide()) {
            return getClient();
        } else {
            return getServer(NiftyCarts.server, level.dimension());
        }
    }

    public static NiftyWorld getClient() {
        if (clientInstance == null) {
            clientInstance = new NiftyWorld();
        }
        return clientInstance;
    }

    public static NiftyWorld getServer(MinecraftServer server, ResourceKey<@NotNull Level> levelType) {
        var dataStorage = Objects.requireNonNull(server.getLevel(levelType)).getDataStorage();
        return dataStorage.computeIfAbsent(TYPE);
    }

    record EntityWithId(int id, UUID entityId) {
        public static final Codec<EntityWithId> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.INT.fieldOf("id").forGetter(EntityWithId::id),
                        UUIDUtil.CODEC.fieldOf("UUID").forGetter(EntityWithId::entityId)
                ).apply(instance, EntityWithId::new));
        public static EntityWithId from(Int2ObjectMap.Entry<AbstractDrawnEntity> entry) {
            return new EntityWithId(entry.getIntKey(), entry.getValue().getUUID());
        }
    }

}
