package net.jmb19905.niftycarts.container;

import net.jmb19905.niftycarts.NiftyCarts;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SeedDrillMenu extends AbstractContainerMenu {

    public static final int SLOT_COUNT = 9;

    private final Container container;

    public SeedDrillMenu(int i, Inventory inv) {
        this(i, inv, new SimpleContainer(SLOT_COUNT));
    }

    public SeedDrillMenu(int i, Inventory inv, Container container) {
        this(NiftyCarts.SEED_DRILL_MENU_TYPE, i, inv, container);
    }

    protected SeedDrillMenu(MenuType<?> menuType, int id, Inventory inventory, Container container) {
        super(menuType, id);
        checkContainerSize(container, SLOT_COUNT);
        this.container = container;
        container.startOpen(inventory.player);
        for (int i = 0; i < SLOT_COUNT; i++) {
            this.addSlot(new Slot(container, i, 8 + 18 * i, 28) {
                @Override
                public boolean mayPlace(ItemStack itemStack) {
                    return itemStack.is(NiftyCarts.SEED_DRILL_PLANTABLE);
                }
            });
        }

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        final ItemStack itemstack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            final ItemStack itemstack1 = slot.getItem();
            if (index < this.container.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack1, this.container.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.container.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    public Container getContainer() {
        return this.container;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

}
