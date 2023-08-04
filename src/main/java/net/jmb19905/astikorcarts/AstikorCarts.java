package net.jmb19905.astikorcarts;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.jmb19905.astikorcarts.container.PlowMenu;
import net.jmb19905.astikorcarts.entity.AnimalCartEntity;
import net.jmb19905.astikorcarts.entity.PlowEntity;
import net.jmb19905.astikorcarts.entity.PostilionEntity;
import net.jmb19905.astikorcarts.entity.SupplyCartEntity;
import net.jmb19905.astikorcarts.entity.ai.goal.AvoidCartGoal;
import net.jmb19905.astikorcarts.entity.ai.goal.PullCartGoal;
import net.jmb19905.astikorcarts.entity.ai.goal.RideCartGoal;
import net.jmb19905.astikorcarts.item.CartItem;
import net.jmb19905.astikorcarts.network.serverbound.ActionKeyMessage;
import net.jmb19905.astikorcarts.network.serverbound.OpenSupplyCartMessage;
import net.jmb19905.astikorcarts.network.serverbound.RequestCartUpdate;
import net.jmb19905.astikorcarts.network.serverbound.ToggleSlowMessage;
import net.jmb19905.astikorcarts.util.AstikorWorld;
import net.jmb19905.astikorcarts.util.GoalAdder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.Supplier;

public class AstikorCarts implements ModInitializer {
	public static final String MOD_ID = "astikorcarts";

	public static final Item WHEEL = new Item(new FabricItemSettings());
	private static final Supplier<CartItem> CART_ITEM_SUPPLIER = () -> new CartItem(new FabricItemSettings().maxCount(1));
	public static final CartItem SUPPLY_CART = CART_ITEM_SUPPLIER.get();
	public static final CartItem PLOW = CART_ITEM_SUPPLIER.get();
	public static final CartItem ANIMAL_CART = CART_ITEM_SUPPLIER.get();

	public static MinecraftServer server = null;

	public static final ResourceLocation ATTACH_SOUND_ID = new ResourceLocation(MOD_ID, "entity.cart.attach");
	public static final ResourceLocation DETACH_SOUND_ID = new ResourceLocation(MOD_ID, "entity.cart.detach");
	public static final ResourceLocation PLACE_SOUND_ID = new ResourceLocation(MOD_ID, "entity.cart.place");

	public static SoundEvent ATTACH_SOUND = SoundEvent.createVariableRangeEvent(ATTACH_SOUND_ID);
	public static SoundEvent DETACH_SOUND = SoundEvent.createVariableRangeEvent(DETACH_SOUND_ID);
	public static SoundEvent PLACE_SOUND = SoundEvent.createVariableRangeEvent(PLACE_SOUND_ID);

