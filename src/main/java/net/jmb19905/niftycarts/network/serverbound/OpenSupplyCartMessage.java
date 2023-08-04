package net.jmb19905.niftycarts.network.serverbound;

import net.jmb19905.niftycarts.entity.SupplyCartEntity;
import net.jmb19905.niftycarts.network.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class OpenSupplyCartMessage implements Message {
    @Override
    public void encode(FriendlyByteBuf buf) {
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
    }

    public static void handle(final Player player) {
        final Entity ridden = player.getVehicle();
        if (ridden instanceof SupplyCartEntity) {
            ((SupplyCartEntity) ridden).openCustomInventoryScreen(player);
        }
    }

}
