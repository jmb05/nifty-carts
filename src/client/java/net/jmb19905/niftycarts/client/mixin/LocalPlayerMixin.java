package net.jmb19905.niftycarts.client.mixin;

import com.mojang.authlib.GameProfile;
import net.jmb19905.niftycarts.entity.AbstractDrawnEntity;
import net.jmb19905.niftycarts.util.NiftyWorld;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Player {

    @Final
    @Shadow
    public ClientPacketListener connection;

    public LocalPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Shadow
    protected abstract void sendIsSprintingIfNeeded();

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getRootVehicle()Lnet/minecraft/world/entity/Entity;"))
    public void tick(CallbackInfo ci) {
        Level level = this.level();
        Entity vehicle = getRootVehicle();
        if (vehicle instanceof AbstractDrawnEntity drawn) {
            Optional<Entity> entityOpt = NiftyWorld.get(level).getCurrentlyPulling(drawn);
            if (entityOpt.isPresent()) {
                Entity pulling = entityOpt.get();
                if (pulling.isControlledByLocalInstance()) {
                    this.connection.send(new ServerboundMoveVehiclePacket(pulling));
                    this.sendIsSprintingIfNeeded();
                }
            }
        }
    }


}
