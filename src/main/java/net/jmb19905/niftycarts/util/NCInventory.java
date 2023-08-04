package net.jmb19905.niftycarts.util;

import com.google.common.collect.Lists;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class NCInventory extends NonNullList<ItemStack> {

    private Consumer<Integer> onContentsChanged;

    public static NCInventory create() {
        return new NCInventory(Lists.newArrayList(), null);
    }

    public static NCInventory createWithCapacity(int i) {
        return new NCInventory(Lists.newArrayListWithCapacity(i), null);
    }

    public static NCInventory withSize(int i, ItemStack object) {
        Validate.notNull(object);
        ItemStack[] objects = new ItemStack[i];
        Arrays.fill(objects, object);
        return new NCInventory(Arrays.asList(objects), object);
    }

    public NCInventory(List<ItemStack> list, @Nullable ItemStack object) {
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
