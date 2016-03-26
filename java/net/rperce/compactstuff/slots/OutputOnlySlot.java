package net.rperce.compactstuff.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class OutputOnlySlot extends Slot {
    public OutputOnlySlot(IInventory a, int b, int c, int d) {
        super(a, b, c, d);
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return false;
    }
}