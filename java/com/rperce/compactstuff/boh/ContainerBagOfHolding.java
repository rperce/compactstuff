package com.rperce.compactstuff.boh;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBagOfHolding extends Container {
	private InventoryPlayer playerInventory;
	private InventoryBagOfHolding itemBag;
	
	public ContainerBagOfHolding(InventoryPlayer inv, InventoryBagOfHolding bag) {
		this.playerInventory = inv;
		this.itemBag = bag;
		
		for(int row = 0; row<3; row++) {
			for(int col=0; col<9; col++) {
				this.addSlotToContainer(new Slot(itemBag, col+row*9, 8+col*18, 18+row*18));
			}
		}
		for(int row=0; row<3; row++) {
			for(int col=0; col<9; col++) {
				this.addSlotToContainer(new Slot(playerInventory, col+row*9+9, 8+col*18, 32+(row+3)*18));
			}
		}
		for(int col=0; col<9; col++) {
			this.addSlotToContainer(new Slot(playerInventory, col, 8+col*18, 90+3*18));
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer useless) {
		return true;
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player) {
		itemBag.saveInventoryToItemStack(player.getHeldItem());
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack out = null;
        Slot slot = (Slot)this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack()) {
            ItemStack temp = slot.getStack();
            out = temp.copy();

            if (slotIndex < 3 * 9) {
                if (!this.mergeItemStack(temp, 3*9 + 1, this.inventorySlots.size(), true))
                    return null;
            }
            else if (!this.mergeItemStack(temp, 0, 3*9, false))
                return null;

            if (temp.stackSize == 0)
                slot.putStack((ItemStack)null);
            else
            	slot.onSlotChanged();
        }
        
        
        return out;
	}
}
