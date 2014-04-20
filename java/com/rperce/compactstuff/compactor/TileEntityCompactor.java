package com.rperce.compactstuff.compactor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

import com.rperce.compactstuff.Commons;
import com.rperce.compactstuff.Metas;

public class TileEntityCompactor extends TileEntity implements ISidedInventory {
	/**
	 * There are, in order, the following slots in the inventory:
	 * 3x9 inventory, 3x3 crafting grid, 1 crafting result, 3x2 compacting list
	 * 27 + 9 + 1 + 6 = 43
	 */
	ItemStack[] stacks = new ItemStack[27+9+1+6]; //3x9 inventory, nine crafting grid, 1 output, 6 compression.  in that order.
	public static final int INVFIRST = 0, INVLAST = 26, CRAFTFIRST = 27, CRAFTLAST = 35, OUTPUT = 36, COMFIRST = 37, COMLAST = 42;
	private Comparator<ItemStack> sorter;
	public HashSet<ItemStack> enabled;
	public ContainerCompactor container;
	
	public synchronized HashSet<ItemStack> enabled() { return this.enabled; }
	public TileEntityCompactor() {
		super();
		this.sorter = new Comparator<ItemStack>() {
			@Override
			public int compare(ItemStack a, ItemStack b) {
				if(b==null) return -1;
				if(a==null) return 1;
				int z = a.itemID - b.itemID;
				return z!=0? z : a.getItemDamage() - b.getItemDamage();
			}
		};
		this.enabled = new HashSet<ItemStack>();
		enabled().addAll(CompactorRecipes.defaultEnabled);
	}
		
	@Override public void onInventoryChanged() {
		if(this.worldObj.isRemote) return;
		ArrayList<ItemStack> tStacks = new ArrayList<ItemStack>();
		for(int i=INVFIRST; i<=INVLAST; i++) if(getStackInSlot(i)!=null) tStacks.add(getStackInSlot(i));
		Collections.sort(tStacks, this.sorter);
		for(int i=0; i<tStacks.size()-1; i++) {
			ItemStack cur = tStacks.get(i);
			if(cur.stackSize==cur.getMaxStackSize()) continue;
			if(Commons.areShallowEqual(cur,tStacks.get(i+1))) {
				int transfer = Math.min(cur.getMaxStackSize()-cur.stackSize, tStacks.get(i+1).stackSize);
				tStacks.get(i+1).stackSize-=transfer;
				tStacks.get(i).stackSize+=transfer;
				if(tStacks.get(i+1).stackSize<1) tStacks.remove(i+1);
				i--;
			}
		}
		for(int i=INVFIRST; i<=INVLAST; i++) {
			if(i-INVFIRST>=tStacks.size()) this.stacks[i]=null;
			else this.stacks[i] = tStacks.get(i-INVFIRST);
		}
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}
	@Override public void updateEntity() {
		if(this.worldObj.isRemote) return;
		for(IRecipe r : CompactorRecipes.getEnabledRecipes(enabled())) {
			if(tryToMake(r)==0) break;
		}
	}
	
	public int tryToMake(IRecipe r) {
		if(r==null) return -1;
		List<ItemStack> reqs = CompactorRecipes.getRequirements(r);
		int[] indices = new int[reqs.size()];
		Arrays.fill(indices, -1);
		int i = INVFIRST;
		for(int o=0; o<reqs.size(); o++) {
			ItemStack req = reqs.get(o);
			i = INVFIRST;
			for(; i<=INVLAST; i++) {
				ItemStack stack = getStackInSlot(i);
				if(stack==null) break;
				if(Commons.areShallowEqual(stack, req) && stack.stackSize>=req.stackSize) {
					indices[o] = i;
					break;
				}
			}
			if(indices[o]==-1) {
				i = -1;
				break;
			}
		}
		if(i==-1) return -1;
		for(int j=0; j<indices.length; j++) {
			getStackInSlot(indices[j]).stackSize-=reqs.get(j).stackSize;
			if(getStackInSlot(indices[j]).stackSize<1) setInventorySlotContents(indices[j],null);
		}
		ItemStack out = r.getRecipeOutput().copy();
		for(int j=INVFIRST; j<=INVLAST; j++) {
			ItemStack cur = getStackInSlot(j);
			if(getStackInSlot(j)==null) {
				setInventorySlotContents(j,out);
				break;
			} else if(Commons.areShallowEqual(cur,out) && cur.getMaxStackSize()-cur.stackSize >= out.stackSize) {
				getStackInSlot(j).stackSize+=out.stackSize;
				break;
			}

		}
		onInventoryChanged();
		return 0;
	}
	@Override public String getInvName() { return "compactstuff.compactor"; }
	@Override public int getSizeInventory() { return this.stacks.length; }
	@Override public ItemStack getStackInSlot(int slot) { return this.stacks[slot]; }
	public boolean addStackToSlot(int slot, ItemStack stack) {
		if(this.stacks[slot]==null) {
			this.setInventorySlotContents(slot, stack);
			return true;
		}
		if(!stack.isItemEqual(this.stacks[slot])) return false;
		if(this.stacks[slot].stackSize+stack.stackSize > stack.getMaxStackSize()) return false;
		this.stacks[slot].stackSize+=stack.stackSize;
		return true;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
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

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (this.stacks[slot] != null) {
            ItemStack var2 = this.stacks[slot];
            this.stacks[slot] = null;
            return var2;
        } 
        return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		try {
			this.stacks[i] = itemstack;		
		} catch(ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}
	@Override public boolean isInvNameLocalized() { return false; }
	@Override public int getInventoryStackLimit() { return 64; }
	@Override public boolean isUseableByPlayer(EntityPlayer entityplayer) { return true; }
	@Override public void openChest() { /* don't do anything */ }
	@Override public void closeChest() { /* don't do anything */ }
	@Override public boolean isItemValidForSlot(int n, ItemStack i) {
		return INVFIRST<=n && n<=INVLAST;
	}
	
	@Override public void readFromNBT(NBTTagCompound tagList) {
        super.readFromNBT(tagList);
        tagList.getTagList("Items");
		NBTTagList enabledList = tagList.getTagList("Enabled");
		this.stacks = Commons.readStacksFromNBT(tagList);        
        this.enabled = new HashSet<ItemStack>();
        for(int i=0; i<enabledList.tagCount(); i++) {
        	NBTTagCompound tags = (NBTTagCompound)enabledList.tagAt(i);
        	this.enabled.add(ItemStack.loadItemStackFromNBT(tags));
        }
    }
	
	@Override public void writeToNBT(NBTTagCompound tagList) {
        super.writeToNBT(tagList);
        NBTTagList enabledList = new NBTTagList();
        Commons.writeStacksToNBT(tagList, this.stacks);
        
        for(ItemStack stack : enabled()) {
        	NBTTagCompound tag = new NBTTagCompound();
        	stack.writeToNBT(tag);
        	enabledList.appendTag(tag);
        }
        tagList.setTag("Enabled",enabledList);
    }

	@Override public int[] getAccessibleSlotsFromSide(int side) {
		int[] out = new int[27];
		for(int i=0; i<27; i++) out[i] = i;
		return out;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		if(side==Metas.MINUS_Y) return false;
		return isItemValidForSlot(slot, item);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		if(side==Metas.PLUS_Y) return false;
		return !CompactorRecipes.isEnabledIngredient(this.enabled, item) && INVFIRST<=slot && slot<= INVLAST;
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound data = new NBTTagCompound();
		writeToNBT(data);
		return new Packet132TileEntityData(this.xCoord,this.yCoord,this.zCoord,0,data);
	}
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNBT(pkt.data);
	}
}