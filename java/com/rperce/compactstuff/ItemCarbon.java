package com.rperce.compactstuff;

import java.util.List;

import com.rperce.compactstuff.client.CSIcons;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCarbon extends Item {
	public static String[] names = {"Impure Carbon","Carbon","Dense Carbon","Compressed Carbon","Metamorphic Carbon","Carbon Fiber","Woven Carbon Fiber" };
	private static Icon[] icons = new Icon[names.length];
	public ItemCarbon(int id) {
		super(id);
		setHasSubtypes(true);
		setMaxStackSize(64);
		setCreativeTab(CompactStuff.compactTab);
		setUnlocalizedName("carbon");
	}
	
	@Override public void registerIcons(IconRegister ir) {
		for(int i=0; i<icons.length; i++) {
			icons[i] = ir.registerIcon(CSIcons.PREFIX+"carbon"+i);
		}
	}
	
	@Override public Icon getIconFromDamage(int dmg) {
		return icons[dmg];
	}
	
	@Override public String getUnlocalizedName(ItemStack i) {
		try { return names[i.getItemDamage()]; }
		catch(NullPointerException e) { return "Mysterious Carbon"; }
	}
	
	@SideOnly(Side.CLIENT)
	public void getSubItems(int i, CreativeTabs tab, List list) {
		list.add(new ItemStack(i,1,0));
		list.add(new ItemStack(i,1,1));
		list.add(new ItemStack(i,1,2));
		list.add(new ItemStack(i,1,3));
		list.add(new ItemStack(i,1,4));
		list.add(new ItemStack(i,1,5));
		list.add(new ItemStack(i,1,6));
	}

	public static ItemStack stack(int meta) {
		return stack(meta,1);
	} public static ItemStack stack(int meta, int amt) {
		return new ItemStack(CompactStuff.carbon, amt, meta);
	}
}
