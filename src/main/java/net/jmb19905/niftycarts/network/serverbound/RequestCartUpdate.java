package net.jmb19905.niftycarts.network.serverbound;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.network.Message;
import net.jmb19905.niftycarts.network.clientbound.UpdateDrawnMessage;
import net.jmb19905.niftycarts.util.NiftyWorld;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class RequestCartUpdate implements Message {

    private int cartId;

    public RequestCartUpdate() {
    }

    public RequestCartUpdate(int cartId) {
        this.cartId = cartId;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(cartId);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.cartId = buf.readVarInt();
    }

    public static void handle(RequestCartUpdate msg, ServerPlayer player) {
        var level = player.level;
        var pulling = NiftyWorld.get(level).getPulling();
        pulling.keySet().intStream()
                .filter(pullId -> pulling.get(pullId).getId() == msg.cartId)
                .findFirst().ifPresent(pullId -> {
                    var buf = PacketByteBufs.create();
                    UpdateDrawnMessage update = new UpdateDrawnMessage(pullId, msg.cartId);
                    update.encode(buf);
                    ServerPlayNetworking.send(player, NiftyCarts.UPDATE_DRAWN_MESSAGE_ID, buf);
                });
    }

}
