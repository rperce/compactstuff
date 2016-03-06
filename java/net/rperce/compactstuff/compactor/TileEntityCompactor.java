package net.rperce.compactstuff.compactor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.rperce.compactstuff.Utilities;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by robert on 3/5/16.
 */
public class TileEntityCompactor extends TileEntity implements ISidedInventory, ITickable {
    // 3x9 inventory, 3x3 crafting grid, 1 output, 2x3 compacting
    private static final int SLOT_COUNT = 43;
    private ItemStack[] stacks = new ItemStack[SLOT_COUNT];
    public static final int     INV_FIRST   = 0,
                                INV_LAST    = 26,
                                CRAFT_FIRST = 27,
                                CRAFT_LAST  = 35,
                                OUTPUT      = 36,
                                COMFIRST    = 37,
                                COMLAST     = 42;

    private HashSet<ItemStack> enabled;
    public TileEntityCompactor() {
        super();
        CompactorRecipes.setup();
        enabled = new HashSet<>(CompactorRecipes.getDefaultEnabled());
        System.err.println("enabled is " + enabled.toString());
    }
    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return IntStream.rangeClosed(INV_FIRST, INV_LAST).toArray();
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemStackIn, EnumFacing direction) {
        if (direction == EnumFacing.DOWN) {
            return false;
        }
        return isItemValidForSlot(slot, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing direction) {
        if (direction == EnumFacing.UP) {
            return false;
        }
        return Utilities.inRange(INV_FIRST, slot, INV_LAST) &&
                !CompactorRecipes.isEnabledIngredient(this.enabled, stack);
    }

    @Override
    public int getSizeInventory() {
        return stacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return stacks[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        ItemStack stack = this.stacks[slot];
        if (stack == null) {
            return null;
        }
        if (stack.stackSize <= count) {
            ItemStack out = stack;
            this.stacks[slot] = null;
            return out;
        }
        ItemStack out = stack.splitStack(count);
        if (stack.stackSize == 0) {
            this.stacks[slot] = null;
        }
        return out;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = this.stacks[index];
        setInventorySlotContents(index, null);
        return stack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        try {
            this.stacks[slot] = stack;
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
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return Utilities.inRange(INV_FIRST, index, INV_LAST);
    }

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
        Arrays.fill(this.stacks, null);
    }

    @Override
    public String getName() {
        return "container.compactor.name";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public IChatComponent getDisplayName() {
        boolean c = this.hasCustomName();
        String n = this.getName();
        return c ? new ChatComponentText(n) : new ChatComponentTranslation(n);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.stacks = Utilities.readStacksFromNBT(compound);
        NBTTagList enabledList = compound.getTagList("Enabled", Utilities.NBT_TYPE_LIST);
        this.enabled = new HashSet<>();
        for (int i = 0; i < enabledList.tagCount(); i++) {
            NBTTagCompound itemTag = enabledList.getCompoundTagAt(i);
            this.enabled.add(ItemStack.loadItemStackFromNBT(itemTag));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        Utilities.writeStacksToNBT(compound, this.stacks);
        NBTTagList enabledList = new NBTTagList();
        for (ItemStack stack : this.enabled) {
            NBTTagCompound tag = new NBTTagCompound();
            stack.writeToNBT(tag);
            enabledList.appendTag(tag);
        }
        compound.setTag("Enabled", enabledList);
    }

    @Override
    public void update() {
        if (this.worldObj.isRemote) return;
        CompactorRecipes.getEnabledRecipes(this.enabled).anyMatch(r -> tryToMake(r) == 0);
    }
    public int tryToMake(IRecipe recipe) {
        if (recipe == null) return -1;
        LinkedList<ItemStack> reqs = CompactorRecipes.getRequirements(recipe)
                    .collect(Collectors.toCollection(LinkedList::new));

        System.err.println("Trying to make recipe " + recipe);
        ItemStack[] invCopy = stacks.clone();
        while (!reqs.isEmpty()) {
            ItemStack req = reqs.remove();
            for (int i = INV_FIRST; i <= INV_LAST; i++) {
                ItemStack stack = getStackInSlot(i);
                if (req.isItemEqual(stack)) {
                    int orig = stack.stackSize;
                    stack.stackSize = Math.max(0, stack.stackSize - req.stackSize);
                    req.stackSize -= (orig - stack.stackSize);
                    if (stack.stackSize == 0)
                        invCopy[i] = null;
                    if (req.stackSize < 1)
                        break;
                }
            }
            if (req.stackSize > 0) return -1;
        }
        System.err.println("Success!");
        for (int i = 0; i < invCopy.length; i++) {
            this.setInventorySlotContents(i, invCopy[i]);
        }
        this.markDirty();
        return 0;
    }
}
