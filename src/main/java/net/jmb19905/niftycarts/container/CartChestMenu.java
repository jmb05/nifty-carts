package net.jmb19905.niftycarts.container;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CartChestMenu extends AbstractContainerMenu {
    private final Container container;
    private final int containerRows;

    public CartChestMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory, int rows) {
        super(menuType, containerId);
        this.container = new SimpleContainer(9 * rows);
        this.containerRows = rows;
        this.container.startOpen(playerInventory.player);
        int slotSize = 18;
        this.addChestGrid(this.container, 8, slotSize);
        int i = this.containerRows < 12 ? 8 : 8 + 27;
        int j = this.containerRows < 12 ? slotSize + this.containerRows * slotSize + 13 : slotSize + 9 * slotSize + 13;
        this.addStandardInventorySlots(playerInventory, i, j);
    }

    protected void addInventoryHotbarSlots(Container container, int x, int y) {
        for(int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(container, i, x + i * 18, y));
        }
    }

    protected void addInventoryExtendedSlots(Container container, int x, int y) {
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(container, j + (i + 1) * 9, x + j * 18, y + i * 18));
            }
        }
    }

    protected void addStandardInventorySlots(Container container, int x, int y) {
        this.addInventoryExtendedSlots(container, x, y);
        this.addInventoryHotbarSlots(container, x, y + 58);
    }

    private void addChestGrid(Container container, int xOffset, int yOffset) {
        if (this.containerRows < 12) {
            for(int i = 0; i < this.containerRows; ++i) {
                for(int j = 0; j < 9; ++j) {
                    this.addSlot(new Slot(container, j + i * 9, xOffset + j * 18, yOffset + i * 18));
                }
            }
        } else {
            for(int i = 0; i < 9; ++i) {
                for(int j = 0; j < this.containerRows; ++j) {
                    this.addSlot(new Slot(container, j * 9 + i, (xOffset + j * 18), yOffset + i * 18));
                }
            }
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (index < this.containerRows * 9) {
                if (!this.moveItemStackTo(itemStack2, this.containerRows * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemStack2, 0, this.containerRows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    public boolean stillValid(@NotNull Player player) {
        return this.container.stillValid(player);
    }

    public void removed(@NotNull Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    public Container getContainer() {
        return this.container;
    }

    public int getRowCount() {
        return this.containerRows;
    }
}
