package net.jmb19905.niftycarts.item;

import net.jmb19905.niftycarts.NiftyCarts;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CartItem extends Item {

    public CartItem(Properties settings) {
        super(settings);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        final ItemStack stack = player.getItemInHand(interactionHand);
        final BlockHitResult result = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (result.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(stack);
        } else {
            final Vec3 lookVec = player.getLookAngle();
            final List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(lookVec.scale(5.0D)).inflate(5.0D), EntitySelector.NO_SPECTATORS.and(Entity::canBeCollidedWith));
            if (!list.isEmpty()) {
                final Vec3 eyePos = player.getEyePosition(1.0F);
                for (final Entity entity : list) {
                    final AABB axisalignedbb = entity.getBoundingBox().inflate(entity.getPickRadius());
                    if (axisalignedbb.contains(eyePos)) {
                        return InteractionResultHolder.pass(stack);
                    }
                }
            }

            if (result.getType() == HitResult.Type.BLOCK) {
                final EntityType<?> type = Registry.ENTITY_TYPE.get(Registry.ITEM.getKey(this));
                final Entity cart = type.create(level);
                if (cart == null) {
                    return InteractionResultHolder.pass(stack);
                }
                cart.moveTo(result.getLocation().x, result.getLocation().y, result.getLocation().z);
                cart.setYRot((player.getYRot() + 180) % 360);
                if (!level.noCollision(cart, cart.getBoundingBox().inflate(0.1F, -0.1F, 0.1F))) {
                    return InteractionResultHolder.fail(stack);
                } else {
                    if (!level.isClientSide()) {
                        level.addFreshEntity(cart);
                        level.playSound(null, cart.getX(), cart.getY(), cart.getZ(), NiftyCarts.PLACE_SOUND, SoundSource.BLOCKS, 0.75F, 0.8F);
                    }
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    player.awardStat(Stats.ITEM_USED.get(this));
                    return InteractionResultHolder.success(stack);
                }
            } else {
                return InteractionResultHolder.pass(stack);
            }
        }
    }
}
