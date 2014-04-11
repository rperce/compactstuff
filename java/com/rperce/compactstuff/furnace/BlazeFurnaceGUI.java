package com.rperce.compactstuff.furnace;

import java.util.ArrayList;
import java.util.Arrays;

import com.rperce.compactstuff.client.ImageFiles;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class BlazeFurnaceGUI extends GuiContainer {
	private TileEntityBlazeFurnace furnace;
	public BlazeFurnaceGUI(IInventory player, TileEntityBlazeFurnace te) {
		super(new ContainerBlazeFurnace(player,te));
		furnace=te;
		ySize = 174;
	}
	
	@Override protected void drawGuiContainerForegroundLayer(int useless, int variables) {
		fontRenderer.drawString("Blaze Furnace", 8, 5, 0x404040);
		int i = Mouse.getX() * this.width / this.mc.displayWidth - (width - xSize) / 2;
	    int j = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1 - (height - ySize) / 2;
	    if(i>64 && i<81 && j>13 && j<52 && Mouse.getEventButton()==-1) {
	    	System.out.printf("Reserve: %d, scaled: %d",furnace.smeltingReserve,furnace.smeltingReserveScaled(8*64));
	    	drawHoveringText(new ArrayList<String>(Arrays.asList(new String[] {furnace.smeltingReserveScaled(8*64)+"/"+(6*64)})),
	    			i, j, fontRenderer);
		} 
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(ImageFiles.BLAZEFURNACE_GUI.loc);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        
        int rem;
        
        if (furnace.isFueling()) {
            rem = furnace.currentFuelTimeScaled(14);
            drawTexturedModalRect(x + 66, y + 54 + 14 - rem, 176, 14 - rem, 14, rem + 2);
        }

        rem = furnace.currentTimeLeftScaled(22);
        drawTexturedModalRect(x + 94, y + 42, 176, 14, rem, 16);
        
        rem = furnace.smeltingReserveScaled(31);
        drawTexturedModalRect(x + 68, y + 18 + 31 - rem, 176, 31 + 31 - rem, 10, rem);
	}
}
