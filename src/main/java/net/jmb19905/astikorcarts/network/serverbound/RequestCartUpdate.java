package net.jmb19905.astikorcarts.network.serverbound;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.jmb19905.astikorcarts.AstikorCarts;
import net.jmb19905.astikorcarts.network.Message;
import net.jmb19905.astikorcarts.network.clientbound.UpdateDrawnMessage;
import net.jmb19905.astikorcarts.util.AstikorWorld;
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
        var pulling = AstikorWorld.get(level).getPulling();
        pulling.keySet().intStream()
                .filter(pullId -> pulling.get(pullId).getId() == msg.cartId)
                .findFirst().ifPresent(pullId -> {
                    var buf = PacketByteBufs.create();
                    UpdateDrawnMessage update = new UpdateDrawnMessage(pullId, msg.cartId);
                    update.encode(buf);
                    ServerPlayNetworking.send(player, AstikorCarts.UPDATE_DRAWN_MESSAGE_ID, buf);
                });
    }

}
