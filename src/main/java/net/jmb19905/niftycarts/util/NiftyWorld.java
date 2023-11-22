package net.jmb19905.niftycarts.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.entity.AbstractDrawnEntity;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class NiftyWorld extends SavedData {

    private static NiftyWorld clientInstance = null;

    private final Int2ObjectMap<AbstractDrawnEntity> pulling = new Int2ObjectOpenHashMap<>();

    public void addPulling(final AbstractDrawnEntity drawn) {
        @Nullable final Entity pulling = drawn.getPulling();
        if (pulling != null) {
            this.pulling.put(pulling.getId(), drawn);
            setDirty();
        }
    }

    public void setPulling(int pullId, final AbstractDrawnEntity drawn) {
        this.pulling.put(pullId, drawn);
        setDirty();
    }

    public Optional<AbstractDrawnEntity> getDrawn(final Entity e) {
        if (e == null) return Optional.empty();
        return Optional.ofNullable(this.pulling.get(e.getId()));
    }

    public boolean isPulling(final Entity e) {
        return this.pulling.containsKey(e.getId());
    }

    public void tick() {
        final Iterator<Integer> it = this.pulling.keySet().iterator();
        while (it.hasNext()) {
            final int pullId = it.next();
            final AbstractDrawnEntity cart = this.pulling.get(pullId);
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

    public Int2ObjectMap<AbstractDrawnEntity> getPulling() {
        return pulling;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        ListTag drawnList = new ListTag();
        for (AbstractDrawnEntity drawn : pulling.values()) {
            drawnList.add(NbtUtils.createUUID(drawn.getUUID()));
        }
        tag.put("drawnList", drawnList);
        return tag;
    }

    public static NiftyWorld createFromNbt(CompoundTag tag, ServerLevel level) {
        NiftyWorld data = new NiftyWorld();
        ListTag drawnList = tag.getList("drawnList", Tag.TAG_INT_ARRAY);
        for (Tag item : drawnList) {
            UUID uuid = NbtUtils.loadUUID(item);
            if (level.getEntity(uuid) instanceof AbstractDrawnEntity drawn) {
                data.addPulling(drawn);
            }
        }
        return data;
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

    public static NiftyWorld getServer(MinecraftServer server, ResourceKey<Level> levelType) {
        var dataStorage = Objects.requireNonNull(server.getLevel(levelType)).getDataStorage();
        Factory<NiftyWorld> factory = new Factory<>(
                NiftyWorld::new,
                tag -> NiftyWorld.createFromNbt(tag, server.getLevel(levelType)),
                null
        );
        return dataStorage.computeIfAbsent(factory, NiftyCarts.MOD_ID);
    }

}
