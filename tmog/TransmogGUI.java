package mods.CompactStuff.tmog;

import mods.CompactStuff.client.ImageFiles;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;

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
		this.buttonList.add(new GuiButton(0, 46, 18, 66, 18, "Transmogrify"));
	}
	
	/*@Override public void initGui() {
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, 46, 18, 66, 18, "Transmogrify"));
	}*/
	
	/*@Override public void actionPerformed(GuiButton button) {
		if(button.id!=0) return;
	}*/
	
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
	}
}
