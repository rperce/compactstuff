package mods.CompactStuff.tmog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Stack;

import mods.CompactStuff.CompactStuff;
import mods.CompactStuff.Lawn;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityTransmog extends TileEntity implements IInventory {
	public static final int CORE=0, SHIELD=1, FRAME=2;
	private int type=-1;
	private boolean valid;
	private Stack<String> worldActions;
	public TileEntityTransmog core;
	private int enabledIndex=0;
	private ArrayList<ItemStack> enabled;
	
	private ItemStack[] inventory;
	public TileEntityTransmog() {
		valid = false;
		core = null;
		enabled = new ArrayList<ItemStack>();
		worldActions = new Stack<String>();
		inventory = new ItemStack[3*9];
	}
	public boolean isLeftButtonEnabled() { return enabledIndex>0; }
	public boolean isRightButtonEnabled() { return enabledIndex+7<enabled.size(); }
	public static boolean areCoordsOverLeftButton(int x, int y) {
		if(y>33 || y<18) return false;
		if(x>12-Math.abs(26-y) && x<21) return true;
		return false;
	}
	
	@Override public void updateEntity() {
		if(worldActions.isEmpty() || worldObj==null) return;
		if(worldActions.pop().equals("CORE")) checkCoreValidity();
		else checkValidity();
	}
	public void clicked(EntityPlayer player) {
		if(!valid) return;
		core.openGui(player);
	}

	public void setType() {
		if(type==-1) type = getBlockMetadata() & 3;
	}
	public void checkValidity() {
		checkEdgeValidity(new HashSet<Integer>());
	} private boolean checkEdgeValidity(HashSet<Integer> been) {
		if(been.contains(this.hashCode())) return false;
		been.add(this.hashCode());
		setType();
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
//		System.out.println("Shielding "+(good?"":"not ")+"good");
		if(good) {
			//check frames;
			dx = new int[] {-1,-1,-1,-1,-1,-1,-1,-1,  0, 0, 0, 0,  1, 1, 1, 1, 1, 1, 1, 1};
			dy = new int[] { 1, 1, 1, 0, 0,-1,-1,-1,  1, 1,-1,-1,  1, 1, 1, 0, 0,-1,-1,-1};
			dz = new int[] {-1, 0, 1,-1, 1,-1, 0, 1, -1, 1,-1, 1, -1, 0, 1,-1, 1,-1, 0, 1};
			for(int i=0; i<dx.length; i++) {
				if(worldObj.getBlockId(x+dx[i], y+dy[i], z+dz[i])!=CompactStuff.transmog.blockID ||
				(worldObj.getBlockMetadata(x+dx[i],  y+dy[i],  z+dz[i])&3)!=FRAME) {
					System.out.printf("Missing frame at %d, %d, %d%n",x+dx[i],y+dy[i],z+dz[i]);
					good = false;
					break;
				}
			}
		}
//		System.out.println("Framing "+(good?"":"not ")+"good");
		dx = new int[] {-1,-1,-1,-1,-1,-1,-1,-1,-1,  0, 0, 0, 0, 0, 0, 0, 0, 0,  1, 1, 1, 1, 1, 1, 1, 1, 1};
		dy = new int[] { 1, 1, 1, 0, 0, 0,-1,-1,-1,  1, 1, 1, 0, 0, 0,-1,-1,-1,  1, 1, 1, 0, 0, 0,-1,-1,-1};
		dz = new int[] {-1, 0, 1,-1, 0, 1,-1, 0, 1, -1, 0, 1,-1, 0, 1,-1, 0, 1, -1, 0, 1,-1, 0, 1,-1, 0, 1};
		TileEntity te = null;
		for(int i=0; i<dx.length; i++) {
			te = worldObj.getBlockTileEntity(x+dx[i], y+dy[i], z+dz[i]);
			if(te!=null && (te instanceof TileEntityTransmog)) {
				BlockTmog.setValid(worldObj,x+dx[i],y+dy[i],z+dz[i],good); 
				((TileEntityTransmog)te).valid = good;
				((TileEntityTransmog)te).core = this;
			}
		}
	}
	
	public void unvalid() {
		if(!valid) return;
		int[] dx = new int[] {-1,-1,-1,-1,-1,-1,-1,-1,-1,  0, 0, 0, 0, 0, 0, 0, 0, 0,  1, 1, 1, 1, 1, 1, 1, 1, 1};
		int[] dy = new int[] { 1, 1, 1, 0, 0, 0,-1,-1,-1,  1, 1, 1, 0, 0, 0,-1,-1,-1,  1, 1, 1, 0, 0, 0,-1,-1,-1};
		int[] dz = new int[] {-1, 0, 1,-1, 0, 1,-1, 0, 1, -1, 0, 1,-1, 0, 1,-1, 0, 1, -1, 0, 1,-1, 0, 1,-1, 0, 1};
		int x=core.xCoord, y=core.yCoord, z=core.zCoord;
		TileEntity te = null;
		for(int i=0; i<dx.length; i++) {
			te = worldObj.getBlockTileEntity(x+dx[i], y+dy[i], z+dz[i]);
			if(te==null || (te instanceof TileEntityTransmog)) {
				BlockTmog.setValid(worldObj,x+dx[i],y+dy[i],z+dz[i],false); 
			}
		}
	}
	
	public void openGui(EntityPlayer player) {
		player.sendChatToPlayer("Gui opened!");
		player.openGui(CompactStuff.instance, 4, worldObj, xCoord, yCoord, zCoord);
	}
	
	@Override public void readFromNBT(NBTTagCompound tagList) {
        super.readFromNBT(tagList);
        
        this.valid = tagList.getBoolean("tetValid");
        this.type = tagList.getInteger("tetType");
		if(type==CORE) worldActions.push("CORE");
        else worldActions.push("EDGE");
		
		inventory = Lawn.readStacksFromNBT(tagList);
		enabled = (ArrayList<ItemStack>)Arrays.asList(Lawn.readStacksFromNBT(tagList,"enabledList"));
    }
	
	@Override public void writeToNBT(NBTTagCompound tagList) {
        super.writeToNBT(tagList);
        
        tagList.setBoolean("tetValid", valid);
        tagList.setInteger("tetType", this.type);
                
        Lawn.writeStacksToNBT(tagList, inventory);
        Lawn.writeStacksToNBT(tagList, "enabledList", enabled.toArray(new ItemStack[enabled.size()]));
	}

	@Override public ItemStack decrStackSize(int slot, int amt) {
		ItemStack out = getStackInSlot(slot).copy();
		if(out==null || amt==0) return null;
		if(getStackInSlot(slot).stackSize<=amt) {
			setInventorySlotContents(slot, null);
			onInventoryChanged();
			return out;
		} onInventoryChanged();
		return out.splitStack(amt);
	}

	@Override public ItemStack getStackInSlotOnClosing(int i) {
		ItemStack out = getStackInSlot(i);
		setInventorySlotContents(i, null);
		return out;
	}	

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord)==this && player.getDistance(xCoord, yCoord, zCoord)<=8d;
	}
	
	@Override public void setInventorySlotContents(int i, ItemStack s) { inventory[i]=s; }
	@Override public boolean isStackValidForSlot(int i, ItemStack s) { return true; }
	@Override public String getInvName() { return "compactstuff.transmogrifier"; }
 	@Override public ItemStack getStackInSlot(int i) { return inventory[i]; }
	@Override public int getSizeInventory() { return inventory.length; }
	@Override public boolean isInvNameLocalized() { return false; }
	@Override public int getInventoryStackLimit() { return 64; }
	@Override public void closeChest() {}
	@Override public void openChest() {}
}
