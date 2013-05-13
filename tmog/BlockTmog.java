package mods.CompactStuff.tmog;

import java.util.HashMap;

import mods.CompactStuff.CSIcons;
import mods.CompactStuff.CompactStuff;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * 4-bit metadata format:
 * bit 3: unused
 * bit 2: 0 -> invalid	1 -> valid
 * bit 1,0:
 * 		00 -> core
 * 		01 -> shield
 * 		10 -> frame
 * e.g. valid core is 0101
 */
public class BlockTmog extends BlockContainer {
	public static HashMap<Integer,Icon> icons = new HashMap<Integer,Icon>();
	public BlockTmog(int id) {
		super(id, Material.iron);
		setHardness(4f);
		setStepSound(Block.soundMetalFootstep);
		setUnlocalizedName("transmogrifier");
		setCreativeTab(CompactStuff.compactTab);		
	}
	
	@Override public void registerIcons(IconRegister ir) {
		icons.put(0, ir.registerIcon(CSIcons.PREFIX+"tmogCoreInvalid"));
		icons.put(1, ir.registerIcon(CSIcons.PREFIX+"tmogShieldInvalid"));
		icons.put(2, ir.registerIcon(CSIcons.PREFIX+"tmogFrameInvalid"));
		icons.put(4, ir.registerIcon(CSIcons.PREFIX+"tmogCoreValid"));
		icons.put(5, ir.registerIcon(CSIcons.PREFIX+"tmogShieldValid"));
		icons.put(6, ir.registerIcon(CSIcons.PREFIX+"tmogFrameValid"));
	}
	
	@Override public int damageDropped(int meta) {
		return meta & 3; //strips everything but the last two bits
	}
	
	public static boolean isValid(IBlockAccess w, int x, int y, int z) {
		return (w.getBlockMetadata(x, y, z)&4)==4;
	}
	public static void setValid(World w, int x, int y, int z, boolean i) {
		int noo = w.getBlockMetadata(x, y, z); //pronounced like "new", a reserved word.
		if(i) noo |= 4; //x0xx | 0100 = x1xx; x1xx | 0100 = x1xx
		else noo &= 11; //x0xx & 1011 = x0xx; x1xx & 1011 = x0xx
		w.setBlockMetadataWithNotify(x, y, z, noo, 3);
	}
	
	@Override public Icon getBlockTexture(IBlockAccess ba,int x,int y,int z, int s) {
		return icons.get(ba.getBlockMetadata(x, y, z));
	}
	
	@Override public Icon getBlockTextureFromSideAndMetadata(int side, int meta) {
		return icons.get(meta);
	}
		
	@Override public boolean onBlockActivated(World world, int x, int y, int z, 
	EntityPlayer player, int a, float b, float c, float d) {
		if(player.isSneaking() || world.isRemote || !isValid(world,x,y,z)) return false;
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te!=null && (te instanceof TileEntityTransmog)) {
			((TileEntityTransmog)te).clicked(player);
		}
		return true;
	}
	
	@Override public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving placer, ItemStack stack) {
		if(placer==null) {
			setValid(world, x, y, z, false);
			return;
		} if(world.isRemote) return;
		world.setBlockMetadataWithNotify(x, y, z, placer.getHeldItem().getItemDamage(), 0x02 | 0x01);
		TileEntityTransmog tet = (TileEntityTransmog)createTileEntity(world, world.getBlockMetadata(x, y, z));
		System.out.printf("Null=%b\tInvalid=%b\tcanUpdate=%b\tchunknull=%b%n",tet==null,tet.isInvalid(),tet.canUpdate(),(world.getChunkFromChunkCoords(x>>4, z>>4))==null);
		world.setBlockTileEntity(x, y, z, tet);
		tet.checkValidity();
    }
	
	@Override public void breakBlock(World world, int x, int y, int z, int useless, int variables) {
		if(world.getBlockTileEntity(x, y, z)!=null) 
			((TileEntityTransmog)world.getBlockTileEntity(x, y, z)).unvalid();
		super.breakBlock(world, x, y, z, useless, variables);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityTransmog();
	}

}
