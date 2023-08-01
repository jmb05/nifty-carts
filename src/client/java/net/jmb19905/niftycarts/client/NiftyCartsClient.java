package net.jmb19905.niftycarts.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.client.renderer.NiftyCartsModelLayers;
import net.jmb19905.niftycarts.client.renderer.entity.AnimalCartRenderer;
import net.jmb19905.niftycarts.client.renderer.entity.PlowRenderer;
import net.jmb19905.niftycarts.client.renderer.entity.PostilionRenderer;
import net.jmb19905.niftycarts.client.renderer.entity.SupplyCartRenderer;
import net.jmb19905.niftycarts.client.renderer.entity.model.AnimalCartModel;
import net.jmb19905.niftycarts.client.renderer.entity.model.PlowModel;
import net.jmb19905.niftycarts.client.renderer.entity.model.SupplyCartModel;
import net.jmb19905.niftycarts.client.screen.PlowScreen;
import net.jmb19905.niftycarts.network.clientbound.UpdateDrawnMessage;
import net.jmb19905.niftycarts.network.serverbound.ActionKeyMessage;
import net.jmb19905.niftycarts.network.serverbound.ToggleSlowMessage;
import net.jmb19905.niftycarts.util.NiftyWorld;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import org.lwjgl.glfw.GLFW;

import static net.jmb19905.niftycarts.NiftyCarts.ACTION_KEY_MESSAGE_ID;
import static net.jmb19905.niftycarts.NiftyCarts.UPDATE_DRAWN_MESSAGE_ID;

public class NiftyCartsClient implements ClientModInitializer {

	private static KeyMapping actionKeyMapping;

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(UPDATE_DRAWN_MESSAGE_ID, (client, handler, buf, responseSender) -> {
			UpdateDrawnMessage msg = new UpdateDrawnMessage();
			msg.decode(buf);
            assert client.level != null;
            UpdateDrawnMessage.handle(msg, client.level);
		});
		EntityRendererRegistry.register(NiftyCarts.SUPPLY_CART_ENTITY, SupplyCartRenderer::new);
		EntityRendererRegistry.register(NiftyCarts.ANIMAL_CART_ENTITY, AnimalCartRenderer::new);
		EntityRendererRegistry.register(NiftyCarts.PLOW_ENTITY, PlowRenderer::new);
		EntityRendererRegistry.register(NiftyCarts.POSTILION_ENTITY, PostilionRenderer::new);

		EntityModelLayerRegistry.registerModelLayer(NiftyCartsModelLayers.SUPPLY_CART, SupplyCartModel::createLayer);
		EntityModelLayerRegistry.registerModelLayer(NiftyCartsModelLayers.ANIMAL_CART, AnimalCartModel::createLayer);
		EntityModelLayerRegistry.registerModelLayer(NiftyCartsModelLayers.PLOW, PlowModel::createLayer);

		MenuScreens.register(NiftyCarts.PLOW_MENU_TYPE, PlowScreen::new);

		actionKeyMapping = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.niftycarts.desc",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_R,
				"key.categories.niftycarts"
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
					ClientPlayNetworking.send(NiftyCarts.TOGGLE_SLOW_MESSAGE_ID, buf);
					KeyMapping.set(binding.getDefaultKey(), false);
				}
			}
			if (!client.isPaused() && client.level != null) {
				NiftyWorld.getClient().tick();
			}
		});
	}
}