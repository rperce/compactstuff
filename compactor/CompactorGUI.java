package mods.CompactStuff.compactor;

import mods.CompactStuff.furnace.TileEntityCompactFurnace;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.StatCollector;

public class CompactorGUI extends GuiChest {

	public CompactorGUI(IInventory par1iInventory, IInventory par2iInventory) {
		super(par1iInventory, par2iInventory);
		System.out.println("Created a new gui");
	}

	@Override protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRenderer.drawString("Compactor", 8, 6, 4210752);
    }
}
