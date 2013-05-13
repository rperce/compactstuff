package mods.CompactStuff;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class BlockComGlass extends Block {
	private boolean fancy;
	public HashMap<Integer, Icon> icons = new HashMap<Integer, Icon>();
	public BlockComGlass(int id, boolean fancy) {
		super(id, Material.glass);
		setCreativeTab(CompactStuff.compactTab);
		setStepSound(Block.soundGlassFootstep);
		setHardness(0.7f);
		setLightOpacity(0);
		LanguageRegistry.addName(this, "Compressed Glass");
		setUnlocalizedName("comglass");
		setResistance(21f);
		this.fancy = fancy;
	}
	@Override public void registerIcons(IconRegister ir) {
		for(int i=0; i<15; i++) icons.put(i, ir.registerIcon(CSIcons.PREFIX+"comglass"+i));
		for(int i=15; i<256; i+=16) icons.put(i, ir.registerIcon(CSIcons.PREFIX+"comglass"+i));
		for(int i : new int[] {19, 22, 23, 25, 27, 28, 29, 30, 31, 39, 43, 45, 46, 47, 55, 59, 61, 62}) {
			icons.put(i,ir.registerIcon(CSIcons.PREFIX+"comglass"+i));			
		}
	}
	@Override public boolean canPlaceTorchOnTop(World w, int x, int y, int z) { return true; }
	@Override public boolean isOpaqueCube() { return false; }
	@Override public boolean renderAsNormalBlock() { return false; }
	@Override public boolean isBlockSolidOnSide(World w, int x, int y, int z, ForgeDirection side) { return true; }
	
	@Override public Icon getBlockTextureFromSideAndMetadata(int s, int m) {
		return icons.get(0);
	}
	@Override public Icon getBlockTexture(IBlockAccess ba, int x, int y, int z, int side) {
		if(!fancy) return icons.get(0);
		int b = 0, m=0;
		boolean[] a = new boolean[8];
		switch(side) {
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
		if(a[4]) b+=4;
		if(a[6]) b+=8;
		switch(b) {
			case 14:if(!a[5]) m+=2;
			case 6:	if(!a[3]) m+=1; break;
			case 7: if(!a[3]) m+=2;
			case 3:	if(!a[1]) m+=1; break;
			case 11:if(!a[1]) m+=2;
			case 9:	if(!a[7]) m+=1; break;
			case 13:if(!a[7]) m+=2;
			case 12:if(!a[5]) m+=1; break;
			case 15:for(int i=1; i<8; i+=2) if(!a[i]) m+=(int)Math.pow(2, i/2); break;
		}
		return icons.get(b+16*m);
	} private boolean a(IBlockAccess ba, int x, int y, int z) {
		return ba.getBlockId(x,y,z)==blockID;
	}
	@Override public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        int var6 = par1IBlockAccess.getBlockId(par2, par3, par4);
        return var6 == this.blockID ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
    }
}
