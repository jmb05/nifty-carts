package net.jmb19905.niftycarts.client;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsConfig;
import net.jmb19905.niftycarts.client.renderer.NiftyCartsModelLayers;
import net.jmb19905.niftycarts.client.renderer.entity.*;
import net.jmb19905.niftycarts.client.renderer.entity.model.*;
import net.jmb19905.niftycarts.client.screen.ChestScreen;
import net.jmb19905.niftycarts.client.screen.PlowScreen;
import net.jmb19905.niftycarts.client.screen.SeedDrillScreen;
import net.jmb19905.niftycarts.item.CartItem;
import net.jmb19905.niftycarts.network.clientbound.UpdateDrawnMessage;
import net.jmb19905.niftycarts.network.serverbound.ActionKeyMessage;
import net.jmb19905.niftycarts.network.serverbound.ToggleSlowMessage;
import net.jmb19905.niftycarts.util.NiftyWorld;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.config.ModConfig;
import org.lwjgl.glfw.GLFW;

import static net.jmb19905.niftycarts.NiftyCarts.ACTION_KEY_MESSAGE_ID;
import static net.jmb19905.niftycarts.NiftyCarts.UPDATE_DRAWN_MESSAGE_ID;

public class NiftyCartsClient implements ClientModInitializer {

	private static KeyMapping actionKeyMapping;
	public static KeyMapping toggleSlowMapping;

	@Override
	public void onInitializeClient() {
		ForgeConfigRegistry.INSTANCE.register(NiftyCarts.MOD_ID, ModConfig.Type.CLIENT, NiftyCartsConfig.clientSpec());
		ClientPlayNetworking.registerGlobalReceiver(UPDATE_DRAWN_MESSAGE_ID, (client, handler, buf, responseSender) -> {
			UpdateDrawnMessage msg = new UpdateDrawnMessage();
			msg.decode(buf);
            assert client.level != null;
            UpdateDrawnMessage.handle(msg, client.level);
		});
		EntityRendererRegistry.register(NiftyCarts.SUPPLY_CART_ENTITY, SupplyCartRenderer::new);
		EntityRendererRegistry.register(NiftyCarts.HAND_CART_ENTITY, HandCartRenderer::new);
		EntityRendererRegistry.register(NiftyCarts.ANIMAL_CART_ENTITY, AnimalCartRenderer::new);
		EntityRendererRegistry.register(NiftyCarts.PLOW_ENTITY, PlowRenderer::new);
		EntityRendererRegistry.register(NiftyCarts.SEED_DRILL_ENTITY, SeedDrillRenderer::new);
		EntityRendererRegistry.register(NiftyCarts.REAPER_ENTITY, ReaperRenderer::new);
        EntityRendererRegistry.register(NiftyCarts.WAGON_ENTITY, WagonRenderer::new);
		EntityRendererRegistry.register(NiftyCarts.POSTILION_ENTITY, PostilionRenderer::new);

		EntityModelLayerRegistry.registerModelLayer(NiftyCartsModelLayers.SUPPLY_CART, SupplyCartModel::createLayer);
		EntityModelLayerRegistry.registerModelLayer(NiftyCartsModelLayers.HAND_CART, HandCartModel::createLayer);
		EntityModelLayerRegistry.registerModelLayer(NiftyCartsModelLayers.ANIMAL_CART, AnimalCartModel::createLayer);
		EntityModelLayerRegistry.registerModelLayer(NiftyCartsModelLayers.PLOW, PlowModel::createLayer);
		EntityModelLayerRegistry.registerModelLayer(NiftyCartsModelLayers.SEED_DRILL, SeedDrillModel::createLayer);
		EntityModelLayerRegistry.registerModelLayer(NiftyCartsModelLayers.REAPER, ReaperModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(NiftyCartsModelLayers.WAGON, WagonModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(NiftyCartsModelLayers.WAGON_ROOF, WagonModel::createRoofLayer);
        EntityModelLayerRegistry.registerModelLayer(NiftyCartsModelLayers.WAGON_CHEST, WagonModel::createChestLayer);

		MenuScreens.register(NiftyCarts.PLOW_MENU_TYPE, PlowScreen::new);
		MenuScreens.register(NiftyCarts.SEED_DRILL_MENU_TYPE, SeedDrillScreen::new);
        MenuScreens.register(NiftyCarts.CHEST_9x4_MENU_TYPE, ChestScreen::new);
        MenuScreens.register(NiftyCarts.CHEST_9x8_MENU_TYPE, ChestScreen::new);
        MenuScreens.register(NiftyCarts.CHEST_9x12_MENU_TYPE, ChestScreen::new);

		actionKeyMapping = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.niftycarts.action",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_R,
				"key.categories.niftycarts"
		));

		toggleSlowMapping = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.niftycarts.slow",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_Z,
				"key.categories.niftycarts"
		));


        ItemTooltipCallback.EVENT.register((stack, tooltipContext, lines) -> {
            if (stack.getItem() instanceof CartItem cart) {
                if (!Screen.hasShiftDown()) {
                    lines.add(Component.translatable("item.cart.press_shift_tooltip").withStyle(ChatFormatting.GRAY));
                } else {
                    lines.add(Component.translatable("item." + cart.getCartType() + ".tooltip1").withStyle(ChatFormatting.GRAY));
                    lines.add(Component.translatable("item." + cart.getCartType() + ".tooltip2").withStyle(ChatFormatting.GRAY));
                }
            }
        });

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (actionKeyMapping.consumeClick()) {
				var buf = PacketByteBufs.create();
				ActionKeyMessage message = new ActionKeyMessage();
				message.encode(buf);
				ClientPlayNetworking.send(ACTION_KEY_MESSAGE_ID, buf);
			}
			var player = client.player;
			if (player != null) {
				while (toggleSlowMapping.consumeClick()) {
                    if (player.getControlledVehicle() != null && ToggleSlowMessage.isSlowable(player.getControlledVehicle())) {
                        if (!ToggleSlowMessage.isSlow(player.getControlledVehicle())) {
                            player.displayClientMessage(Component.translatable("message.niftycarts.slow_toggled_on", toggleSlowMapping.getTranslatedKeyMessage()), true);
                        } else {
                            player.displayClientMessage(Component.translatable("message.niftycarts.slow_toggled_off", toggleSlowMapping.getTranslatedKeyMessage()), true);
                        }
                    }
					var buf = PacketByteBufs.create();
					var msg = new ToggleSlowMessage();
					msg.encode(buf);
					ClientPlayNetworking.send(NiftyCarts.TOGGLE_SLOW_MESSAGE_ID, buf);
					KeyMapping.set(toggleSlowMapping.getDefaultKey(), false);
				}
			}
			if (!client.isPaused() && client.level != null) {
				NiftyWorld.getClient().tick();
			}
		});
	}
}