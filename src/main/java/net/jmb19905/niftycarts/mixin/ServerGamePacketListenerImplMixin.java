package net.jmb19905.niftycarts.mixin;

import com.mojang.logging.LogUtils;
import net.jmb19905.niftycarts.entity.AbstractDrawnEntity;
import net.jmb19905.niftycarts.util.NiftyWorld;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin extends ServerCommonPacketListenerImpl {
    @Unique
    private static final Logger LOGGER = LogUtils.getLogger();
    @Shadow
    public ServerPlayer player;
    @Shadow
    private Entity lastVehicle;
    private double pullingFirstGoodX;
    private double pullingFirstGoodY;
    private double pullingFirstGoodZ;
    private double pullingLastGoodX;
    private double pullingLastGoodY;
    private double pullingLastGoodZ;

    public ServerGamePacketListenerImplMixin(MinecraftServer minecraftServer, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraftServer, connection, commonListenerCookie);
    }

    @Inject(method = "handleMoveVehicle", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getRootVehicle()Lnet/minecraft/world/entity/Entity;"), cancellable = true)
    public void handleMoveVehicle(ServerboundMoveVehiclePacket serverboundMoveVehiclePacket, CallbackInfo ci) {

        /*Entity entity = this.player.getRootVehicle();
        if (entity instanceof AbstractDrawnEntity drawnEntity) {
            ServerLevel level = this.player.serverLevel();
            Optional<Entity> pullingOpt = NiftyWorld.get(level).getCurrentlyPulling(drawnEntity);
            if (pullingOpt.isPresent()) {
                Entity pulling = pullingOpt.get();
                if (entity != this.player && entity.getControllingPassenger() == this.player && entity == this.lastVehicle) {
                    double pullingX = pulling.getX();
                    double pullingY = pulling.getY();
                    double pullingZ = pulling.getZ();
                    double clientPullingX = clampHorizontal(serverboundMoveVehiclePacket.position().x());
                    double clientPullingY = clampVertical(serverboundMoveVehiclePacket.position().y());
                    double clientPullingZ = clampHorizontal(serverboundMoveVehiclePacket.position().z());
                    float clientPullingYRot = Mth.wrapDegrees(serverboundMoveVehiclePacket.yRot());
                    float clientPullingXRot = Mth.wrapDegrees(serverboundMoveVehiclePacket.xRot());
                    double clientPullingDX = clientPullingX - this.pullingFirstGoodX;
                    double clientPullingDY = clientPullingY - this.pullingFirstGoodY;
                    double clientPullingDZ = clientPullingZ - this.pullingFirstGoodZ;
                    double pullingDeltaSquared = entity.getDeltaMovement().lengthSqr();
                    double clientPullingDeltaSquared = clientPullingDX * clientPullingDX + clientPullingDY * clientPullingDY + clientPullingDZ * clientPullingDZ;
                    if (clientPullingDeltaSquared - pullingDeltaSquared > 100.0 && !this.isSingleplayerOwner()) {
                        LOGGER.warn("{} (pulling entity of {}) moved too quickly! {},{},{}", entity.getName().getString(), this.player.getName().getString(), clientPullingDX, clientPullingDY, clientPullingDZ);
                        this.send(ClientboundMoveVehiclePacket.fromEntity(entity));
                        ci.cancel();
                        return;
                    }
                }
            }
        }*/
    }

    @Unique
    private static double clampHorizontal(double d) {
        return Mth.clamp(d, -3.0E7, 3.0E7);
    }

    @Unique
    private static double clampVertical(double d) {
        return Mth.clamp(d, -2.0E7, 2.0E7);
    }

}
