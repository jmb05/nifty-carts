package net.jmb19905.astikorcarts.client.mixin;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.jmb19905.astikorcarts.AstikorCarts;
import net.jmb19905.astikorcarts.entity.AbstractDrawnEntity;
import net.jmb19905.astikorcarts.network.serverbound.RequestCartUpdate;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {

    @Inject(method = "addEntity", at = @At("TAIL"))
    public void addEntity(int i, Entity entity, CallbackInfo ci) {
        if (entity instanceof AbstractDrawnEntity d) {
            var buf = PacketByteBufs.create();
            RequestCartUpdate msg = new RequestCartUpdate(d.getId());
            msg.encode(buf);
            ClientPlayNetworking.send(AstikorCarts.REQUEST_CART_UPDATE_MESSAGE_ID, buf);
        }
    }

}
