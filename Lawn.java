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
		NBTTagList itemsList = new NBTTagList();
		ItemStack stack=stacks[0];
		for(int i=0; i<stacks.length; i++) {
			stack=stacks[i];
			if(stack!=null) {
				NBTTagCompound item = new NBTTagCompound();
				stack.writeToNBT(item);
				item.setByte("Slot", (byte)i);
				itemsList.appendTag(item);
			}
		}
		data.setTag("itemsList", itemsList);
		data.setShort("itemsListLength", (short)stacks.length);
		return data;
	}
	
	public static ItemStack[] readStacksFromNBT(NBTTagCompound data) {
		if(!data.hasKey("itemsListLength") || !data.hasKey("itemsList")) {
			System.err.println("Tried to read stacks from an NBTTagCompound that doesn't have stacks");
			return null;
		}
		ItemStack[] stacks = new ItemStack[data.getShort("itemsListLength")];
		Arrays.fill(stacks, null);
		NBTTagList itemsList = data.getTagList("itemsList");
		for(int i=0; i<stacks.length; i++) {
			NBTTagCompound item = (NBTTagCompound)itemsList.tagAt(i);
			ItemStack stack = ItemStack.loadItemStackFromNBT(item);
			stacks[item.getByte("Slot")] = stack;
		}
		return stacks;
	}
}
