package com.rperce.compactstuff.furnace;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCompactFurnace extends ItemBlock {
	public ItemCompactFurnace(int id) {
		super(id);
		setHasSubtypes(true);
	}
	
	@Override public int getMetadata(int d) {
		return d<8 ? 2 : 10;
	}

	/** Warnings suppressed due to override constraints */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SideOnly(Side.CLIENT)
	@Override public void getSubItems(int i, CreativeTabs tab, List list) {
		list.add(new ItemStack(i,1,2));
		list.add(new ItemStack(i,1,10));
	}
	
	@Override public String getUnlocalizedName(ItemStack i) {
		return i.getItemDamage()<8?"Compression Furnace":"Carbon Furnace";
	}
}
