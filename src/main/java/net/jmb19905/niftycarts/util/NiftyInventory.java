package net.jmb19905.niftycarts.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class NiftyInventory extends NonNullList<@NotNull ItemStack> {

    private Consumer<Integer> onContentsChanged;

    public static NiftyInventory create() {
        return new NiftyInventory(new ArrayList<>(), null);
    }

    public static NiftyInventory createWithCapacity(int i) {
        return new NiftyInventory(new ArrayList<>(i), null);
    }

    public static NiftyInventory withSize(int i, ItemStack object) {
        if (object == null) {
            object = ItemStack.EMPTY;
        }
        List<ItemStack> list = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            list.add(object);
        }
        return new NiftyInventory(list, object);
    }

    public NiftyInventory(List<ItemStack> list, @Nullable ItemStack object) {
        super(list, object);
    }

    public void setOnContentsChanged(Consumer<Integer> onContentsChanged) {
        this.onContentsChanged = onContentsChanged;
    }

    @Override
    public @NotNull ItemStack set(int i, ItemStack object) {
        var out = super.set(i, object);
        onContentsChanged.accept(i);
        return out;
    }

    @Override
    public void add(int i, ItemStack object) {
        super.add(i, object);
        onContentsChanged.accept(i);
    }

    @Override
    public ItemStack remove(int i) {
        var out = super.remove(i);
        onContentsChanged.accept(i);
        return out;
    }

    @Override
    public void clear() {
        int size = this.size();
        super.clear();
        for(int i = 0; i < size; ++i) {
            onContentsChanged.accept(i);
        }
    }
}
