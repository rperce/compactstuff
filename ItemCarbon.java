package compactstuff;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCarbon extends Item {
	private String[] names = {"Carbon Wafer","Carbon","Dense Carbon","Compressed Carbon","Heated Compressed Carbon","Carbon Fiber","Woven Carbon Fiber" };
	public ItemCarbon(int id) {
		super(id);
		setHasSubtypes(true);
		setMaxStackSize(64);
		setCreativeTab(CompactStuff.compactTab);
		setItemName("carbon");
	}
	
	@Override public String getTextureFile() {
		return ImageFiles.ITEMS.path;
	}
	
	@Override public int getIconFromDamage(int dmg) {
		return 16+dmg;
	}
	
	@Override public String getItemNameIS(ItemStack i) {
		try { return names[i.getItemDamage()]; }
		catch(NullPointerException e) { return "Carbon"; }
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
}
