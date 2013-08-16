package mods.CompactStuff.tmog;

import mods.CompactStuff.client.ImageFiles;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TransmogGUI extends GuiContainer {
	private InventoryPlayer playerInv;
	private TileEntityTransmog tet;
	public TransmogGUI(InventoryPlayer inventory, TileEntityTransmog te) {
		super(new ContainerTmog(inventory, te));
		tet = te;
		playerInv = inventory;
		ySize = 181;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(ImageFiles.TMOG_GUI.path);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
	
	@Override protected void drawGuiContainerForegroundLayer(int a, int b) {
		this.fontRenderer.drawString("Transmogrifier",8,6,0x404040);
		if(tet.isLeftButtonEnabled()) {
			if(Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) {
				drawTexturedModalRect(/*(width-xSize)/2+*/13, /*(height-ySize)/2+*/18, 16, 181, 8, 16);
			} else if(tet.areCoordsOverLeftButton(Mouse.getX(),Mouse.getY())) {
				drawTexturedModalRect(/*(width-xSize)/2+*/13, /*(height-ySize)/2+*/18, 32, 181, 8, 16);
			} else {
				drawTexturedModalRect(/*(width-xSize)/2+*/13, /*(height-ySize)/2+*/18, 0, 181, 8, 16);
			}
		} else {
			drawTexturedModalRect(/*(width-xSize)/2+*/13, /*(height-ySize)/2+*/18, 48, 181, 8, 16);
		}
	}
}
