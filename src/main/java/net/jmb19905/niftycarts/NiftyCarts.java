package net.jmb19905.niftycarts;

import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.jmb19905.niftycarts.container.PlowMenu;
import net.jmb19905.niftycarts.entity.*;
import net.jmb19905.niftycarts.entity.ai.goal.AvoidCartGoal;
import net.jmb19905.niftycarts.entity.ai.goal.PullCartGoal;
import net.jmb19905.niftycarts.entity.ai.goal.RideCartGoal;
import net.jmb19905.niftycarts.item.CartItem;
import net.jmb19905.niftycarts.network.clientbound.UpdateDrawnPayload;
import net.jmb19905.niftycarts.network.serverbound.*;
import net.jmb19905.niftycarts.util.NiftyWorld;
import net.jmb19905.niftycarts.util.GoalAdder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.Supplier;

public class NiftyCarts implements ModInitializer {
	public static final String MOD_ID = "niftycarts";

	public static final Item WHEEL = new Item(new Item.Properties());
	private static final Supplier<CartItem> CART_ITEM_SUPPLIER = () -> new CartItem(new Item.Properties().stacksTo(1));
	public static final CartItem SUPPLY_CART = CART_ITEM_SUPPLIER.get();
	public static final CartItem HAND_CART = CART_ITEM_SUPPLIER.get();
	public static final CartItem PLOW = CART_ITEM_SUPPLIER.get();
	public static final CartItem ANIMAL_CART = CART_ITEM_SUPPLIER.get();

	public static MinecraftServer server = null;

