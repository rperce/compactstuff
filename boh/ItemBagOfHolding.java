package compactstuff.boh;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import compactstuff.ImageFiles;
import compactstuff.CompactStuff;

public class ItemBagOfHolding extends Item {
	static int rows;
	public ItemBagOfHolding(int id) {
		super(id);
		setIconIndex(32);
		setMaxStackSize(1);
		this.rows = 3;
		this.setItemName("Bag of Holding");
		this.setCreativeTab(CompactStuff.compactTab);
	}
	
	/*@Override public boolean getShareTag() {
		return true;
	}*/
	
	@Override public String getTextureFile() {
		return ImageFiles.ITEMS.path;
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
}
