package net.rperce.compactstuff.compactor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.rperce.compactstuff.MouseButtonType;
import net.rperce.compactstuff.CommonProxy;
import net.rperce.compactstuff.CompactStuff;
import net.rperce.compactstuff.IntRange;

import java.awt.*;
import java.io.IOException;
import java.util.Optional;

@SideOnly(Side.CLIENT)
class GuiCompactor extends GuiContainer {
    private static final ResourceLocation texture = new ResourceLocation(CompactStuff.MODID, "textures/compactor_gui.png");
    private final IInventory tec;

    public GuiCompactor(EntityPlayer player, TileEntityCompactor tec) {
        super(new ContainerCompactor(player.inventory, tec));
        this.tec = tec;
        this.ySize = 222;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(0, this.guiLeft + 63, this.guiTop + 18, 9, 9, "x"));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString(tec.getDisplayName().getUnformattedText(), 8, 6, Color.darkGray.getRGB());
        this.mc.renderEngine.bindTexture(GuiCompactor.texture);
        int x0 = 133, y0 = 18;
        for (int r = 0; r < 3 ; r++) {
            for (int c = 0; c < 2; c++) {
                if (tec.getField(r * 2 + c) == 1) {
                    this.drawTexturedModalRect(
                            x0 + c * 18,
                            y0 + r * 18,
                            176, 0, 18, 18
                    );
                }
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1f, 1f, 1f, 1f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(GuiCompactor.texture);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        int slot = getSlotNumberIn(x, y, TileEntityCompactor.OUTPUT).orElse(
                   getSlotNumberIn(x, y, TileEntityCompactor.SELECTED).orElse(
                   -1));
        if (slot == -1) {
            super.mouseClicked(x, y, button);
        } else {
            boolean shift = false;
            if (GuiContainer.isShiftKeyDown()) shift = true;
            clickPacket(slot, button, shift);
        }
    }

    private Optional<Integer> getSlotNumberIn(int x, int y, IntRange range) {
        return getSlotAtPosition(x, y)
                .map(Slot::getSlotIndex)
                .filter(range::contains);
    }

    private Optional<Slot> getSlotAtPosition(int x, int y) {
        return this.inventorySlots.inventorySlots.stream()
                .filter(slot -> this.isMouseOverSlot(slot, x, y))
                .findFirst();
    }

    private boolean isMouseOverSlot(Slot slot, int x, int y) {
        return this.isPointInRegion(
                slot.xDisplayPosition, slot.yDisplayPosition, 16, 16,
                x, y
        );
    }

    private void clickPacket(int slot, int button, boolean shift) {
        CommonProxy.networkWrapper.sendToServer(
                new CompactorClickMessage(slot, MouseButtonType.from(button, shift))
        );
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.id == 0) {
            CommonProxy.networkWrapper.sendToServer(new CompactorClearMessage());
        }
    }
}
