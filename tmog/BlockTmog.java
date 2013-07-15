package mods.CompactStuff.tmog;

import java.util.HashMap;

import mods.CompactStuff.CompactStuff;
import mods.CompactStuff.Metas;
import mods.CompactStuff.client.CSIcons;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

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
	public static HashMap<String,Icon> frameicons = new HashMap<String, Icon>();
	public BlockTmog(int id) {
		super(id, Material.iron);
		setHardness(4f);
		setStepSound(Block.soundMetalFootstep);
		setUnlocalizedName("transmogrifier");
		setCreativeTab(CompactStuff.compactTab);
	}
	
	@Override public void registerIcons(IconRegister ir) {
		icons.put(0, ir.registerIcon(CSIcons.PREFIX+"tmogcore_off"));
		icons.put(1, ir.registerIcon(CSIcons.PREFIX+"tmogshield_off"));
		icons.put(2, ir.registerIcon(CSIcons.PREFIX+"tmogframe_off"));
		icons.put(4, ir.registerIcon(CSIcons.PREFIX+"tmogcore_on"));
		icons.put(5, ir.registerIcon(CSIcons.PREFIX+"tmogshield_on"));
		
		for(String s : new String[] {"0","1l","1r","2","3","4t","4b","5"})
			frameicons.put(s, ir.registerIcon(CSIcons.PREFIX+"tmogframe_on"+s));
		frameicons.put("!", ir.registerIcon(CSIcons.PREFIX+"tmogframe_on_internal"));
	}
	
	@Override public int damageDropped(int meta) {
		return meta & 3; //strips everything but the last two bits
	}
	
	public static boolean isValid(IBlockAccess w, int x, int y, int z) {
		return (w.getBlockMetadata(x, y, z)&4)==4; //x0xx ==0, x1xx == 4
	}
	public static void setValid(World w, int x, int y, int z, boolean i) {
		int noo = w.getBlockMetadata(x, y, z); //pronounced like "new", a reserved word.
		if(i) noo |= 4; //x0xx | 0100 = x1xx; x1xx | 0100 = x1xx
		else noo &= 11; //x0xx & 1011 = x0xx; x1xx & 1011 = x0xx
		w.setBlockMetadataWithNotify(x, y, z, noo, 3);
	}
	
	@Override public Icon getBlockTexture(IBlockAccess ba,int x,int y,int z, int s) {
		int meta = ba.getBlockMetadata(x,y,z);
		if(meta!=6) //6 = 0110: valid frame
			return icons.get(ba.getBlockMetadata(x, y, z));
		int b=0;
		boolean[] a = new boolean[8];
		switch(s) {
			case 0:
			case 1: a = new boolean[] { a(ba,x,y,z-1),a(ba,x+1,y,z-1),a(ba,x+1,y,z),a(ba,x+1,y,z+1),
									a(ba,x,y,z+1),a(ba,x-1,y,z+1),a(ba,x-1,y,z),a(ba,x-1,y,z-1),}; break;
			case 3: a = new boolean[] { a(ba,x,y+1,z),a(ba,x+1,y+1,z),a(ba,x+1,y,z),a(ba,x+1,y-1,z),
									a(ba,x,y-1,z),a(ba,x-1,y-1,z),a(ba,x-1,y,z),a(ba,x-1,y+1,z),}; break;
			case 2: a = new boolean[] { a(ba,x,y+1,z),a(ba,x-1,y+1,z),a(ba,x-1,y,z),a(ba,x-1,y-1,z),
									a(ba,x,y-1,z),a(ba,x+1,y-1,z),a(ba,x+1,y,z),a(ba,x+1,y+1,z),}; break;
			case 4: a = new boolean[] { a(ba,x,y+1,z),a(ba,x,y+1,z+1),a(ba,x,y,z+1),a(ba,x,y-1,z+1),
									a(ba,x,y-1,z),a(ba,x,y-1,z-1),a(ba,x,y,z-1),a(ba,x,y+1,z-1),}; break;
			case 5: a = new boolean[] { a(ba,x,y+1,z),a(ba,x,y+1,z-1),a(ba,x,y,z-1),a(ba,x,y-1,z-1),
									a(ba,x,y-1,z),a(ba,x,y-1,z+1),a(ba,x,y,z+1),a(ba,x,y+1,z+1),}; break;
		}
		if(a[0]) b+=1;
		if(a[2]) b+=2;
		if(a[4]) b+=3;
		if(a[6]) b+=5;
		b-=3; //possible values are now 0-5
		String path = ""+b;
		switch(b) {
			case 1: if(a[1]) path+="l";
					else path+="r"; break;
			case 4: if(!a[1]) path+="t";
					else path+="b"; break;
		} boolean i = false;
		switch(s) {
			case 0: i=b(ba,x,y-1,z); break;
			case 1: i=b(ba,x,y+1,z); break;
			case 2: i=b(ba,x,y,z-1); break;
			case 3: i=b(ba,x,y,z+1); break;
			case 4: i=b(ba,x-1,y,z); break;
			case 5: i=b(ba,x+1,y,z); break;
		}
		return frameicons.get(i?"!":path);
	} private boolean a(IBlockAccess ba, int x, int y, int z) {
		return ba.getBlockId(x,y,z)==blockID && ba.getBlockMetadata(x, y, z)==6; //6==0110, active frame
	} private boolean b(IBlockAccess ba, int x, int y, int z) {
		return ba.getBlockId(x,y,z)==blockID && ba.getBlockMetadata(x, y, z)==5; //5==0101, active shielding
	}
	
	@Override public Icon getIcon(int side, int meta) {
		return icons.get(meta);
	}
	
	@Override public boolean canPlaceTorchOnTop(World w, int x, int y, int z) { return true; }
	@Override public boolean isOpaqueCube() { return false; }
	@Override public boolean renderAsNormalBlock() { return false; }
	@Override public boolean isBlockSolidOnSide(World w, int x, int y, int z, ForgeDirection side) { return true; }
		
	@Override public boolean onBlockActivated(World world, int x, int y, int z, 
	EntityPlayer player, int a, float b, float c, float d) {
		if(player.isSneaking() || !isValid(world,x,y,z)) return false;
		if(world.isRemote) return true;
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
		//world.setBlockMetadataWithNotify(x, y, z, placer.getHeldItem().getItemDamage(), 0x02 | 0x01);
		TileEntityTransmog tet = (TileEntityTransmog)createTileEntity(world, world.getBlockMetadata(x, y, z));
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
	
	@Override public float getBlockHardness(World world, int x, int y, int z) {
        return getResFromMeta(world.getBlockMetadata(x, y, z) & 3);
    }
	@Override public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double eX, double eY, double eZ) {
        return getBlockHardness(world,x,y,z);
    }
	private float getResFromMeta(int type) {
        switch(type) {
        case 2 : return 0.7f;
        case 1 : return 50f;
        } return 4f;
	}

}
