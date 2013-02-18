package compactstuff.boh;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InventoryBagOfHolding implements IInventory {
	private ItemStack[] inv;
	int rows;
	private ItemStack bag;
	
	public InventoryBagOfHolding(int rows, ItemStack bag) {
		this.rows = rows;
		inv = new ItemStack[getSizeInventory()];
		if(bag.stackTagCompound==null) bag.stackTagCompound = new NBTTagCompound();
		if(!bag.stackTagCompound.hasKey("BoHItems")) saveInventoryToItemStack(bag);
		readItemsFromNBT(bag.stackTagCompound.getTagList("BoHItems"));
	}
	
	private void readItemsFromNBT(NBTTagList itemList) {
		inv = new ItemStack[getSizeInventory()];
		for(int i=0; i<itemList.tagCount(); i++) {
			NBTTagCompound item = (NBTTagCompound)itemList.tagAt(i);
			byte slot = item.getByte("slot");
			
			if(slot>=0 && slot<inv.length) {
				inv[slot] = ItemStack.loadItemStackFromNBT(item);
			}
		}
	}
	
	public void saveInventoryToItemStack(ItemStack heldStack) {
		if(heldStack==null) return;
		NBTTagList items = new NBTTagList();
		for(int i=0; i<inv.length; i++) {
			if(inv[i]!=null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("slot",(byte)i);
				inv[i].writeToNBT(item);
				items.appendTag(item);
			}
		}
		heldStack.stackTagCompound.setTag("BoHItems",items);
	}

	@Override public int getSizeInventory() {
		return rows*9;
	}

	@Override public ItemStack getStackInSlot(int slot) {
		return inv[slot];
	}

	@Override public ItemStack decrStackSize(int slot, int amt) {
		if(inv[slot]==null) return null;
		ItemStack out;
		if (inv[slot].stackSize <= amt) {
            out = inv[slot];
            inv[slot] = null;
            return out;
        } else {
            out = inv[slot].splitStack(amt);
            if(inv[slot].stackSize == 0) inv[slot] = null;
            return out;
        }
	}

	@Override public ItemStack getStackInSlotOnClosing(int slot) {
		if (inv[slot] != null) {
            ItemStack var2 = inv[slot];
            inv[slot] = null;
            return var2;
        } 
        return null;
	}

	@Override public void setInventorySlotContents(int slot, ItemStack stack) {
		inv[slot] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
            stack.stackSize = this.getInventoryStackLimit();
	}

	@Override public String getInvName() {
		return "robertwan.bagofholding";
	}

	@Override public int getInventoryStackLimit() {
		return 64;
	}

	@Override public void onInventoryChanged() {}

	@Override public boolean isUseableByPlayer(EntityPlayer var1) {
		return true;
	}

	@Override public void openChest() {}
	@Override public void closeChest() {}

}
