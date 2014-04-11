package com.rperce.compactstuff.furnace;

import java.util.Random;

import com.rperce.compactstuff.CompactStuff;
import com.rperce.compactstuff.client.CSIcons;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/*
 * 4-bit metadata format:
 * Meta		0000
 * Bit no. (0123)
 * 
 * Bit 0: 0 -> Empty			1 -> Fueled
 * Bit 1: 0 -> Inactive			1 -> Active
 * Bit 2/3: Front side:
 * 	00 -> 2
 * 	01 -> 5
 *  10 -> 3
 *  11 -> 4
 */
public class BlockBlazeFurnace extends BlockCompactFurnace {
	Icon[] icons = new Icon[4];
	public BlockBlazeFurnace(int id) {
		super(id);
	}
	
	@Override public int idPicked(World world, int x, int y, int z) { return CompactStuff.blazeFurn.blockID; }
	@Override public int idDropped(int a, Random b, int c) { return CompactStuff.blazeFurn.blockID; }
	@Override public TileEntity createTileEntity(World w, int m) { return new TileEntityBlazeFurnace(); }
	@Override public int damageDropped(int meta) { return 2; }
	@Override public void registerIcons(IconRegister ir) {
		icons[0] = ir.registerIcon(CSIcons.PREFIX+CSIcons.BLAZEFURNACE_TOP);
		icons[1] = ir.registerIcon(CSIcons.PREFIX+CSIcons.BLAZEFURNACE_SIDE);
		icons[2] = ir.registerIcon(CSIcons.PREFIX+CSIcons.BLAZEFURNACE_FRONT_INACTIVE);
		icons[3] = ir.registerIcon(CSIcons.PREFIX+CSIcons.BLAZEFURNACE_FRONT_ACTIVE);
	}
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,EntityPlayer player, int a, float b, float c, float d) {
		if(player.isSneaking()) return true;
        if (world.isRemote) return true;
        
    	TileEntityBlazeFurnace tileEntity = (TileEntityBlazeFurnace)world.getBlockTileEntity(x, y, z);
		
		if (tileEntity != null) {
			player.openGui(CompactStuff.instance, 5, world, x, y, z);
		}
		
		return true;
	}
	@Override public Icon getBlockTexture(IBlockAccess ba,int x,int y,int z, int s) {
		if(s==0 || s==1) return icons[0]; 
		TileEntityBlazeFurnace te = (TileEntityBlazeFurnace)(ba.getBlockTileEntity(x, y, z));
		int front = getFront(ba,x,y,z);
		boolean isActive = isActive(ba,x,y,z);
		return icons[s==front ? (isActive ? 3 : 2) : 1];
	}
	
	@Override public Icon getIcon(int side, int meta) {
		return icons[(side==1||side==0) ? 0 : (side==3 ? 2 : 1)];
	}
}
