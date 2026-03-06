package net.jmb19905.niftycarts.network.serverbound;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.network.clientbound.UpdateDrawnPayload;
import net.jmb19905.niftycarts.util.NiftyWorld;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record RequestCartUpdatePayload(int cartId) implements CustomPacketPayload {

    public static final Type<@NotNull RequestCartUpdatePayload> TYPE = CustomPacketPayload.createType(NiftyCarts.MOD_ID + "_request_cart_update");
    public static final StreamCodec<@NotNull FriendlyByteBuf, @NotNull RequestCartUpdatePayload> CODEC = new StreamCodec<>() {
        @Override
        public @NotNull RequestCartUpdatePayload decode(FriendlyByteBuf buf) {
            return new RequestCartUpdatePayload(buf.readVarInt());
        }

        @Override
        public void encode(FriendlyByteBuf buf, RequestCartUpdatePayload payload) {
            buf.writeVarInt(payload.cartId());
        }
    };

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RequestCartUpdatePayload msg, ServerPlayer player) {
        var level = player.level();
        var pulling = NiftyWorld.get(level).getPulling();

        pulling.keySet().intStream()
                .filter(pullId -> NiftyWorld.get(level).getDrawn(level.getEntity(pullId)).map(Entity::getId).orElse(-1) == msg.cartId)
                .findFirst().ifPresent(pullId -> ServerPlayNetworking.send(player, new UpdateDrawnPayload(pullId, msg.cartId)));
    }
}
