package mods.CompactStuff.tmog;

import java.util.HashSet;

import mods.CompactStuff.CompactStuff;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class TileEntityTransmog extends TileEntity {
	public static final int CORE=0, SHIELD=1, FRAME=2;
	private int type;
	private boolean valid;
	private TileEntityTransmog core;
	public TileEntityTransmog() {
		type = blockMetadata & 3; //last two bits
		valid = false;
		core = null;
	}
	
	public void clicked(EntityPlayer player) {
		if(valid) return;
		core.openGui(player);
	}

	public void checkValidity() {
		System.out.println("Blll");
		checkEdgeValidity(new HashSet<Integer>());
	} private boolean checkEdgeValidity(HashSet<Integer> been) {
		System.out.println("Holding steady...");
		if(been.contains(this.hashCode())) return false;
		System.out.println("Not been!");
		System.out.println("Type: "+(type==CORE?"CORE":type==FRAME?"FRAME":"SHIELD"));
		been.add(this.hashCode());
		if(type==CORE) {
			checkCoreValidity();
			been.clear();
			return true;
		}
		TileEntity[] tes;
		for(int d=-1; d<2; d+=2) {
			tes = new TileEntity[] {worldObj.getBlockTileEntity(xCoord+d, yCoord, zCoord),
					worldObj.getBlockTileEntity(xCoord, yCoord+d, zCoord),
					worldObj.getBlockTileEntity(xCoord, yCoord, zCoord+d) };
			for(TileEntity te : tes) {
				if(te==null || !(te instanceof TileEntityTransmog)) continue;
				if(((TileEntityTransmog)te).checkEdgeValidity(been)) return true;
			}
		}
		return false;
	} private void checkCoreValidity() {
		int x=xCoord, y=yCoord, z=zCoord;
		System.out.printf("Found core at %d, %d, %d%n",x,y,z);
		//check shielding;
		int[] dx = {-1, 0, 0, 0, 0, 1},
			  dy = { 0, 1, 0,-1, 0, 0},
			  dz = { 0, 0, 1, 0,-1, 0};
		boolean good = true;
		for(int i=0; i<dx.length; i++) {
			if(worldObj.getBlockId(x+dx[i], y+dy[i], z+dz[i])!=CompactStuff.transmog.blockID ||
			(worldObj.getBlockMetadata(x+dx[i],  y+dy[i],  z+dz[i])&3)!=SHIELD) {
				good = false;
				break;
			}
		}
		if(good) {
			//check frames;
			dx = new int[] {-1,-1,-1,-1,-1,-1,-1,-1,  0, 0, 0, 0, 0, 0, 0, 0,  1, 1, 1, 1, 1, 1, 1, 1};
			dy = new int[] { 1, 1, 1, 0, 0,-1,-1,-1,  1, 1, 1, 0, 0,-1,-1,-1,  1, 1, 1, 0, 0,-1,-1,-1};
			dz = new int[] {-1, 0, 1,-1, 1,-1, 0, 1, -1, 0, 1,-1, 1,-1, 0, 1, -1, 0, 1,-1, 1,-1, 0, 1};
			for(int i=0; i<dx.length; i++) {
				if(worldObj.getBlockId(x+dx[i], y+dy[i], z+dz[i])!=CompactStuff.transmog.blockID ||
				(worldObj.getBlockMetadata(x+dx[i],  y+dy[i],  z+dz[i])&3)!=FRAME) {
					good = false;
					break;
				}
			}
		}
		dx = new int[] {-1,-1,-1,-1,-1,-1,-1,-1,-1,  0, 0, 0, 0, 0, 0, 0, 0, 0,  1, 1, 1, 1, 1, 1, 1, 1, 1};
		dy = new int[] { 1, 1, 1, 0, 0, 0,-1,-1,-1,  1, 1, 1, 0, 0, 0,-1,-1,-1,  1, 1, 1, 0, 0, 0,-1,-1,-1};
		dz = new int[] {-1, 0, 1,-1, 0, 1,-1, 0, 1, -1, 0, 1,-1, 0, 1,-1, 0, 1, -1, 0, 1,-1, 0, 1,-1, 0, 1};
		TileEntity te = null;
		for(int i=0; i<dx.length; i++) {
			te = worldObj.getBlockTileEntity(x+dx[i], y+dy[i], z+dz[i]);
			if(te==null || (te instanceof TileEntityTransmog)) 
				BlockTmog.setValid(worldObj,x+dx[i],y+dy[i],z+dz[i],good); 
		}
	}
	
	public void unvalid() {
		int[] dx = new int[] {-1,-1,-1,-1,-1,-1,-1,-1,-1,  0, 0, 0, 0, 0, 0, 0, 0, 0,  1, 1, 1, 1, 1, 1, 1, 1, 1};
		int[] dy = new int[] { 1, 1, 1, 0, 0, 0,-1,-1,-1,  1, 1, 1, 0, 0, 0,-1,-1,-1,  1, 1, 1, 0, 0, 0,-1,-1,-1};
		int[] dz = new int[] {-1, 0, 1,-1, 0, 1,-1, 0, 1, -1, 0, 1,-1, 0, 1,-1, 0, 1, -1, 0, 1,-1, 0, 1,-1, 0, 1};
		TileEntity te = null;
		for(int i=0; i<dx.length; i++) {
			te = worldObj.getBlockTileEntity(xCoord+dx[i], yCoord+dy[i], zCoord+dz[i]);
			if(te==null || (te instanceof TileEntityTransmog)) 
				BlockTmog.setValid(worldObj,xCoord+dx[i],yCoord+dy[i],zCoord+dz[i],false); 
		}
	}
	
	public void openGui(EntityPlayer player) {
		player.sendChatToPlayer("Gui opened!");
	}
}
