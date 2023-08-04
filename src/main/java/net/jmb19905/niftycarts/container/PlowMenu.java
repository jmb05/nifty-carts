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

public class PlowMenu extends AbstractContainerMenu {

    public static final int SLOT_COUNT = 3;

    private final Container container;

    public PlowMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(SLOT_COUNT));
    }

    public PlowMenu(int id, Inventory inventory, Container container) {
        this(NiftyCarts.PLOW_MENU_TYPE, id, inventory, container);
    }

    public PlowMenu(MenuType<?> menuType, int id, Inventory inventory, Container container) {
        super(menuType, id);
        checkContainerSize(container, SLOT_COUNT);
        this.container = container;
        container.startOpen(inventory.player);
        this.addSlot(new Slot(container, 0, 57, 24));
        this.addSlot(new Slot(container, 1, 80, 17));
        this.addSlot(new Slot(container, 2, 103, 24));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
        }
    }

    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(final Player playerIn, final int index) {
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
}
