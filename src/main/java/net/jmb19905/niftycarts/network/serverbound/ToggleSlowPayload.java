package net.jmb19905.niftycarts.network.serverbound;

import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsConfig;
import net.jmb19905.niftycarts.entity.AbstractDrawnEntity;
import net.jmb19905.niftycarts.util.NiftyWorld;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ToggleSlowPayload() implements CustomPacketPayload {

    public static final ResourceLocation PULL_SLOWLY_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(NiftyCarts.MOD_ID, "pull_slowly");

    public static final Type<ToggleSlowPayload> TYPE = CustomPacketPayload.createType(NiftyCarts.MOD_ID + "_toggle_slow");
    public static final StreamCodec<FriendlyByteBuf, ToggleSlowPayload> CODEC = new StreamCodec<>() {
        @Override
        public @NotNull ToggleSlowPayload decode(FriendlyByteBuf object) {
            return new ToggleSlowPayload();
        }

        @Override
        public void encode(FriendlyByteBuf object, ToggleSlowPayload object2) {}
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final Player player) {
        Entity vehicle = player.getControlledVehicle();
        if (vehicle != null) {
            if (!toggleSlow(vehicle)) {
                getCart(player)
                        .map(AbstractDrawnEntity::getPulling)
                        .ifPresent(ToggleSlowPayload::toggleSlow);
            }
        }
    }

    public static boolean toggleSlow(Entity vehicle) {
        if (!(vehicle instanceof LivingEntity)) return false;
        final AttributeInstance speed = ((LivingEntity) vehicle).getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return false;
        final AttributeModifier modifier = speed.getModifier(PULL_SLOWLY_MODIFIER_ID);
        if (modifier == null) {
            speed.addTransientModifier(new AttributeModifier(
                    PULL_SLOWLY_MODIFIER_ID,
                    NiftyCartsConfig.get().slowSpeed.get(),
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            ));
        } else {
            speed.removeModifier(modifier.id());
        }
        return true;
    }

    public static boolean isSlowable(Entity vehicle) {
        if (!(vehicle instanceof LivingEntity)) return false;
        return ((LivingEntity) vehicle).getAttribute(Attributes.MOVEMENT_SPEED) != null;
    }

    public static boolean isSlow(Entity vehicle) {
        if (!(vehicle instanceof LivingEntity)) return false;
        final AttributeInstance speed = ((LivingEntity) vehicle).getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return false;
        final AttributeModifier modifier = speed.getModifier(PULL_SLOWLY_MODIFIER_ID);
        return modifier != null;
    }

    public static Optional<AbstractDrawnEntity> getCart(final Player player) {
        final Entity ridden = player.getVehicle();
        if (ridden == null) return Optional.empty();
        if (ridden instanceof AbstractDrawnEntity) return Optional.of((AbstractDrawnEntity) ridden);
        return NiftyWorld.get(player.level()).getDrawn(ridden);
    }

}
