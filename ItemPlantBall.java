package compactstuff;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPlantBall extends Item {
	private String[] names = {"Oak Sapling Ball","Spruce Sapling Ball","Birch Sapling Ball","Jungle Sapling Ball","Seed Ball"};
	public ItemPlantBall(int id) {
		super(id);
		setHasSubtypes(true);
		setMaxStackSize(64);
		setCreativeTab(CompactStuff.compactTab);
		setItemName("seedBall");
	}
	public String getTextureFile() {
		return ImageFiles.ITEMS.path;
	}
	public int getIconFromDamage(int dmg) {
		return dmg;
	}
	public String getItemNameIS(ItemStack i) {
		try { return names[i.getItemDamage()]; }
		catch(NullPointerException e) { return "Seed Ball"; }
	}
	
	@SideOnly(Side.CLIENT)
	public void getSubItems(int i, CreativeTabs tab, List list) {
		list.add(new ItemStack(i,1,0));
		list.add(new ItemStack(i,1,1));
		list.add(new ItemStack(i,1,2));
		list.add(new ItemStack(i,1,3));
		list.add(new ItemStack(i,1,4));
	}
}
