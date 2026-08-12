package net.jmb19905.niftycarts.container;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class WagonMenu extends AbstractContainerMenu {
    private final Container container;
    private final int containerRows;

    public WagonMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory, Container container, int rows) {
        super(menuType, containerId);
        this.container = container;
        this.containerRows = rows;
        container.startOpen(playerInventory.player);
        int slotSize = 18;
        this.addChestGrid(container, 8, slotSize);
        int i = this.containerRows < 12 ? 0 : 27;
        int j = this.containerRows < 12 ? slotSize + this.containerRows * slotSize + 13 : slotSize + 9 * slotSize + 13;
        this.addStandardInventorySlots(playerInventory, 8 + i, j);
    }

    private void addChestGrid(Container container, int xOffset, int yOffset) {
        for(int i = 0; i < 9; ++i) {
            for(int j = 0; j < this.containerRows; ++j) {
                this.addSlot(new Slot(container, j * 9 + i, xOffset + j * 18, yOffset + i * 18));
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
