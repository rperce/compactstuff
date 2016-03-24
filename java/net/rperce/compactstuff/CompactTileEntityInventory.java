package net.rperce.compactstuff;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Arrays;

public abstract class CompactTileEntityInventory extends TileEntity implements ISidedInventory {
    protected abstract ItemStack[] getStacks();
    protected abstract void setStacks(ItemStack[] stacks);

    @Override
    public int getSizeInventory() {
        return this.getStacks().length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.getStacks()[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        ItemStack stack = this.getStackInSlot(slot);
        if (stack == null) {
            return null;
        }
        ItemStack out;
        if (stack.stackSize <= count) {
            out = stack.copy();
            this.getStacks()[slot] = null;
        } else {
            out = stack.splitStack(count);
            if (stack.stackSize == 0) {
                this.getStacks()[slot] = null;
            }
        }
        markDirty();
        return out;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = this.getStackInSlot(index);
        setInventorySlotContents(index, null);
        return stack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        try {
            this.getStacks()[slot] = stack;
            this.markDirty();
        } catch(ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override public void openInventory(EntityPlayer player) { }
    @Override public void closeInventory(EntityPlayer player) { }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) { }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        Arrays.fill(this.getStacks(), null);
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        boolean c = this.hasCustomName();
        String n = this.getName();
        return c ? new TextComponentString(n) : new TextComponentTranslation(n);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.setStacks(Utilities.readStacksFromNBT(compound));
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        Utilities.writeStacksToNBT(compound, this.getStacks());
    }

    protected int maxStackSize(ItemStack stack) {
        return Math.min(stack.getMaxStackSize(), this.getInventoryStackLimit());
    }

    protected void mergeItemStackWithSlots(ItemStack merge, IntRange range) {
        for (int i = range.first(); i < range.last(); i++) {
            ItemStack stack = this.getStackInSlot(i);
            if (stack == null) continue;
            if (stack.isItemEqual(merge)) {
                int origSize = stack.stackSize;
                int newSize = Math.min(origSize + merge.stackSize, this.maxStackSize(stack));
                stack.stackSize = newSize;
                merge.stackSize -= (newSize - origSize);
                if (merge.stackSize < 1) break;
            }
        }

        if (merge.stackSize > 0) {
            for (int i = range.first(); i < range.last(); i++) {
                ItemStack stack = this.getStackInSlot(i);
                if (stack == null) {
                    this.setInventorySlotContents(i, merge);
                    break;
                }
            }
        }
    }
}
