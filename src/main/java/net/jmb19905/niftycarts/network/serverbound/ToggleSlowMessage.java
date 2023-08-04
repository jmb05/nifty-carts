package net.jmb19905.niftycarts.network.serverbound;

import net.jmb19905.niftycarts.entity.AbstractDrawnEntity;
import net.jmb19905.niftycarts.network.Message;
import net.jmb19905.niftycarts.util.NiftyWorld;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

public class ToggleSlowMessage implements Message {
    @Override
    public void encode(FriendlyByteBuf buf) {
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
    }

    public static void handle(final Player player) {
        getCart(player).ifPresent(AbstractDrawnEntity::toggleSlow);
    }

    public static Optional<AbstractDrawnEntity> getCart(final Player player) {
        final Entity ridden = player.getVehicle();
        if (ridden == null) return Optional.empty();
        if (ridden instanceof AbstractDrawnEntity) return Optional.of((AbstractDrawnEntity) ridden);
        return NiftyWorld.get(player.level).getDrawn(ridden);
    }

}
