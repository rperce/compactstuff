package net.rperce.compactstuff.compactor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import scala.tools.nsc.backend.icode.Members;

import java.util.Optional;

class ContainerCompactor extends Container {
    private final IInventory playerInventory;
    private final TileEntityCompactor te;

    public ContainerCompactor(IInventory player, TileEntityCompactor tileEntity) {
        this.te = tileEntity;
        this.playerInventory = player;

        // compactor inventory
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                this.addSlotToContainer(new Slot(this.te,
                        c  +  r * 9,
                        8  + 18 * c,
                        79 + 18 * r));
            }
        }

        // crafting grid
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                this.addSlotToContainer(new Slot(this.te,
                        27 +  r * 3 + c,
                        8  + 18 * c,
                        19 + 18 * r) {
                    @Override
                    public void onSlotChanged() {
                        craftMatrixChanged();
                    }
                });
            }
        }

        // output slot
        this.addSlotToContainer(new GhostSlot(this.te, 36, 89, 37));

        // compression slots
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 2; c++) {
                this.addSlotToContainer(new GhostSlot(this.te,
                        37  +  r * 2 + c,
                        134 + 18 * c,
                        19  + 18 * r));
            }
        }

        // player inventory
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                this.addSlotToContainer(new Slot(this.playerInventory,
                        9   +  r * 9 + c,
                        8   + 18 * c,
                        140 + 18 * r
                        ));
            }
        }

        // player hotbar
        for (int c = 0; c < 9; c++) {
            this.addSlotToContainer(new Slot(this.playerInventory,
                    c,
                    8 + 18 * c,
                    198));
        }
    }
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.te.isUseableByPlayer(player);
    }

    private class GhostSlot extends Slot {
        public GhostSlot(IInventory a, int b, int c, int d) {
            super(a, b, c, d);
        }
        @Override
        public boolean canTakeStack(EntityPlayer playerIn) {
            return false;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        int PLAYER_FIRST = 3 * 3 + 1 + 2 * 3 + 3 * 9;
        int PLAYER_LAST  = PLAYER_FIRST + 4 * 9 - 1;
        if (index > TileEntityCompactor.CRAFT_LAST && index < PLAYER_FIRST
                || index < 0 || index > PLAYER_LAST)
            return null;
        Slot slot = this.inventorySlots.get(index);
        ItemStack original = null;
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            original = stack.copy();
            if (index <= TileEntityCompactor.CRAFT_LAST) {
                if (!mergeItemStack(stack, PLAYER_FIRST, PLAYER_LAST + 1, false))
                    return null;
            } else {
                if (!mergeItemStack(stack, TileEntityCompactor.INV_FIRST,
                        TileEntityCompactor.INV_LAST + 1, false))
                    return null;
            }

            if (stack.stackSize < 1)
                slot.putStack(null);
            else
                slot.onSlotChanged();
        }
        return original;
    }

    public void craftMatrixChanged() {
        Optional<ItemStack> out = CompactorRecipes.findMatchingRecipe(
                new LocalCrafting(this, TileEntityCompactor.CRAFT_FIRST),
                this.te.getWorld());
        if (out.isPresent() && CompactorRecipes.isEnabled(this.te.enabled, out.get()))
            te.setInventorySlotContents(TileEntityCompactor.OUTPUT, out.get());
        else
            te.setInventorySlotContents(TileEntityCompactor.OUTPUT, null);
    }

    class LocalCrafting extends InventoryCrafting {
        public LocalCrafting(Container container, int first) {
            super(container, 3, 3);
            for (int i = 0; i < getSizeInventory(); i++) {
                this.setInventorySlotContents(i, container.inventoryItemStacks.get(i + first));
            }
        }
    }
}
