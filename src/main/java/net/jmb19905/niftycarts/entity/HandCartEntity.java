package net.jmb19905.niftycarts.entity;

import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsConfig;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class HandCartEntity extends AbstractCargoCart {

    public HandCartEntity(EntityType<? extends Entity> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn, 27);
    }

    @Override
    protected double getSpacing() {
        return 1.0;
    }

    @Override
    protected boolean canInteractNotOpen() {
        return false;
    }

    @Override
    protected InteractionResult onInteractNotOpen(Player player, InteractionHand hand) {
        return InteractionResult.SUCCESS;
    }

    @Override
    protected AbstractContainerMenu createMenuLootUnpacked(int i, Inventory inventory, Player player) {
        return ChestMenu.threeRows(i, inventory, this);
    }

    @Override
    public Item getCartItem() {
        return NiftyCarts.HAND_CART.get(getWoodType());
    }

    @Override
    protected NiftyCartsConfig.CartConfig getConfig() {
        return NiftyCartsConfig.get().handCart;
    }
}
