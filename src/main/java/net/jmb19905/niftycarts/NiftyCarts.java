package net.jmb19905.niftycarts;

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
import net.jmb19905.niftycarts.container.PlowMenu;
import net.jmb19905.niftycarts.container.SeedDrillMenu;
import net.jmb19905.niftycarts.entity.*;
import net.jmb19905.niftycarts.entity.ai.goal.AvoidCartGoal;
import net.jmb19905.niftycarts.entity.ai.goal.PullCartGoal;
import net.jmb19905.niftycarts.entity.ai.goal.RideCartGoal;
import net.jmb19905.niftycarts.item.CartItem;
import net.jmb19905.niftycarts.network.serverbound.ActionKeyMessage;
import net.jmb19905.niftycarts.network.serverbound.OpenSupplyCartMessage;
import net.jmb19905.niftycarts.network.serverbound.RequestCartUpdate;
import net.jmb19905.niftycarts.network.serverbound.ToggleSlowMessage;
import net.jmb19905.niftycarts.util.GoalAdder;
import net.jmb19905.niftycarts.util.NiftyWorld;
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
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.*;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.config.ModConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class NiftyCarts implements ModInitializer {
	public static final String MOD_ID = "niftycarts";

	public static final Item WHEEL = new Item(new FabricItemSettings());
	private static final BiFunction<NiftyCartsWoodType, String, CartItem> CART_ITEM_SUPPLIER = (woodType, type) -> new CartItem(woodType, type, new FabricItemSettings().maxCount(1));
	public static final Map<NiftyCartsWoodType, CartItem> SUPPLY_CART = new HashMap<>();
	public static final Map<NiftyCartsWoodType, CartItem> HAND_CART = new HashMap<>();
	public static final Map<NiftyCartsWoodType, CartItem> PLOW = new HashMap<>();
	public static final Map<NiftyCartsWoodType, CartItem> SEED_DRILL = new HashMap<>();
	public static final Map<NiftyCartsWoodType, CartItem> REAPER = new HashMap<>();
	public static final Map<NiftyCartsWoodType, CartItem> ANIMAL_CART = new HashMap<>();
    public static final Map<NiftyCartsWoodType, CartItem> WAGON = new HashMap<>();

	static {
		for (NiftyCartsWoodType woodType : NiftyCartsWoodType.values()) {
			SUPPLY_CART.put(woodType, CART_ITEM_SUPPLIER.apply(woodType, "supply_cart"));
			HAND_CART.put(woodType, CART_ITEM_SUPPLIER.apply(woodType, "hand_cart"));
			PLOW.put(woodType, CART_ITEM_SUPPLIER.apply(woodType, "plow"));
			SEED_DRILL.put(woodType, CART_ITEM_SUPPLIER.apply(woodType, "seed_drill"));
			REAPER.put(woodType, CART_ITEM_SUPPLIER.apply(woodType, "reaper"));
			ANIMAL_CART.put(woodType, CART_ITEM_SUPPLIER.apply(woodType, "animal_cart"));
            WAGON.put(woodType, CART_ITEM_SUPPLIER.apply(woodType, "wagon"));
		}
	}

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

	public static final EntityType<HandCartEntity> HAND_CART_ENTITY = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			new ResourceLocation(MOD_ID, "hand_cart"),
			FabricEntityTypeBuilder.create(MobCategory.MISC, HandCartEntity::new).dimensions(EntityDimensions.fixed(1.3f, 1.1f)).build()
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
	
	public static final EntityType<SeedDrillEntity> SEED_DRILL_ENTITY = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			new ResourceLocation(MOD_ID, "seed_drill"),
			FabricEntityTypeBuilder.create(MobCategory.MISC, SeedDrillEntity::new).dimensions(EntityDimensions.fixed(1.3f, 1.4f)).build()
	);

	public static final EntityType<ReaperCartEntity> REAPER_ENTITY = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			new ResourceLocation(MOD_ID, "reaper"),
			FabricEntityTypeBuilder.create(MobCategory.MISC, ReaperCartEntity::new).dimensions(EntityDimensions.fixed(1.3f, 1.4f)).build()
	);

    public static final EntityType<WagonEntity> WAGON_ENTITY = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "wagon"),
            FabricEntityTypeBuilder.create(MobCategory.MISC, WagonEntity::new).dimensions(EntityDimensions.fixed(2.5f, 3f)).build()
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

	public static final ResourceLocation ACTION_KEY_MESSAGE_ID = new ResourceLocation(NiftyCarts.MOD_ID, "action_key");
	public static final ResourceLocation TOGGLE_SLOW_MESSAGE_ID = new ResourceLocation(NiftyCarts.MOD_ID, "toggle_slow");
	public static final ResourceLocation UPDATE_DRAWN_MESSAGE_ID = new ResourceLocation(NiftyCarts.MOD_ID, "update_drawn");
	public static final ResourceLocation REQUEST_CART_UPDATE_MESSAGE_ID = new ResourceLocation(NiftyCarts.MOD_ID, "request_cart_update");
	public static final ResourceLocation OPEN_SUPPLY_MESSAGE_ID = new ResourceLocation(NiftyCarts.MOD_ID, "open_supply");

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
    public static final MenuType<ChestMenu> CHEST_9x4_MENU_TYPE = new MenuType<>((i, inv) -> new ChestMenu(NiftyCarts.CHEST_9x4_MENU_TYPE, i, inv, new SimpleContainer(9 * 4), 4), FeatureFlags.DEFAULT_FLAGS);
    public static final MenuType<ChestMenu> CHEST_9x8_MENU_TYPE = new MenuType<>((i, inv) -> new ChestMenu(NiftyCarts.CHEST_9x8_MENU_TYPE, i, inv, new SimpleContainer(9 * 8), 8), FeatureFlags.DEFAULT_FLAGS);
    public static final MenuType<ChestMenu> CHEST_9x12_MENU_TYPE = new MenuType<>((i, inv) -> new ChestMenu(NiftyCarts.CHEST_9x12_MENU_TYPE, i, inv, new SimpleContainer(9 * 12), 12), FeatureFlags.DEFAULT_FLAGS);

	public static final ResourceLocation CART_ONE_CM = new ResourceLocation(MOD_ID, "cart_one_cm");

	public static final TagKey<Block> PLOW_BREAKABLE_HOE = TagKey.create(Registries.BLOCK, new ResourceLocation(NiftyCarts.MOD_ID, "plow_breakable/hoe"));
	public static final TagKey<Block> PLOW_BREAKABLE_SHOVEL = TagKey.create(Registries.BLOCK, new ResourceLocation(NiftyCarts.MOD_ID, "plow_breakable/shovel"));
	public static final TagKey<Block> PLOW_BREAKABLE_AXE = TagKey.create(Registries.BLOCK, new ResourceLocation(NiftyCarts.MOD_ID, "plow_breakable/axe"));
    public static final TagKey<Block> REAPER_HARVESTABLE = TagKey.create(Registries.BLOCK, new ResourceLocation(NiftyCarts.MOD_ID, "reaper_harvestable"));
    public static final TagKey<Item> SEED_DRILL_PLANTABLE = TagKey.create(Registries.ITEM, new ResourceLocation(NiftyCarts.MOD_ID, "seed_drill_plantable"));

	@Override
	public void onInitialize() {
		ForgeConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.COMMON, NiftyCartsConfig.spec());

		Registry.register(BuiltInRegistries.CUSTOM_STAT, CART_ONE_CM, CART_ONE_CM);
		Stats.CUSTOM.get(CART_ONE_CM, StatFormatter.DEFAULT);
		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "wheel"), WHEEL);
		for (NiftyCartsWoodType woodType : NiftyCartsWoodType.values()) {
			Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, woodType.getId() + "_supply_cart"), SUPPLY_CART.get(woodType));
			Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, woodType.getId() + "_hand_cart"), HAND_CART.get(woodType));
			Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, woodType.getId() + "_plow"), PLOW.get(woodType));
			Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, woodType.getId() + "_seed_drill"), SEED_DRILL.get(woodType));
			Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, woodType.getId() + "_reaper"), REAPER.get(woodType));
			Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, woodType.getId() + "_animal_cart"), ANIMAL_CART.get(woodType));
            Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, woodType.getId() + "_wagon"), WAGON.get(woodType));
		}

		Registry.register(BuiltInRegistries.MENU, new ResourceLocation(MOD_ID, "plow"), PLOW_MENU_TYPE);
		Registry.register(BuiltInRegistries.MENU, new ResourceLocation(MOD_ID, "seed_drill"), SEED_DRILL_MENU_TYPE);
        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(MOD_ID, "chest_four_rows"), CHEST_9x4_MENU_TYPE);
        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(MOD_ID, "chest_eight_rows"), CHEST_9x8_MENU_TYPE);
        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(MOD_ID, "chest_quad"), CHEST_9x12_MENU_TYPE);

		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(content -> content.accept(WHEEL));
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> {
			for (NiftyCartsWoodType woodType : NiftyCartsWoodType.values()) {
				content.accept(SUPPLY_CART.get(woodType));
				content.accept(HAND_CART.get(woodType));
				content.accept(PLOW.get(woodType));
				content.accept(SEED_DRILL.get(woodType));
				content.accept(REAPER.get(woodType));
				content.accept(ANIMAL_CART.get(woodType));
                content.accept(WAGON.get(woodType));
			}
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
}