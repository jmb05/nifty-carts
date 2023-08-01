package net.jmb19905.astikorcarts.network.serverbound;

import it.unimi.dsi.fastutil.Pair;
import net.jmb19905.astikorcarts.AstikorCarts;
import net.jmb19905.astikorcarts.entity.AbstractDrawnEntity;
import net.jmb19905.astikorcarts.network.Message;
import net.jmb19905.astikorcarts.util.AstikorWorld;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Comparator;

public class ActionKeyMessage implements Message {
    @Override
    public void encode(FriendlyByteBuf buf) {
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
    }

    public static void handle(final ActionKeyMessage msg, final ServerPlayer player) {
        final Entity pulling = player.getVehicle();
        final Level level = player.level;
        if (pulling == null) return;
        var drawn = AstikorWorld.getServer(AstikorCarts.server, level.dimension()).getDrawn(pulling);
        drawn.map(c -> Pair.of(c, (Entity) null))
                .or(() -> level.getEntitiesOfClass(AbstractDrawnEntity.class, pulling.getBoundingBox().inflate(2.0d), entity -> entity != pulling).stream()
                        .min(Comparator.comparing(pulling::distanceTo))
                        .map(c -> Pair.of(c, pulling))
                ).ifPresent(p -> p.key().setPulling(p.value()));
    }

}
