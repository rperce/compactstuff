package com.rperce.compactstuff.furnace;


import java.util.HashMap;

import com.rperce.compactstuff.Commons;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityCompactFurnace extends TileEntity implements ISidedInventory {
	/**
	 * Slot 0: Material; 1: Fuel; 2: Output
	 * @return
	 */
	public abstract ItemStack[] getFurnaceItemStacks();
	public abstract void setFurnaceItemStacks(ItemStack[] i);
	public abstract int getSizeInventory();
	public abstract int getFurnaceCookTime();
	public abstract int getFurnaceBurnTime();
	public abstract int getCurrentItemBurnTime();
	public abstract void setFurnaceCookTime(int time);
	public abstract void setFurnaceBurnTime(int time);
	public abstract void setCurrentItemBurnTime(int time);
	public abstract void updateEntity();
	public abstract void smeltItem();
	public abstract boolean canSmelt();
	
	public boolean isCarbonFurnace() { return false; }
	public boolean isCobbleFurnace() { return false; }
	public HashMap<ItemStack, ItemStack> getCustom() { return null; }
	
	public TileEntityCompactFurnace() {
		super();
	}
	
	public int getCookProgressScaled(int par1) {
        return getFurnaceCookTime() * par1 / 200;
    }
	
	public int getBurnTimeRemainingScaled(int par1) {
        if (getCurrentItemBurnTime() == 0)
            setCurrentItemBurnTime(200);

        return getFurnaceBurnTime() * par1 / getCurrentItemBurnTime();
    }
	
	public boolean isBurning() {
        return getFurnaceBurnTime() > 0;
    }
	
	public boolean hasOutput() {
		return getStackInSlot(2)!=null && getStackInSlot(2).stackSize>0;
	}
	public boolean hasFuel() {
		return Commons.getItemBurnTime(getStackInSlot(1))>0;
	}
	

	public void updateBlock() {
		BlockCompactFurnace.updateFurnace(isBurning(),
				this.worldObj, this.xCoord, this.yCoord, this.zCoord);
	}
	
	@Override public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        setFurnaceBurnTime(data.getShort("BurnTime"));
        setFurnaceCookTime(data.getShort("CookTime"));
        setFurnaceItemStacks(Commons.readStacksFromNBT(data));
        setCurrentItemBurnTime(Commons.getItemBurnTime(this.getFurnaceItemStacks()[1]));
    }
	@Override public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setShort("BurnTime", (short)getFurnaceBurnTime());
        data.setShort("CookTime", (short)getFurnaceCookTime());
        data = Commons.writeStacksToNBT(data, getFurnaceItemStacks());
    }
	@Override public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
        getFurnaceItemStacks()[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
            par2ItemStack.stackSize = this.getInventoryStackLimit();
    }
	
	@Override public ItemStack getStackInSlotOnClosing(int par1) {
        if (getFurnaceItemStacks()[par1] != null) {
            ItemStack var2 = getFurnaceItemStacks()[par1];
            getFurnaceItemStacks()[par1] = null;
            return var2;
        } 
        return null;
    }
	
	@Override public ItemStack decrStackSize(int slot, int amt) {
        if (getFurnaceItemStacks()[slot] != null) {
            ItemStack var3;

            if (getFurnaceItemStacks()[slot].stackSize <= amt) {
                var3 = getFurnaceItemStacks()[slot];
                getFurnaceItemStacks()[slot] = null;
                return var3;
            } else {
                var3 = this.getFurnaceItemStacks()[slot].splitStack(amt);

                if (this.getFurnaceItemStacks()[slot].stackSize == 0) {
                    this.getFurnaceItemStacks()[slot] = null;
                }

                return var3;
            }
        }
        return null;
    }
	@Override public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ?
		false :
			player.getDistanceSq((double)this.xCoord + 0.5D,
				(double)this.yCoord + 0.5D,
				(double)this.zCoord + 0.5D) <= 64.0D;
	}
	@Override public ItemStack getStackInSlot(int par1) {
        return getFurnaceItemStacks()[par1];
    }
	@Override public boolean isInvNameLocalized() { return false; }
	
	@Override public boolean isItemValidForSlot(int slot, ItemStack stack) {
		switch(slot) {
			case 0: return FurnaceRecipes.smelting().getSmeltingResult(stack)!=null || getCustom()!=null && getCustom().containsKey(stack);
			case 1: return Commons.getItemBurnTime(stack)>0;
		} return false;
	}
	@Override public int getInventoryStackLimit() { return 64; }
	@Override public void openChest()  { }
	@Override public void closeChest() { }
	
	@Override public int[] getAccessibleSlotsFromSide(int side) {
		if(side==1) return new int[] {0,1}; //top: material and fuel
		if(side==0) return new int[] {1,2}; //bottom: result and fuel (bucket)
		return new int[] {0,1,2}; //sides: material, fuel, result
	}
	@Override public boolean canInsertItem(int slot, ItemStack item, int side) {
		return isItemValidForSlot(slot, item);
	}
	@Override public boolean canExtractItem(int slot, ItemStack item, int side) {
		return slot==2 || item.itemID==Item.bucketEmpty.itemID;
	}
}