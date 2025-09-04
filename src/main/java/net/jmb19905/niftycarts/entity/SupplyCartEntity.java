package net.jmb19905.niftycarts.entity;

import net.jmb19905.niftycarts.NiftyCarts;
import net.jmb19905.niftycarts.NiftyCartsConfig;
import net.minecraft.util.Mth;
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
import net.minecraft.world.phys.Vec3;

public class SupplyCartEntity extends AbstractCargoCart {

    public SupplyCartEntity(EntityType<? extends Entity> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn, 54);
    }

    @Override
    public Item getCartItem() {
        return NiftyCarts.SUPPLY_CART.get(getWoodType());
    }

    @Override
    public NiftyCartsConfig.CartConfig getConfig() {
        return NiftyCartsConfig.get().supplyCart;
    }

    @Override
    public double getPassengersRidingOffset() {
        return 10.5D / 16.0D;
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction moveFunction) {
        if (this.hasPassenger(passenger)) {
            final Vec3 forward = this.getLookAngle();
            final Vec3 origin = new Vec3(0.0D, this.getPassengersRidingOffset(), 1.0D / 16.0D);
            final Vec3 pos = origin.add(forward.scale(-0.68D));
            moveFunction.accept(passenger, this.getX() + pos.x, getY() + pos.y - 0.1D + passenger.getMyRidingOffset(), getZ() + pos.z);
            passenger.setYBodyRot(this.getYRot() + 180.0F);
            final float f2 = Mth.wrapDegrees(passenger.getYRot() - this.getYRot() + 180.0F);
            final float f1 = Mth.clamp(f2, -105.0F, 105.0F);
            passenger.yRotO += f1 - f2;
            passenger.setYRot(passenger.getYRot() + (f1 - f2));
            passenger.setYHeadRot(passenger.getYRot());
        }
    }

    @Override
    protected InteractionResult onInteractNotOpen(Player player, InteractionHand hand) {
        final InteractionResult bannerResult = this.useBanner(player, hand);
        if (bannerResult.consumesAction()) {
            return bannerResult;
        }
        if (this.isVehicle()) {
            return InteractionResult.PASS;
        }
        if (!this.level().isClientSide) {
            return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected AbstractContainerMenu createMenuLootUnpacked(int i, Inventory inventory, Player player) {
        return ChestMenu.sixRows(i, inventory, this);
    }
}
