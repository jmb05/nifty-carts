package net.jmb19905.niftycarts.client.mixin;

import net.jmb19905.niftycarts.client.NiftyCartsClient;
import net.jmb19905.niftycarts.entity.ReaperEntity;
import net.jmb19905.niftycarts.network.serverbound.ToggleSlowPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Shadow private ClientLevel level;

    @Inject(method = "handleSetEntityPassengersPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;setOverlayMessage(Lnet/minecraft/network/chat/Component;Z)V"), cancellable = true)
    private void handleSetEntityPassengersPacket(ClientboundSetPassengersPacket clientboundSetPassengersPacket, CallbackInfo ci) {
        Entity vehicle = this.level.getEntity(clientboundSetPassengersPacket.getVehicle());
        if (ToggleSlowPayload.isSlow(vehicle)) {
            Component slowToggled = Component.translatable("message.niftycarts.slow_toggled_on", NiftyCartsClient.toggleSlowMapping.getTranslatedKeyMessage());
            Component component = Component.translatable("mount.onboard", Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage())
                    .append("; ")
                    .append(slowToggled);
            Minecraft.getInstance().gui.setOverlayMessage(component, false);
            Minecraft.getInstance().getNarrator().sayNow(component);
            ci.cancel();
        } else if (vehicle instanceof ReaperEntity) {
            Component slowModeMessage = Component.translatable("tutorial.slow.message", NiftyCartsClient.toggleSlowMapping.getTranslatedKeyMessage());
            Component component = Component.translatable("mount.onboard", Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage()).append("; ").append(slowModeMessage);
            Minecraft.getInstance().gui.setOverlayMessage(component, false);
            Minecraft.getInstance().getNarrator().sayNow(component);
            ci.cancel();
        }
    }

}