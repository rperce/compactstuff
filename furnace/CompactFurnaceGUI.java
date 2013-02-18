package compactstuff.furnace;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import compactstuff.ImageFiles;


public class CompactFurnaceGUI extends GuiContainer {

	private TileEntityCompactFurnace furnace;
	public CompactFurnaceGUI(InventoryPlayer invPlayer, TileEntityCompactFurnace te) {
		super(new ContainerCompactFurnace(invPlayer,te));
		this.furnace = te;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		if(furnace.isCarbonFurnace()) {
			fontRenderer.drawString("Carbon Furnace", 8, 5, 0x404040);	
		} else {
			fontRenderer.drawString("Compression Furnace", 8, 5, 0x404040);
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		int texture = mc.renderEngine.getTexture(ImageFiles.FURNACE_GUI.path);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(texture);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        
        int rem;
        
        if (furnace.isBurning()) {
            rem = furnace.getBurnTimeRemainingScaled(12);
            drawTexturedModalRect(x + 56, y + 36 + 12 - rem, 176, 12 - rem, 14, rem + 2);
        }

        rem = furnace.getCookProgressScaled(24);
        drawTexturedModalRect(x + 79, y + 34, 176, 14, rem + 1, 16);
	}

}
