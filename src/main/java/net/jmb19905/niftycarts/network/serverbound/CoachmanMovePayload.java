package net.jmb19905.niftycarts.network.serverbound;

import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.entity.AbstractDrawnEntity;
import net.jmb19905.niftycarts.util.NiftyWorld;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public record CoachmanMovePayload(float x, float y, float z, float xRot, float yRot) implements CustomPacketPayload {

    public static final Type<CoachmanMovePayload> TYPE = CustomPacketPayload.createType(NiftyCarts.MOD_ID + "_coachman_move");
    public static final StreamCodec<FriendlyByteBuf, CoachmanMovePayload> CODEC = new StreamCodec<>() {
        @Override
        public @NotNull CoachmanMovePayload decode(FriendlyByteBuf buf) {
            return new CoachmanMovePayload(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
        }

        @Override
        public void encode(FriendlyByteBuf buf, CoachmanMovePayload payload) {
            buf.writeFloat(payload.x);
            buf.writeFloat(payload.y);
            buf.writeFloat(payload.z);
            buf.writeFloat(payload.xRot);
            buf.writeFloat(payload.yRot);
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static double clampHorizontal(double d) {
        return Mth.clamp(d, -3.0E7, 3.0E7);
    }

    private static double clampVertical(double d) {
        return Mth.clamp(d, -2.0E7, 2.0E7);
    }

    public static void handle(CoachmanMovePayload msg, ServerPlayer player) {
        Entity vehicle = player.getVehicle();
        if (vehicle instanceof AbstractDrawnEntity drawn) {
            Optional<Entity> entityOpt = NiftyWorld.get(player.level()).getCurrentlyPulling(drawn);
            if (entityOpt.isPresent()) {
                Entity entity = entityOpt.get();
                LivingEntity livingEntity;
                ServerLevel serverLevel = player.serverLevel();
                double d = entity.getX();
                double e = entity.getY();
                double f = entity.getZ();
                double g = clampHorizontal(msg.x());
                double h = clampVertical(msg.y());
                double i = clampHorizontal(msg.z());
                float j = Mth.wrapDegrees(msg.yRot());
                float k = Mth.wrapDegrees(msg.xRot());
                double l = g - this.vehicleFirstGoodX;
                double m = h - this.vehicleFirstGoodY;
                double n = i - this.vehicleFirstGoodZ;
                double p = l * l + m * m + n * n;
                double o = entity.getDeltaMovement().lengthSqr();
                if (p - o > 100.0 && !Objects.requireNonNull(player.getServer()).isSingleplayerOwner(this.playerProfile())) {
                    LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", entity.getName().getString(), player.getName().getString(), l, m, n);
                    this.send(new ClientboundMoveVehiclePacket(entity));
                    return;
                }
                boolean bl = serverLevel.noCollision(entity, entity.getBoundingBox().deflate(0.0625));
                l = g - this.vehicleLastGoodX;
                m = h - this.vehicleLastGoodY - 1.0E-6;
                n = i - this.vehicleLastGoodZ;
                boolean bl2 = entity.verticalCollisionBelow;
                if (entity instanceof LivingEntity && (livingEntity = (LivingEntity)entity).onClimbable()) {
                    livingEntity.resetFallDistance();
                }
                entity.move(MoverType.PLAYER, new Vec3(l, m, n));
                double q = m;
                l = g - entity.getX();
                m = h - entity.getY();
                if (m > -0.5 || m < 0.5) {
                    m = 0.0;
                }
                n = i - entity.getZ();
                p = l * l + m * m + n * n;
                boolean bl3 = false;
                if (p > 0.0625) {
                    bl3 = true;
                    LOGGER.warn("{} (vehicle of {}) moved wrongly! {}", entity.getName().getString(), player.getName().getString(), Math.sqrt(p));
                }
                entity.absMoveTo(g, h, i, j, k);
                boolean bl4 = serverLevel.noCollision(entity, entity.getBoundingBox().deflate(0.0625));
                if (bl && (bl3 || !bl4)) {
                    entity.absMoveTo(d, e, f, j, k);
                    this.send(new ClientboundMoveVehiclePacket(entity));
                    return;
                }
                player.serverLevel().getChunkSource().move(player);
                entity.recordMovementThroughBlocks(new Vec3(d, e, f), entity.position());
                Vec3 vec3 = new Vec3(entity.getX() - d, entity.getY() - e, entity.getZ() - f);
                handlePlayerKnownMovement(player, vec3);
                player.checkMovementStatistics(vec3.x, vec3.y, vec3.z);
                this.clientVehicleIsFloating = q >= -0.03125 && !bl2 && !Objects.requireNonNull(player.getServer()).isFlightAllowed() && !entity.isNoGravity() && this.noBlocksAround(entity);
                this.vehicleLastGoodX = entity.getX();
                this.vehicleLastGoodY = entity.getY();
                this.vehicleLastGoodZ = entity.getZ();
            }
        }
    }

    private static void handlePlayerKnownMovement(ServerPlayer player, Vec3 vec3) {
        if (vec3.lengthSqr() > (double)1.0E-5f) {
            player.resetLastActionTime();
        }
        player.setKnownMovement(vec3);
        receivedMovementThisTick = true;
    }
}
