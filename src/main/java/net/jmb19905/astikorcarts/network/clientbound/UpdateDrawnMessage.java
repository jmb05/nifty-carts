package net.jmb19905.astikorcarts.network.clientbound;

import net.jmb19905.astikorcarts.entity.AbstractDrawnEntity;
import net.jmb19905.astikorcarts.network.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class UpdateDrawnMessage implements Message {

    private int pullingId;
    private int cartId;

    public UpdateDrawnMessage() {
    }

    public UpdateDrawnMessage(final int pullingId, final int cartId) {
        this.pullingId = pullingId;
        this.cartId = cartId;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.pullingId);
        buf.writeVarInt(this.cartId);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.pullingId = buf.readVarInt();
        this.cartId = buf.readVarInt();
    }

    public static void handle(UpdateDrawnMessage msg, Level level) {
        final Entity e = level.getEntity(msg.cartId);
        if (e instanceof AbstractDrawnEntity drawn) {
            if (msg.pullingId < 0) {
                drawn.setPulling(null);
            } else {
                drawn.setPulling(level.getEntity(msg.pullingId));
            }
        }
    }

}
