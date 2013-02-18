package compactstuff.furnace;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import compactstuff.ImageFiles;
import compactstuff.CompactStuff;


/*
 * 4-bit metadata format:
 * Meta		0000
 * Bit no. (0123)
 * 
 * Bit 0: 0 -> Compression Furnace		1 -> Carbon Furnace	
 * Bit 1: 0 -> Inactive					1 -> Active
 * Bit 2/3: Front side:
 * 	00 -> 2
 * 	01 -> 5
 *  10 -> 3
 *  11 -> 4
 *  
 *  Default Compression Furnace value: 0010 = 2
 *  	(possible values 0000 0001 0010 0011 0100 0101 0110 0111 i.e. 0 to 7)
 *  Default Carbon Furnace value:	   1010 = 10
 *  	(possible values 1000 1001 1010 1011 1100 1101 1110 1111 i.e. 8 to 15)
 */
public class BlockCompactFurnace extends BlockFurnace {
	private int blockImageIndex;
	private static boolean keepInv;
	private Random furnaceRand = new Random();
	
	public BlockCompactFurnace(int id, int blockIndex) {
		super(id, false);
		this.blockImageIndex = blockIndex;
	}
	
	@Override public int idDropped(int a, Random b, int c) {
		return CompactStuff.furnace.blockID;
	}
	
	@Override public int damageDropped(int meta) {
		if(meta<8) return 2;
		return 10;
	}
	
	@Override public int getBlockTexture(IBlockAccess ba,int x,int y,int z, int s) {
		int blockIndex = this.blockImageIndex + 16*this.getFirstMetaBit(ba,x,y,z);
		if(s==0 || s==1) return blockIndex;
		TileEntityCompactFurnace te = (TileEntityCompactFurnace)(ba.getBlockTileEntity(x, y, z));
		boolean hasOutput = te.hasOutput();
		int front = getFront(ba,x,y,z);
		boolean isActive = isActive(ba,x,y,z);
        return (s!=front)?blockIndex+1:(isActive?blockIndex+3:blockIndex+2);
	}
	
	@Override public int getBlockTextureFromSide(int side) {
        return (side==1||side==0)?this.blockImageIndex:(side==3?this.blockImageIndex+2:this.blockImageIndex+1);
    }
	@Override public int getBlockTextureFromSideAndMetadata(int side, int meta) {
		int blockIndex = blockImageIndex+16*Integer.parseInt(""+getMetaStr(meta)[0]);
		return (side==1||side==0)?blockIndex:(side==3?blockIndex+2:blockIndex+1);
	}
	public static void updateFurnaceBlockState(boolean isActive, World world, int x, int y, int z) {
		if(isActive) setIsActive(world,x,y,z,true);
		else setIsActive(world,x,y,z,false);
		
		int meta = world.getBlockMetadata(x, y, z);
        TileEntity te = world.getBlockTileEntity(x, y, z);
        
        keepInv = true;
        world.setBlockWithNotify(x, y, z, CompactStuff.furnace.blockID);
        world.setBlockMetadata(x,y,z,meta);
        keepInv = false;

        
        if (te != null)
        {
            te.validate();
            world.setBlockTileEntity(x, y, z, te);
        }
    }
	
	@Override public TileEntity createNewTileEntity(World par1World,int meta) {
        if(meta<8) return new TileEntityCobbleFurnace();
        return new TileEntityCarbonFurnace();
    }
	
	@Override public boolean onBlockActivated(World world, int x, int y, int z,
	EntityPlayer player, int a, float b, float c, float d) {
		if(player.isSneaking()) return true;
        if (world.isRemote) return true;
        
    	TileEntityCompactFurnace tileEntity = (TileEntityCompactFurnace)world.getBlockTileEntity(x, y, z);
		
		if (tileEntity != null) {
			player.openGui(CompactStuff.instance, 0, world, x, y, z);
		}
			

		return true;
    }
	
