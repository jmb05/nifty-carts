package net.jmb19905.niftycarts.item;

import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.entity.AbstractDrawnEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CartItem extends Item {

    private final WoodType woodType;
    private final String cartType;

    public CartItem(WoodType woodType, String cartType, Properties settings) {
        super(settings);
        this.woodType = woodType;
        this.cartType = cartType;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        consumer.accept(Component.empty());
        consumer.accept(Component.translatable("item." + this.cartType + ".tooltip1").withStyle(ChatFormatting.GRAY));
        consumer.accept(Component.translatable("item." + this.cartType + ".tooltip2").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        final ItemStack stack = player.getItemInHand(interactionHand);
        final BlockHitResult result = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (result.getType() == HitResult.Type.MISS) {
            return InteractionResult.PASS;
        } else {
            final Vec3 lookVec = player.getLookAngle();
            final List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(lookVec.scale(5.0D)).inflate(5.0D), EntitySelector.NO_SPECTATORS.and(Entity::canBeCollidedWith));
            if (!list.isEmpty()) {
                final Vec3 eyePos = player.getEyePosition(1.0F);
                for (final Entity entity : list) {
                    final AABB axisalignedbb = entity.getBoundingBox().inflate(entity.getPickRadius());
                    if (axisalignedbb.contains(eyePos)) {
                        return InteractionResult.PASS;
                    }
                }
            }

            if (result.getType() == HitResult.Type.BLOCK) {
                ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, NiftyCarts.resLoc(cartType));
                final Optional<Holder.Reference<EntityType<?>>> type = BuiltInRegistries.ENTITY_TYPE.get(key);
                if (type.isEmpty()) return InteractionResult.PASS;
                final Entity cart = type.get().value().create(level, EntitySpawnReason.SPAWN_ITEM_USE);
                if (cart == null) return InteractionResult.PASS;
                if (cart instanceof AbstractDrawnEntity drawn) drawn.setWoodType(woodType);
                cart.snapTo(result.getLocation().x, result.getLocation().y, result.getLocation().z);
                cart.setYRot((player.getYRot() + 180) % 360);
                if (!level.noCollision(cart, cart.getBoundingBox().inflate(0.1F, -0.1F, 0.1F))) {
                    return InteractionResult.FAIL;
                } else {
                    if (!level.isClientSide()) {
                        level.addFreshEntity(cart);
                        level.playSound(null, cart.getX(), cart.getY(), cart.getZ(), NiftyCarts.PLACE_SOUND, SoundSource.BLOCKS, 0.75F, 0.8F);
                    }
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    player.awardStat(Stats.ITEM_USED.get(this));
                    return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
                }
            } else {
                return InteractionResult.PASS;
            }
        }
    }
}
