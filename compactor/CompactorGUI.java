package mods.CompactStuff.compactor;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import mods.CompactStuff.client.CompactPacket;
import mods.CompactStuff.client.ImageFiles;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

public class CompactorGUI extends GuiContainer {
	TileEntityCompactor te;
	public CompactorGUI(IInventory play, TileEntityCompactor tile) {
		super(new ContainerCompactor(play, tile));
		this.ySize=222;
		this.te=tile;
	}

	@Override protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRenderer.drawString("Compactor", 8, 6, 4210752);
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(ImageFiles.COMPACTOR_GUI.path);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x,y,0,0,xSize,ySize);
	}
	
	private int getSlotNumberBetween(int x, int y, int first, int last) {
		Slot s = getSlotAtPosition(x,y);
		int slot=-1;
		if(s!=null) slot = s.slotNumber;
		if(s==null || slot<first || slot>last) return -1;
		return slot;
	}
	@Override
	protected void mouseClicked(int x, int y, int button) {
		int slot = getSlotNumberBetween(x,y,TileEntityCompactor.CRAFTFIRST, TileEntityCompactor.CRAFTLAST);
		if(slot==-1) {
			super.mouseClicked(x, y, button);
			return;
		}
		TileEntityCompactor tile = te;
		CompactPacket packet = new CompactPacket("compactorClick");
		packet.writeInts(tile.xCoord, tile.yCoord, tile.zCoord, slot, button);
		packet.send();
	}
	
	@Override
	protected void mouseMovedOrUp(int x, int y, int which) {
		System.out.printf("MovedOrUp: %d, %d, %d%n",x,y,which);
		int slot = getSlotNumberBetween(x,y,TileEntityCompactor.CRAFTFIRST, TileEntityCompactor.CRAFTLAST);
		if(slot==-1) {
			super.mouseMovedOrUp(x, y, which);
			return;
		}
		TileEntityCompactor tile = te;
		CompactPacket packet = new CompactPacket("compactorClick");
		System.out.printf("Trying to write %d, %d, %d, %d, %d%n",tile.xCoord, tile.yCoord, tile.zCoord, slot, 0);
		packet.writeInts(tile.xCoord, tile.yCoord, tile.zCoord, slot, 0);
		packet.send();
	}
	
	private Slot getSlotAtPosition(int par1, int par2)
    {
        for (int k = 0; k < this.inventorySlots.inventorySlots.size(); ++k)
        {
            Slot slot = (Slot)this.inventorySlots.inventorySlots.get(k);

            if (this.isMouseOverSlot(slot, par1, par2))
            {
                return slot;
            }
        }

        return null;
    }
	private boolean isMouseOverSlot(Slot par1Slot, int par2, int par3)
    {
        return this.isPointInRegion(par1Slot.xDisplayPosition, par1Slot.yDisplayPosition, 16, 16, par2, par3);
    }
}
