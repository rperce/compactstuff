package compactstuff.boh;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import org.lwjgl.opengl.GL11;

import compactstuff.ImageFiles;

public class BagOfHoldingGUI extends GuiContainer {
	private InventoryPlayer inv;
	public BagOfHoldingGUI(InventoryPlayer inv, InventoryBagOfHolding bag) {
		super(new ContainerBagOfHolding(inv,bag));
		this.inv = inv;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int _, int __) {
		fontRenderer.drawString("Bag of Holding", 8, 5, 0x404040);
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		int texture = mc.renderEngine.getTexture(ImageFiles.HOLDINGBAG_GUI.path);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(texture);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);		
	}
	
	@Override protected void mouseClicked(int x, int y, int mouse) {
		if(getSlotAtPosition(x,y)!=null && getSlotAtPosition(x,y).slotNumber-(27+27) == inv.currentItem)
				return;
		super.mouseClicked(x, y, mouse);
	}
	
	private Slot getSlotAtPosition(int x, int y) {
        for (int i = 0; i < this.inventorySlots.inventorySlots.size(); i++) {
            Slot slot = (Slot)this.inventorySlots.inventorySlots.get(i);

            if (this.isMouseOverSlot(slot, x, y))
                return slot;
        }
        return null;
    }
	
	private boolean isMouseOverSlot(Slot slot, int x, int y) {
        return isPointInRegion(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, x, y);
    }
}
