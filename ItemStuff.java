package compactstuff;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemStuff extends Item {
	public static final int
		GLASS_SLAG	= 0,
		GLASS_FIBER	= 1,
		DIAMOND_PLATE=2,
		ALLOY_PLATE	= 3,
		IRON_PLATE	= 4,
		STEEL_PLATE	= 5,
		STEEL_INGOT	= 6;
	
	public static final String[] names =
		{"Glass Slag", "Glass Fiber","Diamond Plating",
		 "Carbon Alloy Plating","Iron Plating","CS Steel Plating",
		 "CS Steel Ingot"};
	
	public ItemStuff(int id) {
		super(id);
		setHasSubtypes(true);
		setMaxStackSize(64);
		setCreativeTab(CompactStuff.compactTab);
		setItemName("itemstuff");
	}
	@Override public String getTextureFile() {
		return ImageFiles.ITEMS.path;
	}
	
	@Override public int getIconFromDamage(int dmg) {
		return 64+dmg;
	}
	
	@Override public String getItemNameIS(ItemStack i) {
		try { return names[i.getItemDamage()]; }
		catch(NullPointerException e) { return "Stuff"; }
	}
	
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs tab, List list) {
		for(int i=0; i<names.length; i++) {
			list.add(new ItemStack(id,1,i));
		}
	}

}
