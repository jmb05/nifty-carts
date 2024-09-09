package net.jmb19905.niftycarts.client;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsConfig;
import net.jmb19905.niftycarts.client.renderer.NiftyCartsModelLayers;
import net.jmb19905.niftycarts.client.renderer.entity.*;
import net.jmb19905.niftycarts.client.renderer.entity.model.AnimalCartModel;
import net.jmb19905.niftycarts.client.renderer.entity.model.HandCartModel;
import net.jmb19905.niftycarts.client.renderer.entity.model.PlowModel;
import net.jmb19905.niftycarts.client.renderer.entity.model.SupplyCartModel;
import net.jmb19905.niftycarts.client.screen.PlowScreen;
import net.jmb19905.niftycarts.network.clientbound.UpdateDrawnPayload;
import net.jmb19905.niftycarts.network.serverbound.ActionKeyPayload;
import net.jmb19905.niftycarts.network.serverbound.ToggleSlowPayload;
import net.jmb19905.niftycarts.util.NiftyWorld;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.config.ModConfig;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class NiftyCartsClient implements ClientModInitializer {

	private static KeyMapping actionKeyMapping;
	private static KeyMapping toggleSlowMapping;

	@Override
	public void onInitializeClient() {
		ForgeConfigRegistry.INSTANCE.register(NiftyCarts.MOD_ID, ModConfig.Type.CLIENT, NiftyCartsConfig.clientSpec());


		ClientPlayNetworking.registerGlobalReceiver(UpdateDrawnPayload.TYPE, (payload, ctx) -> ctx.client().execute(() -> UpdateDrawnPayload.handle(payload, Objects.requireNonNull(ctx.client().level))));
		EntityRendererRegistry.register(NiftyCarts.SUPPLY_CART_ENTITY, SupplyCartRenderer::new);
		EntityRendererRegistry.register(NiftyCarts.ANIMAL_CART_ENTITY, AnimalCartRenderer::new);
		EntityRendererRegistry.register(NiftyCarts.PLOW_ENTITY, PlowRenderer::new);
		EntityRendererRegistry.register(NiftyCarts.HAND_CART_ENTITY, HandCartRenderer::new);
		EntityRendererRegistry.register(NiftyCarts.POSTILION_ENTITY, PostilionRenderer::new);

		EntityModelLayerRegistry.registerModelLayer(NiftyCartsModelLayers.SUPPLY_CART, SupplyCartModel::createLayer);
		EntityModelLayerRegistry.registerModelLayer(NiftyCartsModelLayers.ANIMAL_CART, AnimalCartModel::createLayer);
		EntityModelLayerRegistry.registerModelLayer(NiftyCartsModelLayers.PLOW, PlowModel::createLayer);
		EntityModelLayerRegistry.registerModelLayer(NiftyCartsModelLayers.HAND_CART, HandCartModel::createLayer);

		MenuScreens.register(NiftyCarts.PLOW_MENU_TYPE, PlowScreen::new);

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

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (actionKeyMapping.consumeClick()) {
				ClientPlayNetworking.send(new ActionKeyPayload());
			}
			var player = client.player;
			if (player != null && ToggleSlowPayload.getCart(player).isPresent()) {
				while (toggleSlowMapping.consumeClick()) {
					ClientPlayNetworking.send(new ToggleSlowPayload());
					KeyMapping.set(toggleSlowMapping.getDefaultKey(), false);
				}
			}
			if (!client.isPaused() && client.level != null) {
				NiftyWorld.getClient().tick();
			}
		});
	}
}