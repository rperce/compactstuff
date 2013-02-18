package compactstuff;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockComGlass extends Block {
	private boolean fancy;
	public BlockComGlass(int id, boolean fancy) {
		super(id, Material.glass);
		setCreativeTab(CompactStuff.compactTab);
		setStepSound(Block.soundGlassFootstep);
		setHardness(0.7f);
		setTextureFile(ImageFiles.GLASS.path);
		setLightOpacity(0);
		LanguageRegistry.addName(this, "Compressed Glass");
		setBlockName("comglass");
		setResistance(21f);
		this.fancy = fancy;
	}
	@Override public boolean canPlaceTorchOnTop(World w, int x, int y, int z) { return true; }
	@Override public boolean isOpaqueCube() { return false; }
	@Override public boolean renderAsNormalBlock() { return false; }
	@Override public boolean isBlockSolidOnSide(World w, int x, int y, int z, ForgeDirection side) { return true; }
	@Override public int getBlockTexture(IBlockAccess ba, int x, int y, int z, int side) {
		if(!fancy) return 0;
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
		return b+16*m;
	} private boolean a(IBlockAccess ba, int x, int y, int z) {
		return ba.getBlockId(x,y,z)==blockID;
	}
	@Override public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        int var6 = par1IBlockAccess.getBlockId(par2, par3, par4);
        return var6 == this.blockID ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
    }
}
