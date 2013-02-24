package compactstuff.tools;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;

import compactstuff.CompactStuff;
import compactstuff.ImageFiles;

public class Paxel extends ItemTool {
    public static Block[] blocksEffectiveAgainst = Block.blocksList;
    public static Block[] ores = {
    	Block.oreCoal, Block.oreDiamond, Block.oreEmerald, Block.oreGold, Block.oreIron,
    	Block.oreLapis, Block.oreRedstone, Block.oreRedstoneGlowing
    };
    public static Block[] glow = {Block.glowStone};
    public static Block[] tree = {Block.wood, Block.leaves};
    public static Block[] gravel = {Block.gravel};
    public static Block[][] allTheThingsToBreak = {ores, glow, tree, gravel};
    public static Random rand = new Random();  
    
    public Paxel(int id, EnumToolMaterial toolMaterial) {
		super(id, 3, toolMaterial, blocksEffectiveAgainst);
		this.setCreativeTab(CompactStuff.compactTab);
		setMaxStackSize(1);
    }
    
    @Override public String getTextureFile() { return ImageFiles.ITEMS.path; }
    
    @Override public boolean canHarvestBlock(Block block) { return true; }
    
    @Override public boolean onBlockDestroyed(ItemStack thisStack, World world, int blockSlot, int x, int y, int z, EntityLiving holder) {
	    if(!(holder instanceof EntityPlayer) || world.getBlockId(x, y, z)==0) return true;
	    EntityPlayer player = (EntityPlayer)holder;
    	if(player.isSneaking()) {
	    	if(world.getBlockId(x, y, z)==Block.sand.blockID) {
	    		for(int r=x-4; r<=x+4; r++) {
	    			for(int c=z-4; c<=z+4; c++) {
	    				if(world.getBlockId(r,y,c)==Block.sand.blockID) {
	    					EntityItem drop = new EntityItem(world, player.posX, player.posY, player.posZ, new ItemStack(
	    		    				Block.sand.idDropped(world.getBlockMetadata(r,y,c), rand, 0),
	    		    				Block.sand.quantityDropped(rand), 
	    		    				Block.sand.damageDropped(world.getBlockMetadata(r, y, c))));
	    		    		world.spawnEntityInWorld(drop);
	    		    		world.setBlockAndMetadataWithNotify(r, y, c, 0, 0);
	    		    		thisStack.damageItem(1, player);
	    				}
	    			}
	    		}
	    		return true;
	    	}
    		if(player instanceof EntityPlayer) {
    			try { Block.blocksList[world.getBlockId(x, y, z)].getBlockName().toLowerCase(); }
    			catch(NullPointerException e) {
    				return false;
    			}
	    		if(Block.blocksList[world.getBlockId(x, y, z)].getBlockName().toLowerCase().contains("ore")) {
	    			int dmg = recursivelyBreakOres(world, x, y, z, (EntityPlayer)player, 0);
	    			if(dmg>0) {
	    				thisStack.damageItem(dmg,player);
	    				return true;
	    			}
	    		} else {
	    			for(Block[] breakThese : allTheThingsToBreak) {
	    				int dmg = recursivelyBreakThings(breakThese, world, x, y, z, (EntityPlayer)player, x,y,z);
	    				if(dmg>0) {
	    					thisStack.damageItem(dmg,player);
	    					return true;
	    				}
	    			}
	    		}
	    	}
    	}
    	return super.onBlockDestroyed(thisStack, world, blockSlot, x, y, z, player);
    } private int recursivelyBreakThings(Block[] breakThese, World world, int x, int y, int z, EntityPlayer player,int ox,int oy,int oz) {
    	if(Math.abs(ox-x)>32 || Math.abs(oy-y)>32 || Math.abs(oz-z)>32) return 0;
    	int thisBlockID = world.getBlockId(x, y, z);
    	if(thisBlockID==0 || thisBlockID==1) return 0;
    	boolean edgeCase = true;
    	for(Block b : breakThese) {
    		if(thisBlockID==b.blockID) {
    			edgeCase = false; 
    			break;
    		}
    	} if(edgeCase) return 0;
    	
    	if(thisBlockID==Block.leaves.blockID) {
    		EntityItem apple = new EntityItem(world, player.posX, player.posY, player.posZ,
    				new ItemStack(Item.appleRed));
    		EntityItem sapling = new EntityItem(world, player.posX, player.posY, player.posZ,
    				new ItemStack(Block.sapling, 1,
    						Block.blocksList[thisBlockID].damageDropped(world.getBlockMetadata(x, y, z))));

    		if(Math.random()<=0.005d) world.spawnEntityInWorld(apple);
    		if(Math.random()<= 0.05d) world.spawnEntityInWorld(sapling);
    	} else {
    		EntityItem drop = new EntityItem(world, player.posX, player.posY, player.posZ, new ItemStack(
    				Block.blocksList[thisBlockID].idDropped(world.getBlockMetadata(x,y,z), rand, 0),
    				Block.blocksList[thisBlockID].quantityDropped(rand), 
    				Block.blocksList[thisBlockID].damageDropped(world.getBlockMetadata(x, y, z))));
    		world.spawnEntityInWorld(drop);
    	}
    	
    	world.setBlockAndMetadataWithNotify(x, y, z, 0, 0);
    	return (thisBlockID==Block.leaves.blockID ? 0 : 1) +
	    	recursivelyBreakThings(breakThese, world, x-1, y, z, player, ox,oy,oz)+
	    	recursivelyBreakThings(breakThese, world, x+1, y, z, player, ox,oy,oz)+
	    	recursivelyBreakThings(breakThese, world, x, y-1, z, player, ox,oy,oz)+
	    	recursivelyBreakThings(breakThese, world, x, y+1, z, player, ox,oy,oz)+
	    	recursivelyBreakThings(breakThese, world, x, y, z-1, player, ox,oy,oz)+
	    	recursivelyBreakThings(breakThese, world, x, y, z+1, player, ox,oy,oz);
    } private int recursivelyBreakOres(World world, int x, int y, int z, EntityPlayer player, int stop) {
    	if(stop>128) return 0;
    	int thisBlockID = world.getBlockId(x, y, z);
    	if(thisBlockID==0 || thisBlockID==1) return 0;
    	if(!Block.blocksList[thisBlockID].getBlockName().toLowerCase().contains("ore")) return 0;
    	EntityItem drop = new EntityItem(world, player.posX, player.posY, player.posZ, new ItemStack(
				Block.blocksList[thisBlockID].idDropped(world.getBlockMetadata(x,y,z), rand, 0),
				Block.blocksList[thisBlockID].quantityDropped(rand), 
				Block.blocksList[thisBlockID].damageDropped(world.getBlockMetadata(x, y, z))));
		world.spawnEntityInWorld(drop);
		world.setBlockAndMetadataWithNotify(x, y, z, 0, 0);
    	return (thisBlockID==Block.leaves.blockID ? 0 : 1) +
	    	recursivelyBreakOres(world, x-1, y, z, player, stop+1) +
	    	recursivelyBreakOres(world, x+1, y, z, player, stop+1) +
	    	recursivelyBreakOres(world, x, y-1, z, player, stop+1) +
	    	recursivelyBreakOres(world, x, y+1, z, player, stop+1) +
	    	recursivelyBreakOres(world, x, y, z-1, player, stop+1) +
	    	recursivelyBreakOres(world, x, y, z+1, player, stop+1);
    }
}
