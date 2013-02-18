package compactstuff;

import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockCompressed extends Block {
	public static HashMap<Integer,String> names = new HashMap<Integer,String>();
	private final static int tickRate = 5;
	static {
		names.put(Metas.COMCOBBLE, 	"Compressed Cobblestone");
		names.put(Metas.COMCOAL, 	"Coal Block");
		names.put(Metas.COMRACK, 	"Compressed Netherrack");
		names.put(Metas.COMREDSTONE,"Redstone Block");
		names.put(Metas.COMDIAMOND, "Compressed Diamond Block");
		names.put(Metas.COMIRON,	"Compressed Iron Block");
		names.put(Metas.DIORITE, 	"Diorite");
		names.put(Metas.STEELBLOCK, "CS Steel Block");
		names.put(Metas.COMSTEEL,	"Compressed CS Steel Block");
		names.put(Metas.COMDIRT, 	"Compressed Dirt");
		names.put(Metas.COMSAND,	"Compressed Sand");
		names.put(Metas.COMGRAVEL,	"Compressed Gravel");
	}
	public BlockCompressed(int id) {
		super(id,Material.rock);
		setStepSound(Block.soundStoneFootstep);
		setCreativeTab(CompactStuff.compactTab);
	}
	public BlockCompressed(int id,Material m) {
		super(id,m);
	}
	@Override
	public String getTextureFile () {
		return ImageFiles.BLOCKS.path;
	}
	
	@Override public int damageDropped(int meta) { return meta; }
	
	@Override public int getBlockTextureFromSideAndMetadata(int side, int meta) {
		return meta;
    }
	@Override public float getBlockHardness(World world, int x, int y, int z) {
        return getResFromMeta(world.getBlockMetadata(x, y, z));
    }
	@Override public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double eX, double eY, double eZ) {
        return getBlockHardness(world,x,y,z);
    }
	private float getResFromMeta(int meta) {
        switch(meta) {
        	case Metas.COMCOBBLE: 	return 16f;
        	case Metas.DIORITE:		return 20f;
        	case Metas.COMCOAL:		return 5f;
        	case Metas.COMRACK:		return 3.2f;
        	case Metas.COMREDSTONE:	return 5f;
        	case Metas.COMDIAMOND:	return 16f;
        	case Metas.COMIRON:		return 16f;
        	case Metas.COMSTEEL:	return 16f;
        	default: return 4f;
        }
	}
		
	@Override
	public boolean isFireSource(World world, int x, int y, int z, int metadata, ForgeDirection side) {
        if (metadata==Metas.COMCOAL || metadata==Metas.COMRACK) return true;
        return super.isFireSource(world,x,y,z,metadata,side);
    }
	
	
	/*==============
     * FALLING
     * =============*/
	@Override public void onBlockAdded(World world, int x, int y, int z) {
        world.scheduleBlockUpdate(x, y, z, this.blockID, tickRate);
    }
   
    @Override public void onNeighborBlockChange(World world, int x, int y, int z, int something) {
        world.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate());
    }

    @Override public void updateTick(World world, int x, int y, int z, Random par5Random) {
        if(world.isRemote) return;
        int meta = world.getBlockMetadata(x,y,z);
        if(meta==Metas.COMSAND || meta==Metas.COMGRAVEL)
            tryToFall(world, x, y, z);  
    }

    private void tryToFall(World world, int x, int y, int z) {
        if(!canFallBelow(world, x, y - 1, z) || y < 0) return;
        byte d = 32;

        if(world.checkChunksExist(x - d, y - d, z - d, x + d, y + d, z + d)) {
            if(!world.isRemote) {
            	double dx = (double)(x+.5f),
            		dy = (double)(y+.5f),
            		dz = (double)(z+.5f);
                EntityFallingCompact var9 = new EntityFallingCompact(world, dx, dy, dz, blockID, world.getBlockMetadata(x, y, z));
                world.spawnEntityInWorld(var9);
            }
        } else {
            world.setBlockWithNotify(x, y, z, 0);

            while (canFallBelow(world, x, y - 1, z) && y > 0) y--;
            if (y > 0) world.setBlockWithNotify(x, y, z, this.blockID);
        }
    }

    public static boolean canFallBelow(World par0World, int par1, int par2, int par3) {
        int id = par0World.getBlockId(par1, par2, par3);

        if(id==0 || id == Block.fire.blockID)
        	return true;
        Material mat = Block.blocksList[id].blockMaterial;
        return mat == Material.water ? true : mat == Material.lava;
    }
}
