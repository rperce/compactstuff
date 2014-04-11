package com.rperce.compactstuff;

import java.util.Arrays;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class Commons {
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
			System.err.println("That's not a good idea, broseph.  Dem stacks null.");
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
		for(int i=0; i<itemsList.tagCount(); i++) {
			NBTTagCompound item = (NBTTagCompound)itemsList.tagAt(i);
			ItemStack stack = ItemStack.loadItemStackFromNBT(item);
			int slot = item.getByte("Slot") & 255;
			if(slot>=0 && slot<stacks.length) 
				stacks[slot] = stack;
		}
		return stacks;
	}
	public static int getItemBurnTime(ItemStack stack) {
        if (stack == null) return 0;
        else {
            int id = stack.getItem().itemID;
            Item item = stack.getItem();

            if (stack.getItem() instanceof ItemBlock && Block.blocksList[id] != null) {
                Block var3 = Block.blocksList[id];

                if (var3 == Block.woodSingleSlab)
                    return 150;

                if (var3.blockMaterial == Material.wood)
                    return 300;
            }
            if (item instanceof ItemTool && ((ItemTool) item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemSword && ((ItemSword) item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemHoe && ((ItemHoe) item).getMaterialName().equals("WOOD")) return 200;
            if (id == Item.stick.itemID) return 100;
            if (id == Item.coal.itemID) return 1600;
            if (id == Item.bucketLava.itemID) return 20000;
            if (id == Block.sapling.blockID) return 100;
            if (id == Item.blazeRod.itemID) return 2400;
            return GameRegistry.getFuelValue(stack);
        }
    }
}
