package net.rperce.compactstuff.compactor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.rperce.compactstuff.CompactStuff;

import java.awt.*;

/**
 * Created by robert on 3/5/16.
 */
public class GuiCompactor extends GuiContainer {
    private static final ResourceLocation texture = new ResourceLocation(CompactStuff.MODID, "textures/compactor_gui.png");
    private TileEntityCompactor tec;

    public GuiCompactor(IInventory player, TileEntityCompactor tec) {
        super(new ContainerCompactor(player, tec));
        this.tec = tec;
        this.ySize = 222;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString("Compactor", 8, 6, Color.darkGray.getRGB());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1f, 1f, 1f, 1f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }
}
