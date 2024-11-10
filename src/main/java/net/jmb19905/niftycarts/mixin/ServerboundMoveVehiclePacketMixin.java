package net.jmb19905.niftycarts.mixin;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ServerboundMoveVehiclePacket.class)
public class ServerboundMoveVehiclePacketMixin {

    @Unique
    public UUID entityId;

    @Inject(method = "<init>(Lnet/minecraft/world/entity/Entity;)V", at = @At("TAIL"))
    protected void init(Entity entity, CallbackInfo ci) {
        this.entityId = entity.getUUID();
    }

    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
    protected void init(FriendlyByteBuf friendlyByteBuf, CallbackInfo ci) {
        this.entityId = friendlyByteBuf.readUUID();
    }

    @Inject(method = "write", at = @At("TAIL"))
    protected void write(FriendlyByteBuf friendlyByteBuf, CallbackInfo ci) {
        friendlyByteBuf.writeUUID(entityId);
    }

}
