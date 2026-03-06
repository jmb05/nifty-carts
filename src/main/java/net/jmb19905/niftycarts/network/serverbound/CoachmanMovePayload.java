package net.jmb19905.niftycarts.network.serverbound;

import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.entity.AbstractDrawnEntity;
import net.jmb19905.niftycarts.entity.PostilionEntity;
import net.jmb19905.niftycarts.util.NiftyWorld;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public record CoachmanMovePayload(float zza) implements CustomPacketPayload {

    public static final Type<@NotNull CoachmanMovePayload> TYPE = CustomPacketPayload.createType(NiftyCarts.MOD_ID + "_coachman_move");
    public static final StreamCodec<@NotNull FriendlyByteBuf, @NotNull CoachmanMovePayload> CODEC = new StreamCodec<>() {
        @Override
        public @NotNull CoachmanMovePayload decode(FriendlyByteBuf buf) {
            return new CoachmanMovePayload(buf.readFloat());
        }

        @Override
        public void encode(FriendlyByteBuf buf, CoachmanMovePayload msg) {
            buf.writeFloat(msg.zza());
        }
    };

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CoachmanMovePayload msg, ServerPlayer player) {
        ServerLevel level = player.level();
        Entity vehicle = player.getRootVehicle();
        if (vehicle != player && vehicle.getControllingPassenger() == player && vehicle instanceof AbstractDrawnEntity drawnEntity) {
            Entity pulling = NiftyWorld.get(level).getCurrentlyPulling(drawnEntity).orElse(null);
            if (pulling != null) {
                LivingEntity passenger = pulling.getControllingPassenger();
                if (passenger instanceof PostilionEntity postilion) {
                    postilion.setYRot(player.getYRot());
                    postilion.yRotO = postilion.getYRot();
                    postilion.setXRot(player.getXRot() * 0.5F);
                    postilion.zza = msg.zza();
                    postilion.xxa = 0.0F;
                }
            }
        }
    }
}
