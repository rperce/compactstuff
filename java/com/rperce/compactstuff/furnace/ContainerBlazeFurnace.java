package com.rperce.compactstuff.furnace;

import com.rperce.compactstuff.compactor.TileEntityCompactor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBlazeFurnace extends Container {
	private IInventory player, te;
	public ContainerBlazeFurnace(IInventory iPlayer, IInventory iTileEntity) {
		this.player = iPlayer;
		this.te = iTileEntity;
		
		// slot args are parent inventory, slot, x, y
		
		// input slots
		for(int r=0; r<3; r++)
			for(int c=0; c<2; c++)
				this.addSlotToContainer(new Slot(te,r*2+c,16+18*c,24+18*r));
		
		// output slots
		for(int r=0; r<3; r++)
			for(int c=0; c<2; c++)
				this.addSlotToContainer(new Slot(te,6+r*2+c,126+18*c,24+18*r));
		
		//fuel slot
		this.addSlotToContainer(new Slot(te,12,65,70));
		
		//player inventory
		for(int r=0; r<3; r++)
			for(int c=0; c<9; c++)
				this.addSlotToContainer(new Slot(player, 9+r*9+c, 8+18*c, 92+18*r));
		
		//player hotbar
		for(int c=0; c<9; c++)
			this.addSlotToContainer(new Slot(player, c, 8+18*c, 150));	
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return te.isUseableByPlayer(player);
	}
	
	@Override public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		return null;		
	}
}
