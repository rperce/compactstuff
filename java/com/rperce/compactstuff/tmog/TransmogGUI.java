package com.rperce.compactstuff.tmog;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.rperce.compactstuff.client.ImageFiles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TransmogGUI extends GuiContainer {
    private TileEntityTransmog tet;

    public TransmogGUI(InventoryPlayer inventory, TileEntityTransmog te) {
        super(new ContainerTmog(inventory, te));
        this.tet = te;
        this.ySize = 181;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(ImageFiles.TMOG_GUI.loc);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int a, int b) {
        this.fontRenderer.drawString("Transmogrifier", 8, 6, 0x404040);
        if (this.tet.isLeftButtonEnabled()) {
            if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) {
                drawTexturedModalRect(/* (width-xSize)/2+ */13, /*
                                                                 * (height-ySize)
                                                                 * /2+
                                                                 */18, 16, 181,
                        8, 16);
            } else if (this.tet.areCoordsOverLeftButton(Mouse.getX(),
                    Mouse.getY())) {
                drawTexturedModalRect(/* (width-xSize)/2+ */13, /*
                                                                 * (height-ySize)
                                                                 * /2+
                                                                 */18, 32, 181,
                        8, 16);
            } else {
                drawTexturedModalRect(/* (width-xSize)/2+ */13, /*
                                                                 * (height-ySize)
                                                                 * /2+
                                                                 */18, 0, 181,
                        8, 16);
            }
        } else {
            drawTexturedModalRect(/* (width-xSize)/2+ */13, /* (height-ySize)/2+ */
                    18, 48, 181, 8, 16);
        }
    }
}
