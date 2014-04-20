package com.rperce.compactstuff.furnace;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntityFurnace;

import com.rperce.compactstuff.Commons;
import com.rperce.compactstuff.CompactStuff;
import com.rperce.compactstuff.ItemStuff;
import com.rperce.compactstuff.Metas;

public class TileEntityBlazeFurnace extends TileEntityFurnace {
	private ItemStack[] stacks = new ItemStack[13];
	public int smeltingReserve = 0;
	public int currentTimeLeft = 0;
	public int currentFuelTime = 0;
	public int staticFuelTime = 0;
	private int change = 1;
	private ItemStack currentlySmelting;
	private int fuelChange = 0;
	
	public int smeltingReserveScaled(int total) {
		return (int)(((double)this.smeltingReserve/(200*8*64))*total);
	}
	public int currentFuelTimeScaled(int total) {
		return (int)(((double)this.currentFuelTime/this.staticFuelTime)*total);
	}
	public int currentTimeLeftScaled(int total) {
		return (int)(((double)this.currentTimeLeft/200)*total);
	}
	public boolean isCooking() { return this.currentTimeLeft>0; }
	public boolean isFueling() { return this.currentFuelTime>0; }
	
	private static HashMap<ItemStack, ItemStack> custom = new HashMap<ItemStack, ItemStack>() {
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
		custom.put(new ItemStack(Block.sand), new ItemStack(Block.glass,2));
		custom.put(new ItemStack(Block.netherrack), new ItemStack(Item.netherrackBrick,2));
		custom.put(new ItemStack(Block.wood), new ItemStack(Item.coal,2,1));
		custom.put(new ItemStack(Item.clay), new ItemStack(Item.brick,2));
		custom.put(new ItemStack(Block.cactus), new ItemStack(Item.dyePowder, 3, Metas.DYE_GREEN));
		custom.put(new ItemStack(Block.thinGlass), new ItemStack(CompactStuff.itemStuff,1,ItemStuff.GLASS_SLAG));
		custom.put(new ItemStack(CompactStuff.comBlock, 1, Metas.COMSAND), new ItemStack(CompactStuff.comGlass,3));
	}
	
	public HashMap<ItemStack,ItemStack> getCustom() { return custom; }
	@Override public String getInvName() { return "cs_blazefurnace"; }
	@Override public int getSizeInventory() { return this.stacks.length; }
	
	private int getOreChange() {
		this.change = (this.change+1)%5;
		return this.change==0?1:0;
	}
	private int getSlowChange() {
		this.change = (this.change+1)%3;
		return this.change==0?1:0;
	}
	
	@Override public void updateEntity() {
		if(this.worldObj.isRemote) return;
        boolean invChange = false;
        
//        System.out.println("CurrentFuelTime: "+currentFuelTime+"; smeltingReserve: "+smeltingReserve);
        if(this.currentFuelTime==0) {
        	if(this.stacks[12] != null) { //fuel slot
        		this.currentFuelTime = Math.max(0, Commons.getItemBurnTime(this.stacks[12]));
        		this.staticFuelTime = this.currentFuelTime;
        		System.out.println("Fuel for "+this.staticFuelTime+"!");
        		if(this.currentFuelTime > 0 && this.smeltingReserve + this.staticFuelTime <= 8 * 64 * 200) {
            		this.stacks[12].stackSize--;
            		if(this.stacks[12].stackSize == 0) {
            			this.stacks[12] = this.stacks[12].getItem().getContainerItemStack(this.stacks[12]);
            		}
                	this.fuelChange = (int)Math.max(1d, this.currentFuelTime/100d);
                	System.out.println("fuelChange: "+this.fuelChange);
                	invChange=true;
            	}
        	}
        } else {
        	if(this.smeltingReserve<8*64*200) {
	        	this.currentFuelTime-=this.fuelChange;
	        	this.smeltingReserve+=this.fuelChange;
        	} if(this.currentFuelTime<0) this.currentFuelTime = 0;
        }

        if(this.currentlySmelting==null && this.canSmelt() && this.smeltingReserve>=200) {
        	for(int i=0; i<6; i++) {
        		if(this.stacks[i]!=null) {
        			this.currentlySmelting = this.stacks[i].splitStack(1);
        			if(this.stacks[i].stackSize==0) this.stacks[i]=null;
        			break;
        		}
        	}
        	invChange = true;
        	this.currentTimeLeft = 200;
        }
        
    	if(this.currentlySmelting!=null) {
    		boolean ore = false;
	    	for(int i : TileEntityCarbonFurnace.oreIDs) {
	    		if(i==this.currentlySmelting.itemID) {
	    			ore = true;
	    			break;
	    		}
	    	}
	    	if(ore) this.change = getOreChange();
	    	else if(custom.containsKey(this.currentlySmelting)) this.change = getSlowChange();
	    	else this.change = 6;
	    
	        if (this.currentTimeLeft > 0) {
	        	this.currentTimeLeft -= this.change;
	        	if(this.currentTimeLeft < 0) this.currentTimeLeft = 0;
	        }
	        
	        if (this.currentTimeLeft == 0 && this.canSmelt()) {
	        	this.smeltItem();
	        	invChange = true;
	        }
    	}
        if (invChange) this.onInventoryChanged();
	}
	
