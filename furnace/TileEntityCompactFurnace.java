package mods.CompactStuff.furnace;


import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.registry.GameRegistry;

public abstract class TileEntityCompactFurnace extends TileEntity implements IInventory {
	
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
		return getItemBurnTime(getStackInSlot(1))>0;
	}
	

	public void updateBlock() {
		BlockCompactFurnace.updateFurnace(isBurning(),
				this.worldObj, this.xCoord, this.yCoord, this.zCoord);
	}
	
	public static int getItemBurnTime(ItemStack stack)
    {
        if (stack == null) return 0;
        else {
            int var1 = stack.getItem().itemID;
            Item var2 = stack.getItem();

            if (stack.getItem() instanceof ItemBlock && Block.blocksList[var1] != null) {
                Block var3 = Block.blocksList[var1];

                if (var3 == Block.woodSingleSlab)
                    return 150;

                if (var3.blockMaterial == Material.wood)
                    return 300;
            }
            if (var2 instanceof ItemTool && ((ItemTool) var2).getToolMaterialName().equals("WOOD")) return 200;
            if (var2 instanceof ItemSword && ((ItemSword) var2).getToolMaterialName().equals("WOOD")) return 200;
            if (var2 instanceof ItemHoe && ((ItemHoe) var2).func_77842_f().equals("WOOD")) return 200;
            if (var1 == Item.stick.itemID) return 100;
            if (var1 == Item.coal.itemID) return 1600;
            if (var1 == Item.bucketLava.itemID) return 20000;
            if (var1 == Block.sapling.blockID) return 100;
            if (var1 == Item.blazeRod.itemID) return 2400;
            return GameRegistry.getFuelValue(stack);
        }
    }
	@Override public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
        setFurnaceItemStacks(new ItemStack[this.getSizeInventory()]);

        for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
            NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
            byte var5 = var4.getByte("Slot");

            if (var5 >= 0 && var5 < this.getFurnaceItemStacks().length)
            	this.getFurnaceItemStacks()[var5] = ItemStack.loadItemStackFromNBT(var4);
        }

        setFurnaceBurnTime(par1NBTTagCompound.getShort("BurnTime"));
        setFurnaceCookTime(par1NBTTagCompound.getShort("CookTime"));
        setCurrentItemBurnTime(getItemBurnTime(this.getFurnaceItemStacks()[1]));
    }
	
	@Override public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setShort("BurnTime", (short)getFurnaceBurnTime());
        par1NBTTagCompound.setShort("CookTime", (short)getFurnaceCookTime());
        NBTTagList var2 = new NBTTagList();

        for (int var3 = 0; var3 < getFurnaceItemStacks().length; ++var3) {
            if (getFurnaceItemStacks()[var3] != null) {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                getFurnaceItemStacks()[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }

        par1NBTTagCompound.setTag("Items", var2);
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
	
	@Override public boolean isStackValidForSlot(int slot, ItemStack stack) {
		switch(slot) {
			case 0: return FurnaceRecipes.smelting().getSmeltingResult(stack)!=null || getCustom()!=null && getCustom().containsKey(stack);
			case 1: return getItemBurnTime(stack)>0;
		} return false;
	}
	@Override public int getInventoryStackLimit() { return 64; }
	@Override public void openChest()  { }
	@Override public void closeChest() { }
}