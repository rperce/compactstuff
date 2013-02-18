package compactstuff;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import compactstuff.boh.BagOfHoldingGUI;
import compactstuff.boh.ContainerBagOfHolding;
import compactstuff.boh.ItemBagOfHolding;
import compactstuff.furnace.CompactFurnaceGUI;
import compactstuff.furnace.ContainerCompactFurnace;
import compactstuff.furnace.TileEntityCompactFurnace;

import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler {
	// Client stuff
	public void registerRenderers() {
		// Nothing here as this is the server side proxy
	}
	
	public int addArmor(String armor) {
		return 0; //server doesn't give a crap
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		switch(id) {
			case 0:
			case 1:	return new ContainerCompactFurnace(player.inventory, (TileEntityCompactFurnace)te);
			case 2:
				if(player.getHeldItem().getItem() instanceof ItemBagOfHolding)
					return new ContainerBagOfHolding(player.inventory, ((ItemBagOfHolding)player.getHeldItem().getItem()).getInventory(player));
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		switch(id) {
			case 0: case 1: return new CompactFurnaceGUI(player.inventory, 
					(TileEntityCompactFurnace)world.getBlockTileEntity(x,y,z));
			case 2:
				if(player.getHeldItem().getItem() instanceof ItemBagOfHolding)
					return new BagOfHoldingGUI(player.inventory, ((ItemBagOfHolding)player.getHeldItem().getItem()).getInventory(player));
		}
		return null;
	}
}
