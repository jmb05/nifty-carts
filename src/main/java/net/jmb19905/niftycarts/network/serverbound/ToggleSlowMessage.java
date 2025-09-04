package net.jmb19905.niftycarts.network.serverbound;

import net.jmb19905.niftycarts.NiftyCartsConfig;
import net.jmb19905.niftycarts.entity.AbstractDrawnEntity;
import net.jmb19905.niftycarts.network.Message;
import net.jmb19905.niftycarts.util.NiftyWorld;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.UUID;

public class ToggleSlowMessage implements Message {

    public static final UUID PULL_SLOWLY_MODIFIER_UUID = UUID.fromString("49B0E52E-48F2-4D89-BED7-4F5DF26F1263");

    @Override
    public void encode(FriendlyByteBuf buf) {}

    @Override
    public void decode(FriendlyByteBuf buf) {}

    public static void handle(final Player player) {
        Entity vehicle = player.getControlledVehicle();
        if (vehicle != null) {
            if (!toggleSlow(vehicle)) {
                getCart(player)
                        .map(AbstractDrawnEntity::getPulling)
                        .ifPresent(ToggleSlowMessage::toggleSlow);
            }
        }
    }

    public static boolean isSlowable(Entity entity) {
        if (!(entity instanceof LivingEntity)) return false;
        final AttributeInstance speed = ((LivingEntity) entity).getAttribute(Attributes.MOVEMENT_SPEED);
        return speed != null;
    }

    public static boolean isSlow(Entity entity) {
        if (!(entity instanceof LivingEntity)) return false;
        final AttributeInstance speed = ((LivingEntity) entity).getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return false;
        final AttributeModifier modifier = speed.getModifier(PULL_SLOWLY_MODIFIER_UUID);
        return modifier != null;
    }

    public static boolean toggleSlow(Entity entity) {
        if (!(entity instanceof LivingEntity)) return false;
        final AttributeInstance speed = ((LivingEntity) entity).getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return false;
        final AttributeModifier modifier = speed.getModifier(PULL_SLOWLY_MODIFIER_UUID);
        if (modifier == null) {
            speed.addTransientModifier(new AttributeModifier(
                    PULL_SLOWLY_MODIFIER_UUID,
                    "Pull slowly modifier",
                    NiftyCartsConfig.get().slowSpeed.get(),
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            ));
        } else {
            speed.removeModifier(modifier);
        }
        return true;
    }

    public static Optional<AbstractDrawnEntity> getCart(final Player player) {
        final Entity ridden = player.getVehicle();
        if (ridden == null) return Optional.empty();
        if (ridden instanceof AbstractDrawnEntity) return Optional.of((AbstractDrawnEntity) ridden);
        return NiftyWorld.get(player.level()).getDrawn(ridden);
    }

}
