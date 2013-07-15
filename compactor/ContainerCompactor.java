package mods.CompactStuff.compactor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCompactor extends Container {
	private IInventory playerInventory, te;
	public ContainerCompactor(IInventory player, IInventory tileEntity) {
		this.playerInventory = player;
		this.te = tileEntity;
		
		//slot args are: parent inventory, ID, x, y
		
		//compactor inventory
		for(int r=0; r<3; r++)
			for(int c=0; c<9; c++)
				this.addSlotToContainer(new Slot(te,r*9+c,8+18*c,79+18*r));
		
		//crafting grid
		for(int r=0; r<3; r++)
			for(int c=0; c<3; c++)
				this.addSlotToContainer(new Slot(te,27+r*3+c, 8+18*c, 19+18*r));
		
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
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
	
		return null;
	}
	
}