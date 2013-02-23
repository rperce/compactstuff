package compactstuff.boh;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import compactstuff.ImageFiles;
import compactstuff.CompactStuff;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBagOfHolding extends Item {
	public static final int[][] COLOR_CRAFTING = {
		//      0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 },
		{/*0*/ -1, -1, -1, -1, -1, -1, -1,  8,  0, -1, -1, -1, -1, -1, -1,  7 },
		{/*1*/ -1, -1,  3, -1,  5,  1, -1, -1, -1,  1, -1, 14, -1, -1,  1,  9 },
		{/*2*/ -1,  3, -1, -1,  6, -1,  2, -1, -1, -1,  2, -1, -1, -1, -1, 10 },
		{/*3*/ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  3 },
		{/*4*/ -1,  5,  6, -1, -1,  4,  4, -1, -1, -1, -1, -1,  4, -1,  3, 12 },
		{/*5*/ -1, -1, -1, -1, -1, -1, -1, -1, -1, 13, -1,  3, -1,  5, -1,  5 },
		{/*6*/ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  6 },
		{/*7*/ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  7 },
		{/*8*/ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  8 },
		{/*9*/ -1, -1, -1, -1, -1, 13, -1, -1, -1, -1, -1, -1, -1,  9, -1,  9 },
		{/*10*/-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10 },
		{/*11*/-1, 14, -1, -1, -1,  3, -1, -1, -1, -1, -1, -1, -1, -1, 11, 11 },
		{/*12*/-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12 },
		{/*13*/-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 13 },
		{/*14*/-1, -1, -1, -1,  3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 14 },
		{/*15*/ 8,  9, 10, 15, 12, 15, 15, 15,  7, 15, 15, 15, 15, 15, 15, -1 },
	};
	static int rows;
	public ItemBagOfHolding(int id) {
		super(id);
		setHasSubtypes(true);
		setIconIndex(80);
		setMaxStackSize(1);
		this.rows = 3;
		this.setItemName("Bag of Holding");
		this.setCreativeTab(CompactStuff.compactTab);
	}
		
	@Override public String getTextureFile() {
		return ImageFiles.ITEMS.path;
	}
	@Override public int getIconFromDamage(int meta) {
		return 80+meta;
	}	
	@Override public ItemStack onItemRightClick(ItemStack thisStack, World world, EntityPlayer player) {
		player.openGui(CompactStuff.instance, 2, world, (int)player.posX, (int)player.posY, (int)player.posZ);
		return thisStack;
	}	
	public static InventoryBagOfHolding getInventory(EntityPlayer player) {
		InventoryBagOfHolding out = null;
		ItemStack held = player.getHeldItem();
		if(held!=null && held.getItem() instanceof ItemBagOfHolding) {
			out = new InventoryBagOfHolding(rows,held);
		}
		if(out==null) System.err.println("Bag of holding is null!");
		return out;		
	}	
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs tab, List list) {
		for(int meta=0; meta<16; meta++)
			list.add(new ItemStack(id,1,meta));
	}
}
