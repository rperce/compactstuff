package mods.CompactStuff.tmog;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerTmog extends Container {
	private IInventory te;
	public ContainerTmog(IInventory player, IInventory te) {
		this.te = te;
		
		//te inventory
		for(int i=0; i<3; i++)
			for(int j=0; j<9; j++)
				this.addSlotToContainer(new Slot(te, i*9+j, 8+j*18, 41+i*18));
		
		//main player inventory
		for(int i=0; i<3; i++) {
			for(int j=0; j<9; j++) {
				this.addSlotToContainer(new Slot(player, 9+i*9+j, 8+j*18, 99+i*18));
			}
		}
				
		//hotbar
		for(int j=0; j<9; j++) {
			this.addSlotToContainer(new Slot(player, j, 8+j*18, 157));
		}
		
		
		
		
	}

	@Override public boolean canInteractWith(EntityPlayer player) {
		return te.isUseableByPlayer(player);
	}
	
	@Override public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		return null;
	}

}
