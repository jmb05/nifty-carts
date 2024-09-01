package net.jmb19905.niftycarts.network.serverbound;

import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.entity.SupplyCartEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record OpenSupplyCartPayload() implements CustomPacketPayload {

    public static final Type<OpenSupplyCartPayload> TYPE = CustomPacketPayload.createType(NiftyCarts.MOD_ID + "_open_supply_cart");
    public static final StreamCodec<FriendlyByteBuf, OpenSupplyCartPayload> CODEC = new StreamCodec<>() {
        @Override
        public @NotNull OpenSupplyCartPayload decode(FriendlyByteBuf object) {
            return new OpenSupplyCartPayload();
        }

        @Override
        public void encode(FriendlyByteBuf object, OpenSupplyCartPayload object2) {
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final Player player) {
        final Entity ridden = player.getVehicle();
        if (ridden instanceof SupplyCartEntity) {
            ((SupplyCartEntity) ridden).openCustomInventoryScreen(player);
        }
    }
}