	@Override public void smeltItem() {
    	ItemStack output = custom.get(this.currentlySmelting);
    	if(output==null) output = FurnaceRecipes.smelting().getSmeltingResult(this.currentlySmelting);
    	for(int i : TileEntityCarbonFurnace.oreIDs) {
    		if(i==this.currentlySmelting.itemID) {
    			output.stackSize*=3;
    		}
    	}
    	this.smeltingReserve-=200;
    	this.currentlySmelting = null;
    	addToOutputSection(output);
	}
	
	public void addToOutputSection(ItemStack a) {
		if(a==null) return;
		ItemStack output = a.copy();
		for(int i=6; i<12; i++) {
			if(this.stacks[i]==null) {
				this.stacks[i]=output.copy();
				break;
			} else if(Commons.areShallowEqual(this.stacks[i], output)) {
				this.stacks[i].stackSize+=output.splitStack(Math.min(output.stackSize, 
						this.stacks[i].getMaxStackSize()-this.stacks[i].stackSize)).stackSize;
				if(output.stackSize==0) break;
			}
		}
	}
	
	public boolean canSmelt() {
		if(this.smeltingReserve<200) return false; //assume enough smelting reserve henceforth
		boolean good = false;
		for(int i=0; i<6; i++) {
			if(FurnaceRecipes.smelting().getSmeltingResult(this.stacks[i])!=null) {
				good = true;
				break;
			}
		} return good;
	}

	public void updateBlock() {
		BlockCompactFurnace.updateFurnace(isBurning(),
				this.worldObj, this.xCoord, this.yCoord, this.zCoord);
	}
	
	@Override public void readFromNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);
        NBTTagList itemsList = tags.getTagList("Items");
        this.stacks = new ItemStack[this.getSizeInventory()];

        for (int item = 0; item < itemsList.tagCount(); item++) {
            NBTTagCompound compound = (NBTTagCompound)itemsList.tagAt(item);
            byte slot = compound.getByte("Slot");

            if (slot >= 0 && slot < this.stacks.length)
            	this.stacks[slot] = ItemStack.loadItemStackFromNBT(compound);
        }
    }
	
	@Override public void writeToNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);
        NBTTagList itemsList = new NBTTagList();

        for (int slot = 0; slot < this.stacks.length; ++slot) {
            if (this.stacks[slot] != null) {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte("Slot", (byte)slot);
                this.stacks[slot].writeToNBT(item);
                itemsList.appendTag(item);
            }
        }

        tags.setTag("Items", itemsList);
    }
	@Override public void setInventorySlotContents(int slot, ItemStack stack) {
        this.stacks[slot] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
            stack.stackSize = this.getInventoryStackLimit();
    }
	
	@Override public ItemStack getStackInSlotOnClosing(int slot) {
        if (this.stacks[slot] != null) {
            ItemStack out = this.stacks[slot];
            this.stacks[slot] = null;
            return out;
        } 
        return null;
    }
	
	@Override public ItemStack decrStackSize(int slot, int amt) {
        if (this.stacks[slot] != null) {
            ItemStack out;

            if (this.stacks[slot].stackSize <= amt) {
                out = this.stacks[slot];
                this.stacks[slot] = null;
                return out;
            }
			out = this.stacks[slot].splitStack(amt);

			if (this.stacks[slot].stackSize == 0) {
			    this.stacks[slot] = null;
			}

			return out;
        }
        return null;
    }
	@Override public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ?
		false :
			player.getDistanceSq(this.xCoord + 0.5D,
				this.yCoord + 0.5D,
				this.zCoord + 0.5D) <= 64.0D;
	}
	
	@Override public ItemStack getStackInSlot(int slot) { return this.stacks[slot]; }
	@Override public boolean isInvNameLocalized() { return false; }
	
	@Override public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if(0<=slot && slot<6) {
			return FurnaceRecipes.smelting().getSmeltingResult(stack)!=null || 
					getCustom()!=null && getCustom().containsKey(stack);
		} 
		else if(slot==12) return Commons.getItemBurnTime(stack)>0;
		return false;
	}
	@Override public int getInventoryStackLimit() { return 64; }
	@Override public void openChest()  { /* do nothing */ }
	@Override public void closeChest() { /* do nothing */ }
	
	@Override public int[] getAccessibleSlotsFromSide(int side) {
		if(side==1) return new int[] {0,1,2,3,4,5,12}; 		//top: material and fuel
		if(side==0) return new int[] {6,7,8,9,10,11,12}; 	//bottom: result and fuel (bucket)
		return new int[] {0,1,2,3,4,5,6,7,8,9,10,11,12}; 	//sides: material, fuel, result
	}
	@Override public boolean canInsertItem(int slot, ItemStack item, int side) {
		return isItemValidForSlot(slot, item);
	}
	@Override public boolean canExtractItem(int slot, ItemStack item, int side) {
		return slot==2 || item.itemID==Item.bucketEmpty.itemID;
	}
	@Override public Packet getDescriptionPacket() {
		NBTTagCompound data = new NBTTagCompound();
		writeToNBT(data);
		return new Packet132TileEntityData(this.xCoord,this.yCoord,this.zCoord,0,data);
	}
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNBT(pkt.data);
	}

}
