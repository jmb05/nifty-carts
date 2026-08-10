package net.jmb19905.niftycarts.mixin;

import net.jmb19905.niftycarts.entity.AbstractCargoCart;
import net.jmb19905.niftycarts.entity.AbstractDrawnInventoryEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChestMenu.class)
public class ChestMenuMixin {

    @Redirect(
            method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;I)V",
            at = @At(
                    value = "NEW",
                    target = "Lnet/minecraft/world/inventory/Slot;"))
    public Slot init(Container container, int i, int j, int k) {
        if (container instanceof AbstractDrawnInventoryEntity cart) {
            return new Slot(container, i, j, k) {
                @Override
                public boolean mayPlace(ItemStack itemStack) {
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
            for (String itemStr : blacklist) {
                if (ResourceLocation.isValidResourceLocation(itemStr) && hasValidCharacters(itemStr)) {
                    Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(itemStr));
                    if (itemStack.is(item))
                        return false;
                } else if (itemStr.startsWith("#")) {
                    String tagStr = itemStr.substring(1);
                    if (!ResourceLocation.isValidResourceLocation(tagStr) || !hasValidCharacters(tagStr)) continue;
                    var tag = TagKey.create(Registries.ITEM, new ResourceLocation(tagStr));
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
            if (!ResourceLocation.isAllowedInResourceLocation(resLoc.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
