package com.rperce.compactstuff.compactor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class ContainerCompactor extends Container {
	private IInventory playerInventory, te;
	public ContainerCompactor(IInventory player, IInventory tileEntity) {
		this.playerInventory = player;
		this.te = tileEntity;
		((TileEntityCompactor)te).container = this;
		
		//slot args are: parent inventory, ID, x, y
		
		//compactor inventory
		for(int r=0; r<3; r++)
			for(int c=0; c<9; c++)
				this.addSlotToContainer(new Slot(te,r*9+c,8+18*c,79+18*r));
		
		//crafting grid
		for(int r=0; r<3; r++)
			for(int c=0; c<3; c++)
				this.addSlotToContainer(new Slot(te,27+r*3+c, 8+18*c, 19+18*r) {
					@Override public void onSlotChanged() {
						onCraftMatrixChanged();
					}
				});
		
		//output slot
		this.addSlotToContainer(new Slot(te,36,89,37) {
			@Override public boolean getHasStack() { return false; }
			@Override public boolean canTakeStack(EntityPlayer e) { return false; }
		});
		
		//compression slots
		for(int r=0; r<3; r++)
			for(int c=0; c<2; c++)
				this.addSlotToContainer(new Slot(te,37+r*2+c, 134+18*c,19+18*r) {
					@Override public boolean getHasStack() { return false; }
					@Override public boolean canTakeStack(EntityPlayer e) { return false; }
				});
		
		//player inventory
		for(int r=0; r<3; r++)
			for(int c=0; c<9; c++)
				this.addSlotToContainer(new Slot(playerInventory, 9+r*9+c, 8+18*c, 140+18*r));
		
		//player hotbar
		for(int c=0; c<9; c++)
			this.addSlotToContainer(new Slot(playerInventory, c, 8+18*c, 198));		
	}
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return te.isUseableByPlayer(player);
	}
	
	public void onCraftMatrixChanged() {
		TileEntityCompactor tec = (TileEntityCompactor)te;
		ItemStack out = CompactorRecipes.findMatchingRecipe(new LocalCrafting(this, tec.CRAFTFIRST), tec.worldObj);
		if(out==null || CompactorRecipes.isEnabled(tec.enabled, out));
		tec.setInventorySlotContents(tec.OUTPUT, out);
	}
	
	@Override public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) { //try {
		int PLAYERFIRST=3*3+1+2*3+3*9, PLAYERLAST=PLAYERFIRST+4*9-1;
		if(slotIndex>TileEntityCompactor.INVLAST && slotIndex<PLAYERFIRST || slotIndex<0 || slotIndex>PLAYERLAST)
			return null;
		Slot slot = (Slot)inventorySlots.get(slotIndex);
		ItemStack original = null;
		if(slot!=null && slot.getHasStack()) {
			ItemStack stack = slot.getStack();
			original = stack.copy();
			if(slotIndex<=TileEntityCompactor.INVLAST) {
				if(!mergeItemStack(stack, PLAYERFIRST, PLAYERLAST+1, false)) return null;
			}
			else {
				if(!mergeItemStack(stack, TileEntityCompactor.INVFIRST, TileEntityCompactor.INVLAST+1, false)) return null;
			}
			
			if(stack.stackSize<1) slot.putStack(null);
			else slot.onSlotChanged();
		}
		return original;
	}
	
}


class LocalCrafting extends InventoryCrafting {
	public LocalCrafting(Container container, int first) {
		super(container, 3, 3);
		for(int i=0; i<getSizeInventory(); i++) {
			this.setInventorySlotContents(i, (ItemStack)container.inventoryItemStacks.get(i+first));
		}
	}
	
}