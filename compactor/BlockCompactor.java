package mods.CompactStuff.compactor;

import mods.CompactStuff.CompactStuff;
import mods.CompactStuff.client.CSIcons;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockCompactor extends BlockContainer {
	private static Icon[] icons = new Icon[4];
	
	public BlockCompactor(int id) {
		super(id, Material.rock);
		this.setHardness(4.0f);
		this.setStepSound(soundMetalFootstep);
		this.setUnlocalizedName("cs_compactor");
		this.setCreativeTab(CompactStuff.compactTab);
	}
	
	@Override public void registerIcons(IconRegister ir) {
		icons[0] = ir.registerIcon(CSIcons.PREFIX + CSIcons.COMPACTOR_BOTTOM);
		icons[1] = ir.registerIcon(CSIcons.PREFIX + CSIcons.COMPACTOR_TOP);
		icons[2] = ir.registerIcon(CSIcons.PREFIX + CSIcons.COMPACTOR_SIDE1);
		icons[3] = ir.registerIcon(CSIcons.PREFIX + CSIcons.COMPACTOR_SIDE2);
	}
	
	@Override public Icon getIcon(int side, int meta) {
		return icons[(side==0||side==1)?side:(side<4?2:3)];
	}
	
	@Override public TileEntity createTileEntity(World world, int meta) {
        return new TileEntityCompactor();
    }
	
	@Override public boolean onBlockActivated(World world, int x, int y, int z,
	EntityPlayer player, int a, float b, float c, float d) {
		if(player.isSneaking() || world.isRemote) return true;
		if(world.getBlockTileEntity(x,y,z)!=null) { 
			player.openGui(CompactStuff.instance, 3, world, x, y, z);
		}
		return true;
	}
	@Override
	public void breakBlock(World world, int x, int y, int z, int useless, int variables) {
		TileEntityCompactor te = (TileEntityCompactor)world.getBlockTileEntity(x, y, z);
		
        if (te != null) {
            for (int i = 0; i < te.getSizeInventory(); i++) {
                ItemStack stack = te.getStackInSlot(i);

                if (stack != null) {
                    float dx = CompactStuff.rand.nextFloat() * 0.8F + 0.1F;
                    float dy = CompactStuff.rand.nextFloat() * 0.8F + 0.1F;
                    float dz = CompactStuff.rand.nextFloat() * 0.8F + 0.1F;

                    while (stack.stackSize > 0) {
                        int randAmt = CompactStuff.rand.nextInt(21) + 10;

                        if (randAmt > stack.stackSize)
                        {
                            randAmt = stack.stackSize;
                        }

                        stack.stackSize -= randAmt;
                        EntityItem drop = new EntityItem(world, (double)((float)x + dx),
                        		(double)((float)y + dy), (double)((float)z + dz),
                        		new ItemStack(stack.itemID, randAmt, stack.getItemDamage()));

                        if (stack.hasTagCompound()) {
                        	drop.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
                        }

                        float var15 = 0.05F;
                        drop.motionX = (double)((float)CompactStuff.rand.nextGaussian() * var15);
                        drop.motionY = (double)((float)CompactStuff.rand.nextGaussian() * var15 + 0.2F);
                        drop.motionZ = (double)((float)CompactStuff.rand.nextGaussian() * var15);
                        world.spawnEntityInWorld(drop);
                    }
                }
            }
        }
        world.removeBlockTileEntity(x, y, z);
        world.setBlockToAir(x, y, z);
    }
	
	@Override public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving placer, ItemStack stack) {
		if(placer==null) return;
		TileEntity te = this.createTileEntity(world, world.getBlockMetadata(x,y,z));
		world.setBlockTileEntity(x, y, z, te);
    }

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityCompactor();
	}
}
