package com.rperce.compactstuff.boh;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InventoryBagOfHolding implements IInventory {
	private ItemStack[] inv;
	int rows;
	public InventoryBagOfHolding(int rows, ItemStack bag) {
		this.rows = rows;
		this.inv = new ItemStack[getSizeInventory()];
		if(bag.stackTagCompound==null) bag.stackTagCompound = new NBTTagCompound();
		if(!bag.stackTagCompound.hasKey("BoHItems")) saveInventoryToItemStack(bag);
		readItemsFromNBT(bag.stackTagCompound.getTagList("BoHItems"));
	}
	
	private void readItemsFromNBT(NBTTagList itemList) {
		this.inv = new ItemStack[getSizeInventory()];
		for(int i=0; i<itemList.tagCount(); i++) {
			NBTTagCompound item = (NBTTagCompound)itemList.tagAt(i);
			byte slot = item.getByte("slot");
			
			if(slot>=0 && slot<this.inv.length) {
				this.inv[slot] = ItemStack.loadItemStackFromNBT(item);
			}
		}
	}
	
	public void saveInventoryToItemStack(ItemStack heldStack) {
		if(heldStack==null) return;
		NBTTagList items = new NBTTagList();
		for(int i=0; i<this.inv.length; i++) {
			if(this.inv[i]!=null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("slot",(byte)i);
				this.inv[i].writeToNBT(item);
				items.appendTag(item);
			}
		}
		heldStack.stackTagCompound.setTag("BoHItems",items);
	}

	@Override public int getSizeInventory() {
		return this.rows*9;
	}

	@Override public ItemStack getStackInSlot(int slot) {
		return this.inv[slot];
	}

	@Override public ItemStack decrStackSize(int slot, int amt) {
		if(this.inv[slot]==null) return null;
		ItemStack out;
		if (this.inv[slot].stackSize <= amt) {
            out = this.inv[slot];
            this.inv[slot] = null;
            return out;
        }
		out = this.inv[slot].splitStack(amt);
		if(this.inv[slot].stackSize == 0) this.inv[slot] = null;
		return out;
	}

	@Override public ItemStack getStackInSlotOnClosing(int slot) {
		if (this.inv[slot] != null) {
            ItemStack var2 = this.inv[slot];
            this.inv[slot] = null;
            return var2;
        } 
        return null;
	}

	@Override public void setInventorySlotContents(int slot, ItemStack stack) {
		this.inv[slot] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
            stack.stackSize = this.getInventoryStackLimit();
	}

	@Override public String getInvName() {
		return "robertwan.bagofholding";
	}

	@Override public int getInventoryStackLimit() {
		return 64;
	}

	@Override public void onInventoryChanged() {
		// overrode from interface, no action taken
	}

	@Override public boolean isUseableByPlayer(EntityPlayer var1) {
		return true;
	}

	@Override public void openChest() {
		// overrode from interface, no action taken
	}
	@Override public void closeChest() {
		// overrode from interface, no action taken
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

}
