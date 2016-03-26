package net.rperce.compactstuff.compactor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.rperce.compactstuff.IntRange;
import net.rperce.compactstuff.slots.CraftSlot;
import net.rperce.compactstuff.slots.OutputOnlySlot;

import java.util.Optional;

class ContainerCompactor extends Container {
    private final TileEntityCompactor te;
    private final IntRange PLAYER_INV;
    public TileEntityCompactor getTileEntity() {
        return te;
    }
    private final int[] cachedFields;

    private static int slotID = 0;
    public ContainerCompactor(IInventory playerInventory, TileEntityCompactor tileEntity) {
        this.te = tileEntity;
        cachedFields = new int[te.getFieldCount()];

        slotID = 0;
        // compactor inventory
        addSlots(3, 9, this.te, 8, 79, "Slot");

        // crafting grid
        addSlots(3, 3, this.te, 8, 19, "CraftSlot");

        // output slot
        addSlots(1, 1, this.te, 89, 37, "OutputOnlySlot");

        // compression slots
        addSlots(3, 2, this.te, 134, 19, "OutputOnlySlot");

        slotID = 0;
        // player hotbar
        addSlots(1, 9, playerInventory, 8, 198, "Slot");

        // player inventory
        addSlots(3, 9, playerInventory, 8, 140, "Slot");


        int p_first = 3 * 3 + 1 + 2 * 3 + 3 * 9;
        int p_last = p_first + 4 * 9 - 1;
        PLAYER_INV = IntRange.closed(p_first, p_last);
    }

    private void addSlots(int rows, int cols, IInventory inv, int startX, int startY, String slotType) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                switch(slotType) {
                    case "Slot":
                        this.addSlotToContainer(new Slot(
                                inv, slotID++, startX + 18 * c, startY + 18 * r));
                        break;
                    case "CraftSlot":
                        this.addSlotToContainer(new CraftSlot(
                                inv, slotID++, startX + 18 * c, startY + 18 * r,
                                this::craftMatrixChanged
                        ));
                        break;
                    case "OutputOnlySlot":
                        this.addSlotToContainer(new OutputOnlySlot(
                                inv, slotID++, startX + 18 * c, startY + 18 * r
                        ));
                        break;
                }
            }
        }
    }
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.te.isUseableByPlayer(player);
    }

    private boolean isTransferableSlot(int index) {
        return TileEntityCompactor.CRAFTING.contains(index) ||
                TileEntityCompactor.INVENTORY.contains(index) ||
                PLAYER_INV.contains(index);
    }
    private boolean mergeItemStack(ItemStack stack, IntRange range, boolean backwards) {
        return this.mergeItemStack(stack, range.first(), range.last() + 1, backwards);
    }

    private boolean mergeItemStackFailed(ItemStack stack, IntRange range, boolean backwards) {
        return !this.mergeItemStack(stack, range, backwards);
    }
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        if (!isTransferableSlot(index))
            return null;

        Slot slot = this.inventorySlots.get(index);
        ItemStack original = null;
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            original = stack.copy();
            if (PLAYER_INV.contains(index)) {
                if (mergeItemStackFailed(stack, TileEntityCompactor.INVENTORY, false))
                    return null;
            } else {
                if (mergeItemStackFailed(stack, PLAYER_INV, false))
                    return null;
            }

            if (stack.stackSize < 1)
                slot.putStack(null);
            slot.onSlotChanged();
        }
        return original;
    }

    private void craftMatrixChanged() {
        Optional<ItemStack> out = CompactorRecipes.findMatchingRecipe(
                new LocalCrafting(this, TileEntityCompactor.CRAFTING.first()),
                this.te.getWorld());
        te.setInventorySlotContents(TileEntityCompactor.OUTPUT.first(), out.orElse(null));
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        boolean[] fieldHasChanged = new boolean[cachedFields.length];
        for (int i = 0; i < cachedFields.length; i++) {
            if (cachedFields[i] != te.getField(i)) {
                cachedFields[i] = te.getField(i);
                fieldHasChanged[i] = true;
            }
        }

        for(ICrafting crafter : this.crafters) {
            for (int i = 0; i < te.getFieldCount(); i++) {
                if (fieldHasChanged[i]) {
                    crafter.sendProgressBarUpdate(this, i, cachedFields[i]);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int data) {
        te.setField(id, data);
    }

    private class LocalCrafting extends InventoryCrafting {
        LocalCrafting(Container container, int first) {
            super(container, 3, 3);
            for (int i = 0; i < getSizeInventory(); i++) {
                this.setInventorySlotContents(i, container.inventoryItemStacks.get(i + first));
            }
        }
    }
    void clearCraftingGrid() {
        TileEntityCompactor.CRAFTING.stream().forEach(slotID -> {
            ItemStack stack = this.getSlot(slotID).getStack();
            if (stack != null) {
                if (this.mergeItemStackFailed(stack, PLAYER_INV, false)) {
                    this.mergeItemStack(stack, TileEntityCompactor.INVENTORY, false);
                }
                if (stack.stackSize == 0)
                    this.getSlot(slotID).putStack(null);
            }
        });
    }
}