	public static final ResourceLocation ATTACH_SOUND_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "entity.cart.attach");
	public static final ResourceLocation DETACH_SOUND_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "entity.cart.detach");
	public static final ResourceLocation PLACE_SOUND_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "entity.cart.place");

	public static SoundEvent ATTACH_SOUND = SoundEvent.createVariableRangeEvent(ATTACH_SOUND_ID);
	public static SoundEvent DETACH_SOUND = SoundEvent.createVariableRangeEvent(DETACH_SOUND_ID);
	public static SoundEvent PLACE_SOUND = SoundEvent.createVariableRangeEvent(PLACE_SOUND_ID);

	public static final EntityType<SupplyCartEntity> SUPPLY_CART_ENTITY = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			ResourceLocation.fromNamespaceAndPath(MOD_ID, "supply_cart"),
			EntityType.Builder.of(SupplyCartEntity::new, MobCategory.MISC).sized(1.5f, 1.4f).build()
	);

	public static final EntityType<AnimalCartEntity> ANIMAL_CART_ENTITY = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			ResourceLocation.fromNamespaceAndPath(MOD_ID, "animal_cart"),
			EntityType.Builder.of(AnimalCartEntity::new, MobCategory.MISC).sized(1.3f, 1.4f).build()
	);

	public static final EntityType<PlowEntity> PLOW_ENTITY = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			ResourceLocation.fromNamespaceAndPath(MOD_ID, "plow"),
			EntityType.Builder.of(PlowEntity::new, MobCategory.MISC).sized(1.3f, 1.4f).build()
	);

	public static final EntityType<HandCartEntity> HAND_CART_ENTITY = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			ResourceLocation.fromNamespaceAndPath(MOD_ID, "hand_cart"),
			EntityType.Builder.of(HandCartEntity::new, MobCategory.MISC).sized(1.3f, 1.1f).build()
	);

	public static final EntityType<PostilionEntity> POSTILION_ENTITY = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			ResourceLocation.fromNamespaceAndPath(MOD_ID, "postilion"),
			EntityType.Builder.of(PostilionEntity::new, MobCategory.MISC)
					.sized(0.25f, 0.25f)
					.noSummon()
					.noSave()
					.build()
	);

	public static final GoalAdder<Mob> MOB_GOAL_ADDER = GoalAdder.mobGoal(Mob.class)
			.add(1, PullCartGoal::new)
			.add(1, RideCartGoal::new)
			.build();

	public static final GoalAdder<PathfinderMob> PATHFINDER_GOAL_ADDER = GoalAdder.mobGoal(PathfinderMob.class)
			.add(3, mob -> new AvoidCartGoal<>(mob, SupplyCartEntity.class, 3.0f, 0.5f))
			.add(3, mob -> new AvoidCartGoal<>(mob, PlowEntity.class, 3.0f, 0.5f))
			.build();

	public static final MenuType<PlowMenu> PLOW_MENU_TYPE = new MenuType<>(PlowMenu::new, FeatureFlags.DEFAULT_FLAGS);

	public static final ResourceLocation CART_ONE_CM = ResourceLocation.fromNamespaceAndPath(MOD_ID, "cart_one_cm");

	public static final TagKey<Block> PLOW_BREAKABLE_HOE = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(NiftyCarts.MOD_ID, "plow_breakable/hoe"));
	public static final TagKey<Block> PLOW_BREAKABLE_SHOVEL = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(NiftyCarts.MOD_ID, "plow_breakable/shovel"));
	public static final TagKey<Block> PLOW_BREAKABLE_AXE = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(NiftyCarts.MOD_ID, "plow_breakable/axe"));

	@Override
	public void onInitialize() {
		ForgeConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.COMMON, NiftyCartsConfig.spec());

		Registry.register(BuiltInRegistries.CUSTOM_STAT, CART_ONE_CM, CART_ONE_CM);
		Stats.CUSTOM.get(CART_ONE_CM, StatFormatter.DEFAULT);

		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "wheel"), WHEEL);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "supply_cart"), SUPPLY_CART);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "plow"), PLOW);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "animal_cart"), ANIMAL_CART);
		Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "hand_cart"), HAND_CART);

		Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath(MOD_ID, "plow"), PLOW_MENU_TYPE);

		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(content -> content.accept(WHEEL));
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> {
			content.accept(SUPPLY_CART);
			content.accept(PLOW);
			content.accept(ANIMAL_CART);
			content.accept(HAND_CART);
		});

		Registry.register(BuiltInRegistries.SOUND_EVENT, ATTACH_SOUND_ID, ATTACH_SOUND);
		Registry.register(BuiltInRegistries.SOUND_EVENT, DETACH_SOUND_ID, DETACH_SOUND);
		Registry.register(BuiltInRegistries.SOUND_EVENT, PLACE_SOUND_ID, PLACE_SOUND);

		PayloadTypeRegistry.playC2S().register(ActionKeyPayload.TYPE, ActionKeyPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(OpenSupplyCartPayload.TYPE, OpenSupplyCartPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(ToggleSlowPayload.TYPE, ToggleSlowPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(RequestCartUpdatePayload.TYPE, RequestCartUpdatePayload.CODEC);

		PayloadTypeRegistry.playS2C().register(UpdateDrawnPayload.TYPE, UpdateDrawnPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(ActionKeyPayload.TYPE, (payload, context) -> ActionKeyPayload.handle(context.player()));
		ServerPlayNetworking.registerGlobalReceiver(OpenSupplyCartPayload.TYPE, (payload, context) -> OpenSupplyCartPayload.handle(context.player()));
		ServerPlayNetworking.registerGlobalReceiver(ToggleSlowPayload.TYPE, (payload, context) -> ToggleSlowPayload.handle(context.player()));
		ServerPlayNetworking.registerGlobalReceiver(RequestCartUpdatePayload.TYPE, (payload, context) -> RequestCartUpdatePayload.handle(payload, context.player()));

		ServerLifecycleEvents.SERVER_STARTED.register(s -> server = s);

		ServerTickEvents.END_SERVER_TICK.register(e -> {
			for (ResourceKey<Level> levelKey : e.levelKeys()) {
				NiftyWorld.getServer(server, levelKey).tick();
			}
		});

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

		//noinspection DataFlowIssue
		FabricDefaultAttributeRegistry.register(POSTILION_ENTITY, LivingEntity.createLivingAttributes());
	}
}