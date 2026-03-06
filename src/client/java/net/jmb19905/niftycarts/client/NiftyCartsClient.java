package net.jmb19905.niftycarts.client;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.InputConstants;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsConfig;
import net.jmb19905.niftycarts.client.renderer.NiftyCartsModelLayers;
import net.jmb19905.niftycarts.client.renderer.entity.*;
import net.jmb19905.niftycarts.client.renderer.entity.model.*;
import net.jmb19905.niftycarts.client.screen.ChestScreen;
import net.jmb19905.niftycarts.client.screen.PlowScreen;
import net.jmb19905.niftycarts.client.screen.SeedDrillScreen;
import net.jmb19905.niftycarts.item.CartItem;
import net.jmb19905.niftycarts.network.clientbound.UpdateDrawnPayload;
import net.jmb19905.niftycarts.network.serverbound.ActionKeyPayload;
import net.jmb19905.niftycarts.network.serverbound.ToggleSlowPayload;
import net.jmb19905.niftycarts.util.NiftyWorld;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class NiftyCartsClient implements ClientModInitializer {

    public static final ImmutableMap<@NotNull WoodType, @NotNull String> LOG_NAME_OVERRIDE = ImmutableMap.of(
            WoodType.CRIMSON, "stem",
            WoodType.WARPED, "stem",
            WoodType.BAMBOO, "block"
    );

	private static KeyMapping actionKeyMapping;
	public static KeyMapping toggleSlowMapping;

	@Override
	public void onInitializeClient() {
		ConfigRegistry.INSTANCE.register(NiftyCarts.MOD_ID, ModConfig.Type.CLIENT, NiftyCartsConfig.clientSpec());

		ClientPlayNetworking.registerGlobalReceiver(UpdateDrawnPayload.TYPE, (payload, ctx) -> ctx.client().execute(() -> UpdateDrawnPayload.handle(payload, Objects.requireNonNull(ctx.client().level))));
        EntityRenderers.register(NiftyCarts.SUPPLY_CART_ENTITY, SupplyCartRenderer::new);
        EntityRenderers.register(NiftyCarts.ANIMAL_CART_ENTITY, AnimalCartRenderer::new);
        EntityRenderers.register(NiftyCarts.PLOW_ENTITY, PlowRenderer::new);
        EntityRenderers.register(NiftyCarts.HAND_CART_ENTITY, HandCartRenderer::new);
        EntityRenderers.register(NiftyCarts.SEED_DRILL_ENTITY, SeedDrillRenderer::new);
        EntityRenderers.register(NiftyCarts.REAPER_ENTITY, ReaperRenderer::new);
        EntityRenderers.register(NiftyCarts.WAGON_ENTITY, WagonRenderer::new);
        EntityRenderers.register(NiftyCarts.POSTILION_ENTITY, PostilionRenderer::new);

		EntityModelLayerRegistry.registerModelLayer(NiftyCartsModelLayers.SUPPLY_CART, SupplyCartModel::createLayer);
		EntityModelLayerRegistry.registerModelLayer(NiftyCartsModelLayers.ANIMAL_CART, AnimalCartModel::createLayer);
		EntityModelLayerRegistry.registerModelLayer(NiftyCartsModelLayers.PLOW, PlowModel::createLayer);
		EntityModelLayerRegistry.registerModelLayer(NiftyCartsModelLayers.HAND_CART, HandCartModel::createLayer);
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

        KeyMapping.Category keyCategory = KeyMapping.Category.register(NiftyCarts.resLoc("niftycarts"));

		actionKeyMapping = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.niftycarts.action",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_R,
                keyCategory
		));

		toggleSlowMapping = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.niftycarts.slow",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_Z,
                keyCategory
		));

        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) -> {
            if (stack.getItem() instanceof CartItem cart) {
                if (!Minecraft.getInstance().hasShiftDown()) {
                    lines.add(Component.translatable("item.cart.press_shift_tooltip").withStyle(ChatFormatting.GRAY));
                }
                else {
                    lines.add(Component.translatable("item." + cart.getCartType() + ".tooltip1").withStyle(ChatFormatting.GRAY));
                    lines.add(Component.translatable("item." + cart.getCartType() + ".tooltip2").withStyle(ChatFormatting.GRAY));
                }
            }
        });

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (actionKeyMapping.consumeClick()) {
				ClientPlayNetworking.send(new ActionKeyPayload());
			}
			var player = client.player;
            if (player != null) {
                while (toggleSlowMapping.consumeClick()) {
                    if (player.getControlledVehicle() != null && ToggleSlowPayload.isSlowable(player.getControlledVehicle())) {
                        if (!ToggleSlowPayload.isSlow(player.getControlledVehicle())) {
                            player.displayClientMessage(Component.translatable("message.niftycarts.slow_toggled_on", toggleSlowMapping.getTranslatedKeyMessage()), true);
                        } else {
                            player.displayClientMessage(Component.translatable("message.niftycarts.slow_toggled_off", toggleSlowMapping.getTranslatedKeyMessage()), true);
                        }
                    }
                    ClientPlayNetworking.send(new ToggleSlowPayload());
                    KeyMapping.set(toggleSlowMapping.getDefaultKey(), false);
                }
            }
			if (!client.isPaused() && client.level != null) {
				NiftyWorld.getClient().tick(client.level);
			}
		});
	}
}