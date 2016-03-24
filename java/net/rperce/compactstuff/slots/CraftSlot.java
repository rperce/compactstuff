package net.rperce.compactstuff.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.rperce.compactstuff.VoidFunction;

public class CraftSlot extends Slot {
    private final VoidFunction fn;
    public CraftSlot(IInventory a, int b, int c, int d, VoidFunction fn) {
        super(a, b, c, d);
        this.fn = fn;
    }

    @Override
    public void onSlotChanged() {
        fn.action();
    }
}
