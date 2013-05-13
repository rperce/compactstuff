package mods.CompactStuff.tools;

import java.util.List;

import mods.CompactStuff.CSIcons;
import mods.CompactStuff.CompactStuff;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CompactSpade extends ItemSpade {
	private String path;
	public CompactSpade(int id, EnumToolMaterial material, String path) {
		super(id,material);
		this.path = path;
		setCreativeTab(CompactStuff.compactTab);
	}
	@Override public void updateIcons(IconRegister ir) {
		iconIndex = ir.registerIcon(CSIcons.PREFIX + path);
	}
	@Override public boolean getIsRepairable(ItemStack thisOne, ItemStack otherOne) {
        return CompactTool.getIsRepairable(thisOne,otherOne,"Spade");
    }
	
	@Override public boolean onBlockDestroyed(ItemStack thisStack, World world, int blockSlot, int x, int y, int z, EntityLiving holder) {
		if(thisStack.itemID!=CompactStuff.heatSpade.itemID || !(holder instanceof EntityPlayer) || world.isAirBlock(x, y, z) || world.isRemote)
			return super.onBlockDestroyed(thisStack, world, blockSlot, x, y, z, holder);
		if(blockSlot==Block.sand.blockID) {
			EntityItem drop = new EntityItem(world, x+.5, y+.5, z+.5, new ItemStack(Block.glass, (world.rand.nextInt(10)<4?2:1)));
			world.setBlockToAir(x, y, z);
			thisStack.damageItem(1, holder);
			world.spawnEntityInWorld(drop);
			
			if(world.rand.nextInt(200)==0)
				world.spawnEntityInWorld(new EntityItem(world, x+.5, y+.5, z+.5, new ItemStack(CompactStuff.comGlass)));
			return true;
		} else if(blockSlot==Block.blockClay.blockID) {
			EntityItem drop = new EntityItem(world, x+.5, y+.5, z+.5, new ItemStack(Item.brick,(world.rand.nextInt(10)<4?8:4)));
			world.setBlockToAir(x,y,z);
			thisStack.damageItem(1, holder);
			world.spawnEntityInWorld(drop);
			return true;
		}
		
		return super.onBlockDestroyed(thisStack, world, blockSlot, x, y, z, holder);
	}
	@Override public void addInformation(ItemStack thisStack, EntityPlayer player, List list, boolean boo) {
		if(thisStack.itemID!=CompactStuff.heatSpade.itemID) return;
		list.add("Drops glass from sand");
		list.add("Drops bricks from clay blocks");
		list.add("40% double bricks and glass");
		list.add("0.5% Compressed Glass from sand");
	}
}
