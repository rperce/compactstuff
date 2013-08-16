package mods.CompactStuff;

import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;

public class Lawn { //"Commons", if you will
	public static boolean areShallowEqual(ItemStack a, ItemStack b) {
    	if(a==null) return b==null;
    	if(b==null) return a==null;
    	return a.itemID==b.itemID && a.getItemDamage()==b.getItemDamage();
    }
	
	public static NBTTagCompound writeStacksToNBT(NBTTagCompound data, ItemStack... stacks) {
		return writeStacksToNBT(data,"itemsList",stacks);
	}

	public static NBTTagCompound writeStacksToNBT(NBTTagCompound data, String string, ItemStack... stacks) {
		if(stacks==null) {
			System.err.println("That's not a good idea, broseph.  Dem stacks null");
			return data;
		}
		NBTTagList itemsList = new NBTTagList();
		for(int i=0; i<stacks.length; i++) {
			ItemStack stack=stacks[i];
			if(stack!=null) {
				NBTTagCompound item = new NBTTagCompound();
				stack.writeToNBT(item);
				item.setByte("Slot", (byte)i);
				itemsList.appendTag(item);
			}
		}
		data.setTag(string, itemsList);
		data.setShort(string+"Length", (short)stacks.length);
		return data;
	}
	
	public static ItemStack[] readStacksFromNBT(NBTTagCompound data) {
		return readStacksFromNBT(data,"itemsList");
	}
	public static ItemStack[] readStacksFromNBT(NBTTagCompound data, String string) {
		if(!data.hasKey(string+"Length") || !data.hasKey(string)) {
			System.err.println("Tried to read stacks from an NBTTagCompound that doesn't have stacks");
			return null;
		}
		ItemStack[] stacks = new ItemStack[data.getShort(string+"Length")];
		Arrays.fill(stacks, null);
		NBTTagList itemsList = data.getTagList(string);
		for(int i=0; i<stacks.length; i++) {
			NBTTagCompound item = (NBTTagCompound)itemsList.tagAt(i);
			ItemStack stack = ItemStack.loadItemStackFromNBT(item);
			int slot = item.getByte("Slot") & 255;
			if(slot>=0 && slot<stacks.length) 
				stacks[slot] = stack;
		}
		return stacks;
	}

	
}
