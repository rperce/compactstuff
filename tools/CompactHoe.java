package mods.CompactStuff.tools;

import java.util.List;

import mods.CompactStuff.CSIcons;
import mods.CompactStuff.CompactStuff;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class CompactHoe extends ItemHoe {
	private String path;
	public CompactHoe(int id, EnumToolMaterial material, String path) {
		super(id,material);
		this.path = path;
		setCreativeTab(CompactStuff.compactTab);
	}
	@Override public void updateIcons(IconRegister ir) {
		iconIndex = ir.registerIcon(CSIcons.PREFIX + path);
	}
	@Override public boolean getIsRepairable(ItemStack thisOne, ItemStack otherOne) {
        return CompactTool.getIsRepairable(thisOne,otherOne,"Hoe");
    }
	
	@Override public boolean onItemUse(ItemStack thisStack, EntityPlayer player, World world, int x, int y, int z, int these, float vars, float do_, float nothing) {
		for(int i=x-4; i<=x+4; i++) {
			for(int j=z-4; j<=z+4; j++) {
				if(world.isAirBlock(i, y+1, j) && (world.getBlockId(i, y, j)==Block.grass.blockID || world.getBlockId(i, y, j)==Block.dirt.blockID)) {
					world.setBlock(i, y, j, Block.tilledField.blockID);
				}
			}
		}
		return true;
	}
	@Override public void addInformation(ItemStack thisStack, EntityPlayer player, List list, boolean boo) {
		if(thisStack.itemID!=CompactStuff.heatHoe.itemID) return;
		list.add("Tills a 9x9 area on right-click");
	}
}
