package net.jmb19905.niftycarts.mixin;

import net.jmb19905.niftycarts.entity.AbstractDrawnInventoryEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChestMenu.class)
public class ChestMenuMixin {

    @Redirect(
            method = "addChestGrid",
            at = @At(
                    value = "NEW",
                    target = "Lnet/minecraft/world/inventory/Slot;"))
    public Slot init(Container container, int i, int j, int k) {
        if (container instanceof AbstractDrawnInventoryEntity cart) {
            return new Slot(container, i, j, k) {
                @Override
                public boolean mayPlace(@NotNull ItemStack itemStack) {
                    return filterByBlacklist(cart, itemStack);
                }
            };
        }
        return new Slot(container, i, j, k);
    }

    @Unique
    private boolean filterByBlacklist(AbstractDrawnInventoryEntity cart, ItemStack itemStack) {
        if (cart.getConfig().cargoBlacklist != null) {
            var blacklist = cart.getConfig().cargoBlacklist.get();
            System.out.println(blacklist);
            System.out.println(BuiltInRegistries.ITEM.getKey(itemStack.getItem()));
            if (blacklist.contains(BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString())) {
                return false;
            }
            for (String itemStr : blacklist) {
                if (itemStr.startsWith("#")) {
                    String tagStr = itemStr.substring(1);
                    String[] parts = tagStr.split(":");
                    if (!hasValidCharacters(tagStr)) continue;
                    var tag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(parts[0], parts[1]));
                    if (itemStack.is(tag))
                        return false;
                }
            }
        }
        return true;
    }

    @Unique
    private boolean hasValidCharacters(String resLoc) {
        for (int i = 0; i < resLoc.length(); i++) {
            if (!Identifier.isAllowedInIdentifier(resLoc.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}