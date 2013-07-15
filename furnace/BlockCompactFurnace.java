package mods.CompactStuff.furnace;

import java.util.HashMap;
import java.util.Random;

import mods.CompactStuff.CompactStuff;
import mods.CompactStuff.client.CSIcons;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;


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
	private static boolean keepInv;
	private Random furnaceRand = new Random();
	private static HashMap<Integer,Icon> icons = new HashMap<Integer,Icon>();
	
	public BlockCompactFurnace(int id, int blockIndex) {
		super(id, false);
		setHardness(3.5f);
		setStepSound(Block.soundStoneFootstep);
		setUnlocalizedName("compactfurnace");
		setCreativeTab(CompactStuff.compactTab);
	}
	
	@Override public int idPicked(World world, int x, int y, int z) {
		return CompactStuff.furnace.blockID;
	}
	@Override public int idDropped(int a, Random b, int c) {
		return CompactStuff.furnace.blockID;
	}
	
	@Override public int damageDropped(int meta) {
		if(meta<8) return 2;
		return 10;
	}
	
	@Override public void registerIcons(IconRegister ir) {
		icons.put(16*0+0, ir.registerIcon(CSIcons.PREFIX+CSIcons.COMFURNACE_TOP));
		icons.put(16*0+1, ir.registerIcon(CSIcons.PREFIX+CSIcons.COMFURNACE_SIDE));
		icons.put(16*0+2, ir.registerIcon(CSIcons.PREFIX+CSIcons.COMFURNACE_FRONT_INACTIVE));
		icons.put(16*0+3, ir.registerIcon(CSIcons.PREFIX+CSIcons.COMFURNACE_FRONT_ACTIVE));
		
		icons.put(16*1+0, ir.registerIcon(CSIcons.PREFIX+CSIcons.CARBFURNACE_TOP));
		icons.put(16*1+1, ir.registerIcon(CSIcons.PREFIX+CSIcons.CARBFURNACE_SIDE));
		icons.put(16*1+2, ir.registerIcon(CSIcons.PREFIX+CSIcons.CARBFURNACE_FRONT_INACTIVE));
		icons.put(16*1+3, ir.registerIcon(CSIcons.PREFIX+CSIcons.CARBFURNACE_FRONT_ACTIVE));
	}
	
	@Override public Icon getBlockTexture(IBlockAccess ba,int x,int y,int z, int s) {
		int blockIndex = 16*this.getFirstMetaBit(ba,x,y,z);
		if(s==0 || s==1) return icons.get(blockIndex);
		TileEntityCompactFurnace te = (TileEntityCompactFurnace)(ba.getBlockTileEntity(x, y, z));
		int front = getFront(ba,x,y,z);
		boolean isActive = isActive(ba,x,y,z);
		return icons.get(blockIndex+(s==front? (isActive ? 3 : 2) : 1));
	}
	
	@Override public Icon getIcon(int side, int meta) {
		int blockIndex = 16*Integer.parseInt(""+getMetaStr(meta)[0]);
		return icons.get((side==1||side==0)?blockIndex:(side==3?blockIndex+2:blockIndex+1));
	}
	
	public static void updateFurnace(boolean isActive, World world, int x, int y, int z) {
		setIsActive(world,x,y,z,isActive);
		int meta = world.getBlockMetadata(x, y, z);
		int id = world.getBlockId(x,y,z);
        TileEntity te = world.getBlockTileEntity(x, y, z);
        
        keepInv = true;
        world.setBlock(x,y,z,id,meta, 0x02 | 0x01);
        keepInv = false;
        
        if (te != null) {
            te.validate();
            world.setBlockTileEntity(x, y, z, te);
        }
    }
	
	@Override public TileEntity createTileEntity(World world, int meta) {
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
        if(keepInv) return;
        TileEntityCompactFurnace te = (TileEntityCompactFurnace)par1World.getBlockTileEntity(x, y, z);
	
        if (te != null) {
            for (int i = 0; i < te.getSizeInventory(); i++) {
                ItemStack stack = te.getStackInSlot(i);

                if (stack != null) {
                    float dx = this.furnaceRand.nextFloat() * 0.8F + 0.1F;
                    float dy = this.furnaceRand.nextFloat() * 0.8F + 0.1F;
                    float dz = this.furnaceRand.nextFloat() * 0.8F + 0.1F;

                    while (stack.stackSize > 0) {
                        int randAmt = this.furnaceRand.nextInt(21) + 10;

                        if (randAmt > stack.stackSize)
                        {
                            randAmt = stack.stackSize;
                        }

                        stack.stackSize -= randAmt;
                        EntityItem drop = new EntityItem(par1World, (double)((float)x + dx),
                        		(double)((float)y + dy), (double)((float)z + dz),
                        		new ItemStack(stack.itemID, randAmt, stack.getItemDamage()));

                        if (stack.hasTagCompound())
                        {
                        	drop.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
                        }

                        float var15 = 0.05F;
                        drop.motionX = (double)((float)this.furnaceRand.nextGaussian() * var15);
                        drop.motionY = (double)((float)this.furnaceRand.nextGaussian() * var15 + 0.2F);
                        drop.motionZ = (double)((float)this.furnaceRand.nextGaussian() * var15);
                        par1World.spawnEntityInWorld(drop);
                    }
                }
            }
        }
        par1World.removeBlockTileEntity(x, y, z);
        par1World.setBlockToAir(x, y, z);
    }
	
	private static char[] getMetaStr(int meta) {
		return String.format("%4s",Integer.toBinaryString(meta)).replace(' ','0').toCharArray();
	}
	public static int getFirstMetaBit(IBlockAccess world,int x,int y,int z) {
		return getMetaStr(world.getBlockMetadata(x,y,z))[0]-'0';
	}
	public static void setFront(World world,int x,int y,int z,int dir) {
		char[] metaStr = getMetaStr(world.getBlockMetadata(x,y,z));
		metaStr[3]=(dir%2==1)?'1':'0';
		metaStr[2]=(dir>1)?'1':'0';
		world.setBlockMetadataWithNotify(x,y,z,Integer.parseInt(new String(metaStr), 2),3);
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
		world.setBlockMetadataWithNotify(x,y,z,Integer.parseInt(new String(metaStr), 2),3);
	}
	public static boolean isActive(IBlockAccess world, int x, int y, int z) {
		char[] metaStr = getMetaStr(world.getBlockMetadata(x,y,z));
		return metaStr[1]=='1';
	}
	
	
	@Override public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving placer, ItemStack stack) {
		if(placer==null) {
			setDefaultDirection(world,x,y,z);
			setIsActive(world, x, y, z, false);
			return;
		}
        world.setBlockMetadataWithNotify(x, y, z, placer.getHeldItem().getItemDamage(), 0x04 | 0x02 | 0x01);
        int dir = MathHelper.floor_double((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        setFront(world,x,y,z,dir);
        setIsActive(world, x, y, z, false);
        world.setBlockTileEntity(x,y,z,this.createTileEntity(world,world.getBlockMetadata(x,y,z)));
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
