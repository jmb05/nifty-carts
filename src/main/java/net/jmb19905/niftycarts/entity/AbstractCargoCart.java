package net.jmb19905.niftycarts.entity;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.NonNullList;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

public abstract class AbstractCargoCart extends AbstractDrawnInventoryEntity {

    private static final ImmutableList<@NotNull EntityDataAccessor<@NotNull ItemStack>> CARGO = ImmutableList.of(
            SynchedEntityData.defineId(AbstractCargoCart.class, EntityDataSerializers.ITEM_STACK),
            SynchedEntityData.defineId(AbstractCargoCart.class, EntityDataSerializers.ITEM_STACK),
            SynchedEntityData.defineId(AbstractCargoCart.class, EntityDataSerializers.ITEM_STACK),
            SynchedEntityData.defineId(AbstractCargoCart.class, EntityDataSerializers.ITEM_STACK));

    public AbstractCargoCart(EntityType<? extends @NotNull Entity> entityTypeIn, Level worldIn, int inventorySize) {
        super(entityTypeIn, worldIn, inventorySize);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        for (final EntityDataAccessor<@NotNull ItemStack> parameter : CARGO) {
            builder.define(parameter, ItemStack.EMPTY);
        }
    }

    public NonNullList<@NotNull ItemStack> getCargo() {
        final NonNullList<@NotNull ItemStack> cargo = NonNullList.withSize(CARGO.size(), ItemStack.EMPTY);
        for (int i = 0; i < CARGO.size(); i++) {
            cargo.set(i, this.entityData.get(CARGO.get(i)));
        }
        return cargo;
    }

    @Override
    protected void onContentsChanged(int slot) {
        final Object2IntMap<Item> totals = new Object2IntLinkedOpenHashMap<>();
        final Object2ObjectMap<Item, ItemStack> stacks = new Object2ObjectOpenHashMap<>();
        for (int i = 0; i < this.getItemStacks().size(); i++) {
            final ItemStack stack = this.getItemStacks().get(i);
            if (!stack.isEmpty()) {
                totals.mergeInt(stack.getItem(), 1, Integer::sum);
                stacks.putIfAbsent(stack.getItem(), stack);
            }
        }
        final Iterator<Object2IntMap.Entry<Item>> topTotals = totals.object2IntEntrySet().stream()
                .sorted(Comparator.<Object2IntMap.Entry<Item>>comparingInt(e -> e.getKey() instanceof BlockItem ? 0 : 1)
                        .thenComparingInt(e -> -e.getIntValue()))
                .limit(CARGO.size()).iterator();
        final ItemStack[] items = new ItemStack[CARGO.size()];
        Arrays.fill(items, ItemStack.EMPTY);
        final int forth = this.getItemStacks().size() / CARGO.size();
        for (int pos = 0; topTotals.hasNext() && pos < CARGO.size(); ) {
            final Object2IntMap.Entry<Item> entry = topTotals.next();
            final int count = Math.max(1, (entry.getIntValue() + forth / 2) / forth);
            for (int n = 1; n <= count && pos < CARGO.size(); n++) {
                final ItemStack stack = stacks.getOrDefault(entry.getKey(), ItemStack.EMPTY).copy();
                stack.setCount(Math.min(stack.getMaxStackSize(), entry.getIntValue() / n));
                items[pos++] = stack;
            }
        }
        for (int i = 0; i < CARGO.size(); i++) {
            this.getEntityData().set(CARGO.get(i), items[i]);
        }
    }

    @Override
    protected void saveInventory(ValueOutput output) {
        this.addChestVehicleSaveData(output);
    }

    @Override
    protected void readInventory(ValueInput input) {
        this.readChestVehicleSaveData(input);
    }
}
