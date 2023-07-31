package net.jmb19905.astikorcarts.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.jmb19905.astikorcarts.AstikorCarts;
import net.jmb19905.astikorcarts.client.renderer.AstikorCartsModelLayers;
import net.jmb19905.astikorcarts.client.renderer.entity.AnimalCartRenderer;
import net.jmb19905.astikorcarts.client.renderer.entity.PlowRenderer;
import net.jmb19905.astikorcarts.client.renderer.entity.PostilionRenderer;
import net.jmb19905.astikorcarts.client.renderer.entity.SupplyCartRenderer;
import net.jmb19905.astikorcarts.client.renderer.entity.model.AnimalCartModel;
import net.jmb19905.astikorcarts.client.renderer.entity.model.PlowModel;
import net.jmb19905.astikorcarts.client.renderer.entity.model.SupplyCartModel;
import net.jmb19905.astikorcarts.client.screen.PlowScreen;
import net.jmb19905.astikorcarts.network.clientbound.UpdateDrawnMessage;
import net.jmb19905.astikorcarts.network.serverbound.ActionKeyMessage;
import net.jmb19905.astikorcarts.network.serverbound.ToggleSlowMessage;
import net.jmb19905.astikorcarts.util.AstikorWorld;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import org.lwjgl.glfw.GLFW;

import static net.jmb19905.astikorcarts.AstikorCarts.ACTION_KEY_MESSAGE_ID;
import static net.jmb19905.astikorcarts.AstikorCarts.UPDATE_DRAWN_MESSAGE_ID;

public class AstikorCartsClient implements ClientModInitializer {

	private static KeyMapping actionKeyMapping;

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(UPDATE_DRAWN_MESSAGE_ID, (client, handler, buf, responseSender) -> {
			UpdateDrawnMessage msg = new UpdateDrawnMessage();
			msg.decode(buf);
            assert client.level != null;
            UpdateDrawnMessage.handle(msg, client.level);
		});
		EntityRendererRegistry.register(AstikorCarts.SUPPLY_CART_ENTITY, SupplyCartRenderer::new);
		EntityRendererRegistry.register(AstikorCarts.ANIMAL_CART_ENTITY, AnimalCartRenderer::new);
		EntityRendererRegistry.register(AstikorCarts.PLOW_ENTITY, PlowRenderer::new);
		EntityRendererRegistry.register(AstikorCarts.POSTILION_ENTITY, PostilionRenderer::new);

		EntityModelLayerRegistry.registerModelLayer(AstikorCartsModelLayers.SUPPLY_CART, SupplyCartModel::createLayer);
		EntityModelLayerRegistry.registerModelLayer(AstikorCartsModelLayers.ANIMAL_CART, AnimalCartModel::createLayer);
		EntityModelLayerRegistry.registerModelLayer(AstikorCartsModelLayers.PLOW, PlowModel::createLayer);

		MenuScreens.register(AstikorCarts.PLOW_MENU_TYPE, PlowScreen::new);

		actionKeyMapping = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.astikorcarts.desc",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_R,
				"key.categories.astikorcarts"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (actionKeyMapping.consumeClick()) {
				var buf = PacketByteBufs.create();
				ActionKeyMessage message = new ActionKeyMessage();
				message.encode(buf);
				ClientPlayNetworking.send(ACTION_KEY_MESSAGE_ID, buf);
			}
			var mc = Minecraft.getInstance();
			var player = client.player;
			if (player != null && ToggleSlowMessage.getCart(player).isPresent()) {
				final var binding = mc.options.keySprint;
				while (binding.consumeClick()) {
					var buf = PacketByteBufs.create();
					var msg = new ToggleSlowMessage();
					msg.encode(buf);
					ClientPlayNetworking.send(AstikorCarts.TOGGLE_SLOW_MESSAGE_ID, buf);
					KeyMapping.set(binding.getDefaultKey(), false);
				}
			}
			if (!client.isPaused() && client.level != null) {
				AstikorWorld.getClient().tick();
			}
		});
	}
}