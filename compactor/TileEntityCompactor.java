package mods.CompactStuff.compactor;

import java.util.HashMap;

import mods.CompactStuff.CompactStuff;
import mods.CompactStuff.ItemStuff;
import mods.CompactStuff.Metas;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileEntityCompactor extends TileEntity implements ISidedInventory {
	/**
	 * There are, in order, the following slots in the inventory:
	 * 3x9 inventory, 3x3 crafting grid, 1 crafting result, 3x2 compacting list
	 * 27 + 9 + 1 + 6 = 43
	 */
	ItemStack[] stacks = new ItemStack[27+9/*+1+6*/]; //3x9 inventory, nine crafting grid.  in that order.
	public static final int INVFIRST = 0, INVLAST = 26, CRAFTFIRST = 27, CRAFTLAST = 35, OUTPUT = 36, COMFIRST = 37, COMLAST = 42;
	private HashMap<ItemStack, ItemStack> compactables = new HashMap<ItemStack, ItemStack>() {
		@Override public boolean containsKey(Object o) {
			if(!(o instanceof ItemStack)) return false;
			ItemStack item = (ItemStack)o;
			for(ItemStack key : this.keySet())
				if(key.itemID == item.itemID && key.stackSize == item.stackSize && key.getItemDamage() == item.getItemDamage())
					return true;
			return false;
		}
	};
		
	public TileEntityCompactor() { 
		super();
		compactables.put(new ItemStack(Block.sapling,8,0), new ItemStack(CompactStuff.plantBall, 1, 0));
		compactables.put(new ItemStack(Block.sapling,8,1), new ItemStack(CompactStuff.plantBall, 1, 1));
		compactables.put(new ItemStack(Block.sapling,8,2), new ItemStack(CompactStuff.plantBall, 1, 2));
		compactables.put(new ItemStack(Block.sapling,8,3), new ItemStack(CompactStuff.plantBall, 1, 3));
		compactables.put(new ItemStack(Item.seeds,8), new ItemStack(CompactStuff.plantBall, 1, 4));
		compactables.put(new ItemStack(Item.coal,8), new ItemStack(CompactStuff.comBlock, 1, Metas.COMCOAL));
		compactables.put(new ItemStack(Block.cobblestone,9), new ItemStack(CompactStuff.comBlock, 1, Metas.COMCOBBLE));
		compactables.put(new ItemStack(Block.dirt,9), new ItemStack(CompactStuff.comBlock, 1, Metas.COMDIRT));
		compactables.put(new ItemStack(Block.gravel,9), new ItemStack(CompactStuff.comBlock, 1, Metas.COMGRAVEL));
		compactables.put(new ItemStack(Block.netherrack,9), new ItemStack(CompactStuff.comBlock, 1, Metas.COMRACK));
		compactables.put(new ItemStack(Block.sand,9), new ItemStack(CompactStuff.comBlock, 1, Metas.COMSAND));
		compactables.put(new ItemStack(Item.clay,4), new ItemStack(Block.blockClay));
		compactables.put(new ItemStack(Item.diamond,9), new ItemStack(Block.blockDiamond));
		compactables.put(new ItemStack(Item.emerald,9), new ItemStack(Block.blockEmerald));
		compactables.put(new ItemStack(Item.ingotGold,9), new ItemStack(Block.blockGold));
		compactables.put(new ItemStack(Item.dyePowder, 9, Metas.DYE_BLUE), new ItemStack(Block.blockLapis));
		compactables.put(new ItemStack(Item.netherQuartz, 9), new ItemStack(Block.blockNetherQuartz));
		compactables.put(new ItemStack(Item.redstone, 9), new ItemStack(Block.blockRedstone));
		compactables.put(new ItemStack(Item.snowball, 4), new ItemStack(Block.blockSnow));
		compactables.put(new ItemStack(Item.ingotIron, 9), new ItemStack(Block.blockIron));
		compactables.put(new ItemStack(Item.brick, 4), new ItemStack(Block.brick));
		compactables.put(new ItemStack(Item.netherrackBrick, 4), new ItemStack(Block.netherBrick));
		compactables.put(ItemStuff.stack(ItemStuff.STEEL_INGOT, 9), new ItemStack(CompactStuff.comBlock, 1, Metas.STEELBLOCK));
	}
	
	@Override public void updateEntity() {
		for(int i=0; i<getSizeInventory(); i++) {
			ItemStack stack = stacks[i];
			if(stack==null) continue;
			boolean done = false;
			for(ItemStack input : compactables.keySet()) {
				if(stack.isItemEqual(input) && stack.stackSize >= input.stackSize) {
					int spot = -1;
					for(int j=0; j<getSizeInventory(); j++) {
						if(stacks[j]==null ||
						(stacks[j].isItemEqual(compactables.get(input)) &&
						stacks[j].stackSize+compactables.get(input).stackSize<=compactables.get(input).getMaxStackSize())) {
							spot = j;
							break;
						}
					}
					if(spot>-1) {
						decrStackSize(i, input.stackSize);
						addStackToSlot(spot, new ItemStack(compactables.get(input).getItem(), compactables.get(input).stackSize, compactables.get(input).getItemDamage()));
					}
					done = true;
					break;
				}
			}
			if(done) {
				onInventoryChanged();
				break;			
			}
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
	@Override public boolean isStackValidForSlot(int n, ItemStack i) {
		return INVFIRST<=n && n<=INVLAST;
	}
	
	@Override public void readFromNBT(NBTTagCompound tagList) {
        super.readFromNBT(tagList);
        NBTTagList itemList = tagList.getTagList("Items");
        stacks = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < itemList.tagCount(); i++) {
            NBTTagCompound tags = (NBTTagCompound)itemList.tagAt(i);
            byte slot = tags.getByte("Slot");

            if (slot >= 0 && slot < this.stacks.length)
            	stacks[slot] = ItemStack.loadItemStackFromNBT(tags);
        }
    }
	
	@Override public void writeToNBT(NBTTagCompound tagList) {
        super.writeToNBT(tagList);
        NBTTagList itemList = new NBTTagList();

        for (int i = 0; i < stacks.length; i++) {
            if (stacks[i] != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte)i);
                stacks[i].writeToNBT(tag);
                itemList.appendTag(tag);
            }
        }

        tagList.setTag("Items", itemList);
    }

	@Override public int[] getAccessibleSlotsFromSide(int side) {
		int[] out = new int[27];
		for(int i=0; i<27; i++) out[i] = i;
		return out;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		if(side==Metas.MINUS_Y) return false;
		return isStackValidForSlot(slot, item);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		if(side==Metas.PLUS_Y) return false;
		return !compactables.containsKey(item) && INVFIRST<=slot && slot<= INVLAST;
	}
}
