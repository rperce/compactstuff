package mods.CompactStuff.tools;

import java.util.List;

import mods.CompactStuff.CompactStuff;
import mods.CompactStuff.client.CSIcons;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CompactAxe extends ItemAxe {
	private String path;
	public CompactAxe(int id, EnumToolMaterial material, String path) {
		super(id,material);
		this.path = path;
		setCreativeTab(CompactStuff.compactTab);
	}
	@Override public void registerIcons(IconRegister ir) {
		itemIcon = ir.registerIcon(CSIcons.PREFIX+path);
	}
	@Override public boolean getIsRepairable(ItemStack thisOne, ItemStack otherOne) {
        return CompactTool.getIsRepairable(thisOne,otherOne,"Axe");
    }
	@Override public boolean onBlockDestroyed(ItemStack thisStack, World world, int blockSlot, int x, int y, int z, EntityLiving holder) {
		if(world.isRemote) return false;
		if(thisStack.itemID!=CompactStuff.heatAxe.itemID || !(holder instanceof EntityPlayer) || world.isAirBlock(x, y, z))
			return super.onBlockDestroyed(thisStack, world, blockSlot, x, y, z, holder);
		if(blockSlot==Block.wood.blockID) {
			int n = (world.rand.nextInt(10)<4?2:1);
			EntityItem drop = new EntityItem(world, x+.5, y+.5, z+.5, new ItemStack(Item.coal,n,1));
			world.setBlockToAir(x, y, z);
			thisStack.damageItem(1, holder);
			world.spawnEntityInWorld(drop);
			return true;
		} else if(blockSlot==Block.cactus.blockID) {
			int ty = y;
			EntityItem drop;
			while(world.getBlockId(x,ty,z)==Block.cactus.blockID) {
				drop = new EntityItem(world, x+.5, ty+.5, z+.5, new ItemStack(Item.dyePowder,1,2));
				world.setBlock(x,ty,z,0,0,2);
				thisStack.damageItem(1, holder);
				world.spawnEntityInWorld(drop);
				ty++;
			}
			return true;
		} return super.onBlockDestroyed(thisStack, world, blockSlot, x, y, z, holder);
	}
	@Override public void addInformation(ItemStack thisStack, EntityPlayer player, List list, boolean boo) {
		if(thisStack.itemID!=CompactStuff.heatAxe.itemID) return;
		list.add("Drops charcoal from logs");
		list.add("40% double charcoal");
	}
}
