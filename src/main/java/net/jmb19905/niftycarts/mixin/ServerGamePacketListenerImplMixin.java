package net.jmb19905.niftycarts.mixin;

import net.jmb19905.niftycarts.entity.AbstractDrawnEntity;
import net.jmb19905.niftycarts.util.NiftyWorld;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin implements ServerGamePacketListener {

    @Shadow public ServerPlayer player;
    @Shadow private double vehicleFirstGoodX;
    @Shadow private double vehicleFirstGoodY;
    @Shadow private double vehicleFirstGoodZ;
    @Shadow private double vehicleLastGoodX;
    @Shadow private double vehicleLastGoodY;
    @Shadow private double vehicleLastGoodZ;
    @Shadow private boolean clientVehicleIsFloating;
    @Shadow private boolean receivedMovementThisTick;

    @Unique
    protected boolean isSingleplayerOwner(MinecraftServer server, Player player) {
        return server.isSingleplayerOwner(player.getGameProfile());
    }

    @Inject(method = "handleMoveVehicle", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getRootVehicle()Lnet/minecraft/world/entity/Entity;"))
    public void handleMoveVehicle(ServerboundMoveVehiclePacket serverboundMoveVehiclePacket, CallbackInfo ci){
        Entity cart = this.player.getRootVehicle();
        if (cart instanceof AbstractDrawnEntity drawn) {
            Optional<Entity> pullingOpt = NiftyWorld.get(this.player.level()).getCurrentlyPulling(drawn);
            if (pullingOpt.isPresent()) {
                Entity pulling = pullingOpt.get();
                ServerLevel serverLevel = this.player.serverLevel();
                MinecraftServer server = serverLevel.getServer();
                double d = pulling.getX();
                double e = pulling.getY();
                double f = pulling.getZ();
                double g = clampHorizontal(serverboundMoveVehiclePacket.getX());
                double h = clampVertical(serverboundMoveVehiclePacket.getY());
                double i = clampHorizontal(serverboundMoveVehiclePacket.getZ());
                float j = Mth.wrapDegrees(serverboundMoveVehiclePacket.getYRot());
                float k = Mth.wrapDegrees(serverboundMoveVehiclePacket.getXRot());
                double l = g - this.vehicleFirstGoodX;
                double m = h - this.vehicleFirstGoodY;
                double n = i - this.vehicleFirstGoodZ;
                double o = pulling.getDeltaMovement().lengthSqr();
                double p = l * l + m * m + n * n;
                if (p - o > 100.0 && !isSingleplayerOwner(server, player)) {
                    LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", pulling.getName().getString(), this.player.getName().getString(), l, m, n);
                    ((ServerGamePacketListenerImpl) (Object) this).send(new ClientboundMoveVehiclePacket(pulling));
                    return;
                }

                boolean bl = serverLevel.noCollision(pulling, pulling.getBoundingBox().deflate(0.0625));
                l = g - this.vehicleLastGoodX;
                m = h - this.vehicleLastGoodY - 1.0E-6;
                n = i - this.vehicleLastGoodZ;
                boolean bl2 = pulling.verticalCollisionBelow;
                if (pulling instanceof LivingEntity livingEntity) {
                    if (livingEntity.onClimbable()) {
                        livingEntity.resetFallDistance();
                    }
                }

                pulling.move(MoverType.PLAYER, new Vec3(l, m, n));
                double q = m;
                l = g - pulling.getX();
                m = h - pulling.getY();
                if (m > -0.5 || m < 0.5) {
                    m = 0.0;
                }

                n = i - pulling.getZ();
                p = l * l + m * m + n * n;
                boolean bl3 = false;
                if (p > 0.0625) {
                    bl3 = true;
                    LOGGER.warn("{} (vehicle of {}) moved wrongly! {}", pulling.getName().getString(), this.player.getName().getString(), Math.sqrt(p));
                }

                pulling.absMoveTo(g, h, i, j, k);
                boolean bl4 = serverLevel.noCollision(pulling, pulling.getBoundingBox().deflate(0.0625));
                if (bl && (bl3 || !bl4)) {
                    pulling.absMoveTo(d, e, f, j, k);
                    ((ServerGamePacketListenerImpl) (Object) this).send(new ClientboundMoveVehiclePacket(pulling));
                    return;
                }

                this.player.serverLevel().getChunkSource().move(this.player);
                pulling.recordMovementThroughBlocks(new Vec3(d, e, f), pulling.position());
                Vec3 vec3 = new Vec3(pulling.getX() - d, pulling.getY() - e, pulling.getZ() - f);
                this.handlePlayerKnownMovement(vec3);
                this.player.checkMovementStatistics(vec3.x, vec3.y, vec3.z);
                this.clientVehicleIsFloating = q >= -0.03125 && !bl2 && !server.isFlightAllowed() && !pulling.isNoGravity() && this.noBlocksAround(pulling);
                this.vehicleLastGoodX = pulling.getX();
                this.vehicleLastGoodY = pulling.getY();
                this.vehicleLastGoodZ = pulling.getZ();
            }
        }
    }

    @Unique
    private static double clampHorizontal(double d) {
        return Mth.clamp(d, -3.0E7, 3.0E7);
    }

    @Unique
    private static double clampVertical(double d) {
        return Mth.clamp(d, -2.0E7, 2.0E7);
    }

    @Unique
    private void handlePlayerKnownMovement(Vec3 vec3) {
        if (vec3.lengthSqr() > 9.999999747378752E-6) {
            this.player.resetLastActionTime();
        }

        this.player.setKnownMovement(vec3);
        this.receivedMovementThisTick = true;
    }

    @Unique
    private boolean noBlocksAround(Entity entity) {
        return entity.level().getBlockStates(entity.getBoundingBox().inflate(0.0625).expandTowards(0.0, -0.55, 0.0)).allMatch(BlockBehaviour.BlockStateBase::isAir);
    }

}
