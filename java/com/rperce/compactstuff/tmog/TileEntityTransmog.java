package com.rperce.compactstuff.tmog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Stack;

import com.rperce.compactstuff.Commons;
import com.rperce.compactstuff.CompactStuff;
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
		this.valid = false;
		this.core = null;
		this.enabled = new ArrayList<ItemStack>();
		this.worldActions = new Stack<String>();
		this.inventory = new ItemStack[3*9];
	}
	public boolean isLeftButtonEnabled() { return this.enabledIndex>0; }
	public boolean isRightButtonEnabled() { return this.enabledIndex+7<this.enabled.size(); }
	public static boolean areCoordsOverLeftButton(int x, int y) {
		if(y>33 || y<18) return false;
		if(x>12-Math.abs(26-y) && x<21) return true;
		return false;
	}
	
	@Override public void updateEntity() {
		if(this.worldActions.isEmpty() || this.worldObj==null) return;
		//from this point forward, there is an action and a world to do it in
		
		if(this.worldActions.pop().equals("CORE")) checkCoreValidity();
		else checkEdgeValidity(new HashSet<Integer>());
	}
	public void clicked(EntityPlayer player) {
		if(!this.valid) return;
		this.core.openGui(player);
	}

	public void setType() {
		if(this.type==-1) this.type = getBlockMetadata() & 3;
	}
	public void checkValidity() {
		checkEdgeValidity(new HashSet<Integer>());
	} private boolean checkEdgeValidity(HashSet<Integer> been) {
		if(been.contains(this.hashCode())) return false;
		been.add(this.hashCode());
		setType();
		if(this.type==CORE) {
			checkCoreValidity();
			been.clear();
			return true;
		}
		TileEntity[] tes;
		for(int d=-1; d<2; d+=2) {
			tes = new TileEntity[] {this.worldObj.getBlockTileEntity(this.xCoord+d, this.yCoord, this.zCoord),
					this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord+d, this.zCoord),
					this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord+d) };
			for(TileEntity te : tes) {
				if(te==null || !(te instanceof TileEntityTransmog)) continue;
				if(((TileEntityTransmog)te).checkEdgeValidity(been)) return true;
			}
		}
		return false;
	} private void checkCoreValidity() {
		int x=this.xCoord, y=this.yCoord, z=this.zCoord;
		//check shielding;
		int[] dx = {-1, 0, 0, 0, 0, 1},
			  dy = { 0, 1, 0,-1, 0, 0},
			  dz = { 0, 0, 1, 0,-1, 0};
		boolean good = true;
		for(int i=0; i<dx.length; i++) {
			if(this.worldObj.getBlockId(x+dx[i], y+dy[i], z+dz[i])!=CompactStuff.transmog.blockID ||
			(this.worldObj.getBlockMetadata(x+dx[i],  y+dy[i],  z+dz[i])&3)!=SHIELD) {
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
				if(this.worldObj.getBlockId(x+dx[i], y+dy[i], z+dz[i])!=CompactStuff.transmog.blockID ||
				(this.worldObj.getBlockMetadata(x+dx[i],  y+dy[i],  z+dz[i])&3)!=FRAME) {
//					System.out.printf("Missing frame at %d, %d, %d%n",x+dx[i],y+dy[i],z+dz[i]);
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
			te = this.worldObj.getBlockTileEntity(x+dx[i], y+dy[i], z+dz[i]);
			if(te!=null && (te instanceof TileEntityTransmog)) {
				BlockTmog.setValid(this.worldObj,x+dx[i],y+dy[i],z+dz[i],good); 
				((TileEntityTransmog)te).valid = good;
				((TileEntityTransmog)te).core = this;
			}
		}
	}
	
	public void unvalid() {
		if(!this.valid) return;
		int[] dx = new int[] {-1,-1,-1,-1,-1,-1,-1,-1,-1,  0, 0, 0, 0, 0, 0, 0, 0, 0,  1, 1, 1, 1, 1, 1, 1, 1, 1};
		int[] dy = new int[] { 1, 1, 1, 0, 0, 0,-1,-1,-1,  1, 1, 1, 0, 0, 0,-1,-1,-1,  1, 1, 1, 0, 0, 0,-1,-1,-1};
		int[] dz = new int[] {-1, 0, 1,-1, 0, 1,-1, 0, 1, -1, 0, 1,-1, 0, 1,-1, 0, 1, -1, 0, 1,-1, 0, 1,-1, 0, 1};
		int x=this.core.xCoord, y=this.core.yCoord, z=this.core.zCoord;
		TileEntity te = null;
		for(int i=0; i<dx.length; i++) {
			te = this.worldObj.getBlockTileEntity(x+dx[i], y+dy[i], z+dz[i]);
			if(te==null || (te instanceof TileEntityTransmog)) {
				BlockTmog.setValid(this.worldObj,x+dx[i],y+dy[i],z+dz[i],false); 
			}
		}
	}
	
	public void openGui(EntityPlayer player) {
		//player.sendChatToPlayer("Gui opened!");
		player.openGui(CompactStuff.instance, 4, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
	}
	
	@Override public void readFromNBT(NBTTagCompound tagList) {
		super.readFromNBT(tagList);
        
		this.valid = tagList.getBoolean("tetValid");
		this.type = tagList.getInteger("tetType");
		if(this.type==CORE) this.worldActions.push("CORE");
        else this.worldActions.push("EDGE");
		
		this.inventory = Commons.readStacksFromNBT(tagList);
		ItemStack[] enabledTemp = Commons.readStacksFromNBT(tagList,"enabledList");
		if(enabledTemp==null) {
			this.enabled = new ArrayList<ItemStack>();
		} else  this.enabled = (ArrayList<ItemStack>)Arrays.asList(enabledTemp);
    }
	
	@Override public void writeToNBT(NBTTagCompound tagList) {
        super.writeToNBT(tagList);
        
        tagList.setBoolean("tetValid", this.valid);
        tagList.setInteger("tetType", this.type);
                
        Commons.writeStacksToNBT(tagList, this.inventory);
        Commons.writeStacksToNBT(tagList, "enabledList", this.enabled.toArray(new ItemStack[this.enabled.size()]));
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
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord)==this && player.getDistance(this.xCoord, this.yCoord, this.zCoord)<=8d;
	}
	
	@Override public void setInventorySlotContents(int i, ItemStack s) { this.inventory[i]=s; }
	@Override public boolean isItemValidForSlot(int i, ItemStack s) { return true; }
	@Override public String getInvName() { return "compactstuff.transmogrifier"; }
 	@Override public ItemStack getStackInSlot(int i) { return this.inventory[i]; }
	@Override public int getSizeInventory() { return this.inventory.length; }
	@Override public boolean isInvNameLocalized() { return false; }
	@Override public int getInventoryStackLimit() { return 64; }
	@Override public void closeChest() { /* do nothing */ }
	@Override public void openChest() { /* do nothing */ }
}
