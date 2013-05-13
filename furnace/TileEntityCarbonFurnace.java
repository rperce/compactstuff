package mods.CompactStuff.furnace;

import java.util.HashMap;

import mods.CompactStuff.CompactStuff;
import mods.CompactStuff.ItemStuff;
import mods.CompactStuff.Metas;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class TileEntityCarbonFurnace extends TileEntityCompactFurnace {
	public static final int[] oreIDs = {
		Block.oreCoal.blockID, Block.oreDiamond.blockID, Block.oreEmerald.blockID,
		Block.oreGold.blockID, Block.oreIron.blockID, Block.oreLapis.blockID,
		Block.oreRedstone.blockID, Block.oreNetherQuartz.blockID
	};
	private static ItemStack glassPaneStack = new ItemStack(Block.thinGlass);
	private ItemStack[] furnaceItemStacks = new ItemStack[3];
	public int furnaceBurnTime = 0;
	public int currentItemBurnTime = 0;
	public int furnaceCookTime = 0;
	private byte change = 3;
	public static HashMap<ItemStack, ItemStack> custom = new HashMap<ItemStack, ItemStack>() {
		@Override public boolean containsKey(Object key) {
			if(!(key instanceof ItemStack)) return false;
			ItemStack stack = (ItemStack)key;
			for(ItemStack i : this.keySet()) {
				if(i.isItemEqual(stack)) return true;
			}
			return false;
		}
		@Override public ItemStack get(Object key) {
			if(!containsKey(key) || !(key instanceof ItemStack)) return null;
			ItemStack stack = (ItemStack)key;
			for(ItemStack i : this.keySet()) {
				if(i.isItemEqual(stack)) return super.get(i);
			}
			return null;
		}
	};
	static {
		custom.put(new ItemStack(Block.thinGlass), new ItemStack(CompactStuff.itemStuff,1,ItemStuff.GLASS_SLAG));
		custom.put(new ItemStack(CompactStuff.comBlock, 1, Metas.COMSAND), new ItemStack(CompactStuff.comGlass,3));
	}
	
	@Override public boolean isCarbonFurnace() { return true; }
	@Override public String getInvName() {
		return "compactstuff.carbonfurnace";
	}
	@Override public HashMap<ItemStack, ItemStack> getCustom() { return custom; }
	
	private int getChange() {
		change = (byte)((change+1)%4);
		return change==0?1:0;
	}
	
	@Override public void updateEntity() {
        boolean burning = this.furnaceBurnTime > 0;
        boolean invChange = false;
        int change = getChange();
        if(furnaceItemStacks[0]!=null) {
        	boolean k = true;
        	for(int i : oreIDs) {
        		if(i==furnaceItemStacks[0].itemID) {
        			k = false;
        			break;
        		}
        	}
        	if(k) change = 1;
        }
        
        if (this.furnaceBurnTime > 0)
        	this.furnaceBurnTime-=change;

        if (!this.worldObj.isRemote) {
            if (this.furnaceBurnTime == 0 && this.canSmelt()) {
                this.currentItemBurnTime = this.furnaceBurnTime = getItemBurnTime(this.furnaceItemStacks[1]);

                if (this.furnaceBurnTime > 0) {
                    invChange = true;

                    if (this.furnaceItemStacks[1] != null) {
                        this.furnaceItemStacks[1].stackSize--;

                        if (this.furnaceItemStacks[1].stackSize == 0) {
                            this.furnaceItemStacks[1] = this.furnaceItemStacks[1].getItem().getContainerItemStack(furnaceItemStacks[1]);
                        }
                    }
                }
            }

            if (this.isBurning() && this.canSmelt()) {
                this.furnaceCookTime+=change;

                if (this.furnaceCookTime >= 200-change) {
                    this.furnaceCookTime = 0;
                    this.smeltItem();
                    invChange = true;
                }
            } else {
                this.furnaceCookTime = 0;
            }

            if (burning != this.furnaceBurnTime > 0) {
                invChange = true;
                updateBlock();
            }
        }

        if (invChange) {
            this.onInventoryChanged();
        }
    }
	
	@Override public boolean canSmelt() {
        if (this.furnaceItemStacks[0] == null) {
        	return false;
        } else {
        	ItemStack var1 = null;
        	if(custom.containsKey(this.furnaceItemStacks[0]))
        		var1 = custom.get(this.furnaceItemStacks[0]);
        	else var1 = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0]);
            if (var1 == null) return false;
            if (this.furnaceItemStacks[2] == null) return true;
            if (!this.furnaceItemStacks[2].isItemEqual(var1)) return false;
            int result = furnaceItemStacks[2].stackSize + var1.stackSize;
            return (result <= getInventoryStackLimit() && result <= var1.getMaxStackSize());
        }
    }
	 
	@Override public void smeltItem() {
        if (this.canSmelt()) {
        	ItemStack result;
        	if(custom.containsKey(this.furnaceItemStacks[0]))
        		result = custom.get(this.furnaceItemStacks[0]);
        	else result = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0]);
            
            if (this.furnaceItemStacks[2] == null)
            	this.furnaceItemStacks[2] = result.copy();
            else if (this.furnaceItemStacks[2].isItemEqual(result))
            	furnaceItemStacks[2].stackSize += result.stackSize;

            for(int b : oreIDs) {
            	if(b == furnaceItemStacks[0].itemID) {
            		furnaceItemStacks[2].stackSize++;
            		break;
            	}
            }
            
            this.furnaceItemStacks[0].stackSize--;

            if (this.furnaceItemStacks[0].stackSize < 1)
            	this.furnaceItemStacks[0] = null;
        }
    }
	
	@Override public int getSizeInventory() { return furnaceItemStacks.length; }
	@Override public int getFurnaceCookTime() { return this.furnaceCookTime; }
	@Override public int getFurnaceBurnTime() { return this.furnaceBurnTime; }
	@Override public int getCurrentItemBurnTime() { return this.currentItemBurnTime; }
	@Override public void setFurnaceCookTime(int time) { this.furnaceCookTime = time; }
	@Override public void setFurnaceBurnTime(int time) { this.furnaceBurnTime = time; }
	@Override public void setCurrentItemBurnTime(int time) { this.currentItemBurnTime = time; }
	@Override public ItemStack[] getFurnaceItemStacks() { return this.furnaceItemStacks; }
	@Override public void setFurnaceItemStacks(ItemStack[] i) { this.furnaceItemStacks = i; }
}
