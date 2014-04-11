package com.rperce.compactstuff.compactor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import com.rperce.compactstuff.Commons;
import com.rperce.compactstuff.Metas;
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

public class TileEntityCompactor extends TileEntity implements ISidedInventory {
	/**
	 * There are, in order, the following slots in the inventory:
	 * 3x9 inventory, 3x3 crafting grid, 1 crafting result, 3x2 compacting list
	 * 27 + 9 + 1 + 6 = 43
	 */
	ItemStack[] stacks = new ItemStack[27+9+1+6]; //3x9 inventory, nine crafting grid, 1 output, 6 compression.  in that order.
	public static final int INVFIRST = 0, INVLAST = 26, CRAFTFIRST = 27, CRAFTLAST = 35, OUTPUT = 36, COMFIRST = 37, COMLAST = 42;
	private Comparator sorter;
	public HashSet<ItemStack> enabled;
	public ContainerCompactor container;
	
	public synchronized HashSet<ItemStack> enabled() { return enabled; }
	public TileEntityCompactor() {
		super();
		sorter = new Comparator() {
			public int compare(Object a, Object b) {
				if(b==null) return -1;
				if(a==null || !(a instanceof ItemStack && b instanceof ItemStack) ) return 1;
				ItemStack aa = (ItemStack)a, bb=(ItemStack)b;
				int z = aa.itemID - bb.itemID;
				return z!=0? z : aa.getItemDamage() - bb.getItemDamage();
			}
		};
		enabled = new HashSet<ItemStack>();
		enabled().addAll(CompactorRecipes.defaultEnabled);
	}
		
	@Override public void onInventoryChanged() {
		if(worldObj.isRemote) return;
		ArrayList<ItemStack> tStacks = new ArrayList<ItemStack>();
		for(int i=INVFIRST; i<=INVLAST; i++) if(getStackInSlot(i)!=null) tStacks.add(getStackInSlot(i));
		Collections.sort(tStacks, sorter);
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
			if(i-INVFIRST>=tStacks.size()) stacks[i]=null;
			else stacks[i] = tStacks.get(i-INVFIRST);
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	@Override public void updateEntity() {
		if(worldObj.isRemote) return;
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
		else {
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
	}
	@Override public String getInvName() { return "compactstuff.compactor"; }
	@Override public int getSizeInventory() { return stacks.length; }
	@Override public ItemStack getStackInSlot(int slot) { return stacks[slot]; }
	public boolean addStackToSlot(int slot, ItemStack stack) {
		if(stacks[slot]==null) {
			this.setInventorySlotContents(slot, stack);
			return true;
		} else {
			if(!stack.isItemEqual(stacks[slot])) return false;
			if(stacks[slot].stackSize+stack.stackSize > stack.getMaxStackSize()) return false;
			stacks[slot].stackSize+=stack.stackSize;
			return true;
		}
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		if (stacks[slot] != null) {
            ItemStack out;

            if (stacks[slot].stackSize <= amt) {
                out = stacks[slot];
                stacks[slot] = null;
                return out;
            } else {
                out = stacks[slot].splitStack(amt);

                if (stacks[slot].stackSize == 0) {
                    stacks[slot] = null;
                }

                return out;
            }
        }
        return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (stacks[slot] != null) {
            ItemStack var2 = stacks[slot];
            stacks[slot] = null;
            return var2;
        } 
        return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		try {
			stacks[i] = itemstack;		
		} catch(ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}
	@Override public boolean isInvNameLocalized() { return false; }
	@Override public int getInventoryStackLimit() { return 64; }
	@Override public boolean isUseableByPlayer(EntityPlayer entityplayer) { return true; }
	@Override public void openChest() { }
	@Override public void closeChest() { }
	@Override public boolean isItemValidForSlot(int n, ItemStack i) {
		return INVFIRST<=n && n<=INVLAST;
	}
	
	@Override public void readFromNBT(NBTTagCompound tagList) {
        super.readFromNBT(tagList);
        NBTTagList itemList = tagList.getTagList("Items"), enabledList = tagList.getTagList("Enabled");
        stacks = Commons.readStacksFromNBT(tagList);        
        enabled = new HashSet<ItemStack>();
        for(int i=0; i<enabledList.tagCount(); i++) {
        	NBTTagCompound tags = (NBTTagCompound)enabledList.tagAt(i);
        	enabled.add(ItemStack.loadItemStackFromNBT(tags));
        }
    }
	
	@Override public void writeToNBT(NBTTagCompound tagList) {
        super.writeToNBT(tagList);
        NBTTagList enabledList = new NBTTagList();
        Commons.writeStacksToNBT(tagList, stacks);
        
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
		return !CompactorRecipes.isEnabledIngredient(enabled, item) && INVFIRST<=slot && slot<= INVLAST;
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound data = new NBTTagCompound();
		writeToNBT(data);
		return new Packet132TileEntityData(xCoord,yCoord,zCoord,0,data);
	}
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNBT(pkt.data);
	}
}