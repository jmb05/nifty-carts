package net.jmb19905.niftycarts.network.serverbound;

import it.unimi.dsi.fastutil.Pair;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.entity.AbstractDrawnEntity;
import net.jmb19905.niftycarts.network.Message;
import net.jmb19905.niftycarts.util.NiftyWorld;
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

    public static void handle(final ActionKeyMessage ignoredMsg, final ServerPlayer player) {
        final Entity pulling;
        final Level level = player.level();
        if (player.getVehicle() == null) {
            pulling = player;
        } else {
            pulling = player.getVehicle();
        }
        var drawn = NiftyWorld.getServer(NiftyCarts.server, level.dimension()).getDrawn(pulling);
        drawn.map(c -> Pair.of(c, (Entity) null))
                .or(() -> level.getEntitiesOfClass(AbstractDrawnEntity.class, pulling.getBoundingBox().inflate(2.0d), entity -> entity != pulling).stream()
                        .min(Comparator.comparing(pulling::distanceTo))
                        .map(c -> Pair.of(c, pulling))
                ).ifPresent(p -> p.key().setPulling(p.value()));
    }

}