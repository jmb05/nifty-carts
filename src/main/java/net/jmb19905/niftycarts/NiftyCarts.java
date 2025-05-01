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
import net.jmb19905.niftycarts.container.SeedDrillMenu;
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
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.fml.config.ModConfig;
import org.apache.commons.lang3.function.TriFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class NiftyCarts implements ModInitializer {
	public static final String MOD_ID = "niftycarts";

	public static final Item WHEEL = register("wheel", Item::new);
	private static final TriFunction<WoodType, String, FeatureFlag[], CartItem> CART_ITEM_SUPPLIER = (wood, type, flags) -> register(wood.name() + "_" + type, prop -> new CartItem(wood, type, prop.stacksTo(1).requiredFeatures(flags)));
	public static final Map<WoodType, CartItem> SUPPLY_CART = new HashMap<>();
	public static final Map<WoodType, CartItem> HAND_CART = new HashMap<>();
	public static final Map<WoodType, CartItem> PLOW = new HashMap<>();
	public static final Map<WoodType, CartItem> ANIMAL_CART = new HashMap<>();
	public static final Map<WoodType, CartItem> SEED_DRILL = new HashMap<>();
	public static final Map<WoodType, CartItem> REAPER = new HashMap<>();

	static {
		WoodType.values().forEach(woodType -> {
			FeatureFlag[] flags = {};
			SUPPLY_CART.put(woodType, CART_ITEM_SUPPLIER.apply(woodType, "supply_cart", flags));
			HAND_CART.put(woodType, CART_ITEM_SUPPLIER.apply(woodType, "hand_cart", flags));
			PLOW.put(woodType, CART_ITEM_SUPPLIER.apply(woodType, "plow", flags));
			SEED_DRILL.put(woodType, CART_ITEM_SUPPLIER.apply(woodType, "seed_drill", flags));
			REAPER.put(woodType, CART_ITEM_SUPPLIER.apply(woodType, "reaper", flags));
			ANIMAL_CART.put(woodType, CART_ITEM_SUPPLIER.apply(woodType, "animal_cart", flags));
		});
	}

	public static MinecraftServer server = null;

	public static final ResourceLocation ATTACH_SOUND_ID = resLoc("entity.cart.attach");
	public static final ResourceLocation DETACH_SOUND_ID = resLoc("entity.cart.detach");
	public static final ResourceLocation PLACE_SOUND_ID = resLoc("entity.cart.place");

	public static SoundEvent ATTACH_SOUND = SoundEvent.createVariableRangeEvent(ATTACH_SOUND_ID);
	public static SoundEvent DETACH_SOUND = SoundEvent.createVariableRangeEvent(DETACH_SOUND_ID);
	public static SoundEvent PLACE_SOUND = SoundEvent.createVariableRangeEvent(PLACE_SOUND_ID);

	public static final EntityType<SupplyCartEntity> SUPPLY_CART_ENTITY = register("supply_cart",
			EntityType.Builder.of(SupplyCartEntity::new, MobCategory.MISC).sized(1.5f, 1.4f));

	public static final EntityType<AnimalCartEntity> ANIMAL_CART_ENTITY = register("animal_cart",
			EntityType.Builder.of(AnimalCartEntity::new, MobCategory.MISC).sized(1.3f, 1.4f));

	public static final EntityType<PlowEntity> PLOW_ENTITY = register("plow",
			EntityType.Builder.of(PlowEntity::new, MobCategory.MISC).sized(1.3f, 1.4f));

	public static final EntityType<HandCartEntity> HAND_CART_ENTITY = register("hand_cart",
			EntityType.Builder.of(HandCartEntity::new, MobCategory.MISC).sized(1.3f, 1.1f));

	public static final EntityType<SeedDrillEntity> SEED_DRILL_ENTITY = register("seed_drill",
			EntityType.Builder.of(SeedDrillEntity::new, MobCategory.MISC).sized(1.3f, 1.4f));

	public static final EntityType<ReaperCartEntity> REAPER_ENTITY = register("reaper",
			EntityType.Builder.of(ReaperCartEntity::new, MobCategory.MISC).sized(1.3f, 1.4f));

	public static final EntityType<PostilionEntity> POSTILION_ENTITY = register("postilion",
			EntityType.Builder.of(PostilionEntity::new, MobCategory.MISC)
					.sized(0.25f, 0.25f)
					.noSummon()
					.noSave());

	public static final GoalAdder<Mob> MOB_GOAL_ADDER = GoalAdder.mobGoal(Mob.class)
			.add(1, PullCartGoal::new)
			.add(1, RideCartGoal::new)
			.build();

	public static final GoalAdder<PathfinderMob> PATHFINDER_GOAL_ADDER = GoalAdder.mobGoal(PathfinderMob.class)
			.add(3, mob -> new AvoidCartGoal<>(mob, SupplyCartEntity.class, 3.0f, 0.5f))
			.add(3, mob -> new AvoidCartGoal<>(mob, PlowEntity.class, 3.0f, 0.5f))
			.build();

	public static final MenuType<PlowMenu> PLOW_MENU_TYPE = new MenuType<>(PlowMenu::new, FeatureFlags.DEFAULT_FLAGS);
	public static final MenuType<SeedDrillMenu> SEED_DRILL_MENU_TYPE = new MenuType<>(SeedDrillMenu::new, FeatureFlags.DEFAULT_FLAGS);

	public static final ResourceLocation CART_ONE_CM = resLoc("cart_one_cm");

	public static final TagKey<Block> PLOW_BREAKABLE_HOE = TagKey.create(Registries.BLOCK, NiftyCarts.resLoc("plow_breakable/hoe"));
	public static final TagKey<Block> PLOW_BREAKABLE_SHOVEL = TagKey.create(Registries.BLOCK, NiftyCarts.resLoc("plow_breakable/shovel"));
	public static final TagKey<Block> PLOW_BREAKABLE_AXE = TagKey.create(Registries.BLOCK, NiftyCarts.resLoc("plow_breakable/axe"));
	public static final TagKey<Item> SEED_DRILL_PLANTABLE = TagKey.create(Registries.ITEM, NiftyCarts.resLoc("seed_drill_plantable"));

	@Override
	public void onInitialize() {
		ForgeConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.COMMON, NiftyCartsConfig.spec());

		Registry.register(BuiltInRegistries.CUSTOM_STAT, CART_ONE_CM, CART_ONE_CM);
		Stats.CUSTOM.get(CART_ONE_CM, StatFormatter.DEFAULT);

		Registry.register(BuiltInRegistries.MENU, resLoc("plow"), PLOW_MENU_TYPE);
		Registry.register(BuiltInRegistries.MENU, resLoc("seed_drill"), SEED_DRILL_MENU_TYPE);

		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(content -> content.accept(WHEEL));
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> WoodType.values().forEach(woodType -> {
            content.accept(SUPPLY_CART.get(woodType));
            content.accept(PLOW.get(woodType));
            content.accept(SEED_DRILL.get(woodType));
            content.accept(REAPER.get(woodType));
            content.accept(ANIMAL_CART.get(woodType));
            content.accept(HAND_CART.get(woodType));
        }));

		Registry.register(BuiltInRegistries.SOUND_EVENT, ATTACH_SOUND_ID, ATTACH_SOUND);
		Registry.register(BuiltInRegistries.SOUND_EVENT, DETACH_SOUND_ID, DETACH_SOUND);
		Registry.register(BuiltInRegistries.SOUND_EVENT, PLACE_SOUND_ID, PLACE_SOUND);

		PayloadTypeRegistry.playC2S().register(ActionKeyPayload.TYPE, ActionKeyPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(OpenSupplyCartPayload.TYPE, OpenSupplyCartPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(ToggleSlowPayload.TYPE, ToggleSlowPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(RequestCartUpdatePayload.TYPE, RequestCartUpdatePayload.CODEC);
		PayloadTypeRegistry.playC2S().register(CoachmanMovePayload.TYPE, CoachmanMovePayload.CODEC);

		PayloadTypeRegistry.playS2C().register(UpdateDrawnPayload.TYPE, UpdateDrawnPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(ActionKeyPayload.TYPE, (payload, context) -> ActionKeyPayload.handle(context.player()));
		ServerPlayNetworking.registerGlobalReceiver(OpenSupplyCartPayload.TYPE, (payload, context) -> OpenSupplyCartPayload.handle(context.player()));
		ServerPlayNetworking.registerGlobalReceiver(ToggleSlowPayload.TYPE, (payload, context) -> ToggleSlowPayload.handle(context.player()));
		ServerPlayNetworking.registerGlobalReceiver(RequestCartUpdatePayload.TYPE, (payload, context) -> RequestCartUpdatePayload.handle(payload, context.player()));
		ServerPlayNetworking.registerGlobalReceiver(CoachmanMovePayload.TYPE, (payload, context) -> CoachmanMovePayload.handle(payload, context.player()));

		ServerLifecycleEvents.SERVER_STARTED.register(s -> server = s);
		ServerLifecycleEvents.SERVER_STOPPED.register(s -> server = null);

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

	public static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> builder) {
		ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, resLoc(id));
		return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
	}

	public static <I extends Item> I register(String id, Function<Item.Properties, I> function) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, resLoc(id));
		I item = function.apply(new Item.Properties().setId(key));
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	public static ResourceLocation resLoc(String name) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
	}

}