	public static final EntityType<SupplyCartEntity> SUPPLY_CART_ENTITY = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			new ResourceLocation(MOD_ID, "supply_cart"),
			FabricEntityTypeBuilder.create(MobCategory.MISC, SupplyCartEntity::new).dimensions(EntityDimensions.fixed(1.5f, 1.4f)).build()
	);

	public static final EntityType<AnimalCartEntity> ANIMAL_CART_ENTITY = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			new ResourceLocation(MOD_ID, "animal_cart"),
			FabricEntityTypeBuilder.create(MobCategory.MISC, AnimalCartEntity::new).dimensions(EntityDimensions.fixed(1.3f, 1.4f)).build()
	);

	public static final EntityType<PlowEntity> PLOW_ENTITY = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			new ResourceLocation(MOD_ID, "plow"),
			FabricEntityTypeBuilder.create(MobCategory.MISC, PlowEntity::new).dimensions(EntityDimensions.fixed(1.3f, 1.4f)).build()
	);

	public static final EntityType<PostilionEntity> POSTILION_ENTITY = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			new ResourceLocation(MOD_ID, "postilion"),
			FabricEntityTypeBuilder.create(MobCategory.MISC, PostilionEntity::new)
					.dimensions(EntityDimensions.fixed(0.25f, 0.25f))
					.disableSummon()
					.disableSaving()
					.build()
	);

	public static final ResourceLocation ACTION_KEY_MESSAGE_ID = new ResourceLocation(AstikorCarts.MOD_ID, "action_key");
	public static final ResourceLocation TOGGLE_SLOW_MESSAGE_ID = new ResourceLocation(AstikorCarts.MOD_ID, "toggle_slow");
	public static final ResourceLocation UPDATE_DRAWN_MESSAGE_ID = new ResourceLocation(AstikorCarts.MOD_ID, "update_drawn");
	public static final ResourceLocation REQUEST_CART_UPDATE_MESSAGE_ID = new ResourceLocation(AstikorCarts.MOD_ID, "request_cart_update");
	public static final ResourceLocation OPEN_SUPPLY_MESSAGE_ID = new ResourceLocation(AstikorCarts.MOD_ID, "open_supply");

	public static final GoalAdder<Mob> MOB_GOAL_ADDER = GoalAdder.mobGoal(Mob.class)
			.add(1, PullCartGoal::new)
			.add(1, RideCartGoal::new)
			.build();

	public static final GoalAdder<PathfinderMob> PATHFINDER_GOAL_ADDER = GoalAdder.mobGoal(PathfinderMob.class)
			.add(3, mob -> new AvoidCartGoal<>(mob, SupplyCartEntity.class, 3.0f, 0.5f))
			.add(3, mob -> new AvoidCartGoal<>(mob, PlowEntity.class, 3.0f, 0.5f))
			.build();

	public static final MenuType<PlowMenu> PLOW_MENU_TYPE = new MenuType<>(PlowMenu::new, FeatureFlags.DEFAULT_FLAGS);

	public static final ResourceLocation CART_ONE_CM = new ResourceLocation(MOD_ID, "cart_one_cm");

	@Override
	public void onInitialize() {
		ForgeConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.COMMON, AstikorCartsConfig.spec());

		Registry.register(BuiltInRegistries.CUSTOM_STAT, CART_ONE_CM, CART_ONE_CM);
		Stats.CUSTOM.get(CART_ONE_CM, StatFormatter.DEFAULT);

		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "wheel"), WHEEL);
		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "supply_cart"), SUPPLY_CART);
		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "plow"), PLOW);
		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "animal_cart"), ANIMAL_CART);

		Registry.register(BuiltInRegistries.MENU, new ResourceLocation(MOD_ID, "plow"), PLOW_MENU_TYPE);

		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(content -> content.accept(WHEEL));
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> {
			content.accept(SUPPLY_CART);
			content.accept(PLOW);
			content.accept(ANIMAL_CART);
		});

		Registry.register(BuiltInRegistries.SOUND_EVENT, ATTACH_SOUND_ID, ATTACH_SOUND);
		Registry.register(BuiltInRegistries.SOUND_EVENT, DETACH_SOUND_ID, DETACH_SOUND);
		Registry.register(BuiltInRegistries.SOUND_EVENT, PLACE_SOUND_ID, PLACE_SOUND);

		ServerPlayNetworking.registerGlobalReceiver(ACTION_KEY_MESSAGE_ID, (server, player, handler, buf, responseSender) -> ActionKeyMessage.handle(null, player));
		ServerPlayNetworking.registerGlobalReceiver(TOGGLE_SLOW_MESSAGE_ID, (server, player, handler, buf, responseSender) -> ToggleSlowMessage.handle(player));
		ServerPlayNetworking.registerGlobalReceiver(OPEN_SUPPLY_MESSAGE_ID, (server, player, handler, buf, responseSender) -> OpenSupplyCartMessage.handle(player));
		ServerPlayNetworking.registerGlobalReceiver(REQUEST_CART_UPDATE_MESSAGE_ID, (server, player, handler, buf, responseSender) -> {
			RequestCartUpdate msg = new RequestCartUpdate();
			msg.decode(buf);
			RequestCartUpdate.handle(msg, player);
		});

		ServerLifecycleEvents.SERVER_STARTED.register(s -> server = s);

		ServerTickEvents.END_SERVER_TICK.register(e -> {
			for (ResourceKey<Level> levelKey : e.levelKeys()) {
				AstikorWorld.getServer(server, levelKey).tick();
			}
		});

		FabricDefaultAttributeRegistry.register(POSTILION_ENTITY, LivingEntity.createLivingAttributes().build());

		UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
			final Entity rider = entity.getControllingPassenger();
			if (rider instanceof PostilionEntity) {
				rider.stopRiding();
			}
			return InteractionResult.PASS;
		});

		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			MOB_GOAL_ADDER.onEntityJoinWorld(entity);
			PATHFINDER_GOAL_ADDER.onEntityJoinWorld(entity);
		});
	}
}