	@Override
	public String getTextureFile () {
		return ImageFiles.BLOCKS.path;
	}
	
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if(rand==null) rand = new Random();
		if (isActive(world,x,y,z)) {
			int front = getFront(world,x,y,z);
            float dx = (float)x + 0.5F;
            float dy = (float)y + 0.0F + rand.nextFloat() * 6.0F / 16.0F;
            float dz = (float)z + 0.5F;
            float c = 0.52F;
            float r = rand.nextFloat() * 0.6F - 0.3F;

            if (front == 4) {
                world.spawnParticle("smoke", (double)(dx-c), (double)dy, (double)(dz+r), 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", (double)(dx-c), (double)dy, (double)(dz+r), 0.0D, 0.0D, 0.0D);
            } else if (front == 5) {
                world.spawnParticle("smoke", (double)(dx+c), (double)dy, (double)(dz+r), 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", (double)(dx+c), (double)dy, (double)(dz+r), 0.0D, 0.0D, 0.0D);
            } else if (front == 2) {
                world.spawnParticle("smoke", (double)(dx+r), (double)dy, (double)(dz-c), 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", (double)(dx+r), (double)dy, (double)(dz-c), 0.0D, 0.0D, 0.0D);
            } else if (front == 3) {
                world.spawnParticle("smoke", (double)(dx+r), (double)dy, (double)(dz+c), 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", (double)(dx+r), (double)dy, (double)(dz+c), 0.0D, 0.0D, 0.0D);
            }
        }
    }
	
	@Override
	public void breakBlock(World par1World, int x, int y, int z, int useless, int variables) {
        if (keepInv) return;
        if(!keepInv) {
	        TileEntityCompactFurnace var7 = (TileEntityCompactFurnace)par1World.getBlockTileEntity(x, y, z);
	
	        if (var7 != null) {
	            for (int var8 = 0; var8 < var7.getSizeInventory(); ++var8) {
	                ItemStack var9 = var7.getStackInSlot(var8);
	
	                if (var9 != null) {
	                    float var10 = this.furnaceRand.nextFloat() * 0.8F + 0.1F;
	                    float var11 = this.furnaceRand.nextFloat() * 0.8F + 0.1F;
	                    float var12 = this.furnaceRand.nextFloat() * 0.8F + 0.1F;
	
	                    while (var9.stackSize > 0) {
	                        int var13 = this.furnaceRand.nextInt(21) + 10;
	
	                        if (var13 > var9.stackSize)
	                        {
	                            var13 = var9.stackSize;
	                        }
	
	                        var9.stackSize -= var13;
	                        EntityItem var14 = new EntityItem(par1World, (double)((float)x + var10),
	                        		(double)((float)y + var11), (double)((float)z + var12),
	                        		new ItemStack(var9.itemID, var13, var9.getItemDamage()));
	
	                        if (var9.hasTagCompound())
	                        {
	                        	var14.func_92014_d().setTagCompound((NBTTagCompound)var9.getTagCompound().copy());
	                        }
	
	                        float var15 = 0.05F;
	                        var14.motionX = (double)((float)this.furnaceRand.nextGaussian() * var15);
	                        var14.motionY = (double)((float)this.furnaceRand.nextGaussian() * var15 + 0.2F);
	                        var14.motionZ = (double)((float)this.furnaceRand.nextGaussian() * var15);
	                        par1World.spawnEntityInWorld(var14);
	                    }
	                }
	            }
	        }
        }
        par1World.removeBlockTileEntity(x, y, z);
        par1World.setBlockMetadata(x, y, z, 0);
    }
	
	private static char[] getMetaStr(int meta) {
		return String.format("%4s",Integer.toBinaryString(meta)).replace(' ','0').toCharArray();
	}
	public static int getFirstMetaBit(IBlockAccess world,int x,int y,int z) {
		char[] metaStr = getMetaStr(world.getBlockMetadata(x,y,z));
		return Integer.parseInt(""+metaStr[0]);
	}
	public static void setFront(World world,int x,int y,int z,int dir) {
		char[] metaStr = getMetaStr(world.getBlockMetadata(x,y,z));
		metaStr[3]=(dir%2==1)?'1':'0';
		metaStr[2]=(dir>1)?'1':'0';
		world.setBlockMetadataWithNotify(x,y,z,Integer.parseInt(new String(metaStr), 2));
	}
	public static int getFront(IBlockAccess world,int x,int y,int z) {
		char[] metaStr = getMetaStr(world.getBlockMetadata(x,y,z));
		String back = "" + metaStr[2] + metaStr[3];
		switch(Integer.parseInt(back, 2)) {
			case 0: return 2;
			case 1: return 5;
			case 2: return 3;
			case 3: return 4;
		}
		return 3;
	}
	public static void setIsActive(World world,int x,int y,int z,boolean act) {
		char[] metaStr = getMetaStr(world.getBlockMetadata(x,y,z));
		if(act) metaStr[1]='1'; else metaStr[1]='0';
		world.setBlockMetadataWithNotify(x,y,z,Integer.parseInt(new String(metaStr), 2));
	}
	public static boolean isActive(IBlockAccess world, int x, int y, int z) {
		char[] metaStr = getMetaStr(world.getBlockMetadata(x,y,z));
		return metaStr[1]=='1';
	}
	
	
	@Override public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving placer) {
		if(placer==null) {
			setDefaultDirection(world,x,y,z);
			setIsActive(world, x, y, z, false);
			return;
		}
        world.setBlockMetadata(x, y, z, placer.getHeldItem().getItemDamage());
        int dir = MathHelper.floor_double((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        setFront(world,x,y,z,dir);
        setIsActive(world, x, y, z, false);
        world.setBlockTileEntity(x,y,z,this.createNewTileEntity(world,world.getBlockMetadata(x,y,z)));
    }
	
	private void setDefaultDirection(World world, int x, int y, int z) {
        if (!world.isRemote)
        {
            int var5 = world.getBlockId(x, y, z - 1);
            int var6 = world.getBlockId(x, y, z + 1);
            int var7 = world.getBlockId(x - 1, y, z);
            int var8 = world.getBlockId(x + 1, y, z);
            byte dir = 2;

            if (Block.opaqueCubeLookup[var5] && !Block.opaqueCubeLookup[var6])
            {
                dir = 2;
            }

            if (Block.opaqueCubeLookup[var6] && !Block.opaqueCubeLookup[var5])
            {
                dir = 0;
            }

            if (Block.opaqueCubeLookup[var7] && !Block.opaqueCubeLookup[var8])
            {
                dir = 1;
            }

            if (Block.opaqueCubeLookup[var8] && !Block.opaqueCubeLookup[var7])
            {
                dir = 3;
            }

            setFront(world,x,y,z,dir);
        }
    }
}
