package mods.CompactStuff;

import mods.CompactStuff.boh.BagOfHoldingGUI;
import mods.CompactStuff.boh.ContainerBagOfHolding;
import mods.CompactStuff.boh.ItemBagOfHolding;
import mods.CompactStuff.compactor.CompactorGUI;
import mods.CompactStuff.compactor.TileEntityCompactor;
import mods.CompactStuff.furnace.CompactFurnaceGUI;
import mods.CompactStuff.furnace.ContainerCompactFurnace;
import mods.CompactStuff.furnace.TileEntityCompactFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
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
			case 3: return new ContainerChest(player.inventory, (TileEntityCompactor)te);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x,y,z);
		switch(id) {
			case 0: case 1: return new CompactFurnaceGUI(player.inventory, (TileEntityCompactFurnace)te);
			case 2:
				if(player.getHeldItem()!=null && player.getHeldItem().getItem() instanceof ItemBagOfHolding)
					return new BagOfHoldingGUI(player.inventory, ((ItemBagOfHolding)player.getHeldItem().getItem()).getInventory(player), player.getHeldItem());
			case 3:
				return new CompactorGUI(player.inventory, (TileEntityCompactor)te);
		}
		return null;
	}
}
