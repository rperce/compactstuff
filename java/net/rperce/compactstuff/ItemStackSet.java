package net.rperce.compactstuff;

import net.minecraft.item.ItemStack;

import java.util.HashSet;
import java.util.Optional;

public class ItemStackSet extends HashSet<ItemStack> {
    public ItemStackSet() {
        super();
    }
    public ItemStackSet(ItemStackSet o) {
        super(o);
    }

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof ItemStack)) return false;
        ItemStack stack = (ItemStack)o;
        for (ItemStack istack : this) {
            if (stack.isItemEqual(istack)) return true;
        }
        return false;
    }

    @Override
    public boolean add(ItemStack itemStack) {
        return (!this.contains(itemStack) && super.add(itemStack));
    }

    @Override
    public boolean remove(Object o) {
        if (!(o instanceof ItemStack)) return false;
        ItemStack stack = (ItemStack)o;
        if (!this.contains(stack)) return false;
        Optional<ItemStack> rem = this.stream()
                .filter(stack::isItemEqual)
                .findFirst();
        boolean out = rem.isPresent();
        rem.ifPresent(super::remove);
        return out;
    }
}
