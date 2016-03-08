package net.rperce.compactstuff.compactor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.rperce.compactstuff.Utilities;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    public HashSet<ItemStack> enabled;
    public TileEntityCompactor() {
        super();
        CompactorRecipes.setup();
        enabled = new HashSet<>(CompactorRecipes.getDefaultEnabled());
    }
    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return IntStream.rangeClosed(INV_FIRST, INV_LAST).toArray();
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemStackIn, EnumFacing direction) {
        return direction != EnumFacing.DOWN && isItemValidForSlot(slot, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing direction) {
        return direction != EnumFacing.UP && Utilities.inRange(INV_FIRST, slot, INV_LAST) &&
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
        ItemStack out;
        if (stack.stackSize <= count) {
            out = stack.copy();
            this.stacks[slot] = null;
        } else {
            out = stack.splitStack(count);
            if (stack.stackSize == 0) {
                this.stacks[slot] = null;
            }
        }
        markDirty();
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
        List<IRecipe> recipes = CompactorRecipes.getEnabledRecipes(this.enabled).collect(Collectors.toList());
        for (int i = 0; i < recipes.size(); i++) {
            IRecipe recipe = recipes.get(i);
            if (this.tryToMake(recipe))
                break;
        }
    }
    private boolean tryToMake(IRecipe recipe) {
        if (recipe == null || this.worldObj.isRemote || this.isMainInventoryEmpty()) return false;

        Stream<ItemStack> reqs = CompactorRecipes.getRequirements(recipe);
        int[] remove = getChangesFromRemoving(reqs);
        if (remove == null || !hasRoomFor(recipe.getRecipeOutput(), remove)) return false;

        for (int i = INV_FIRST; i < INV_LAST; i++) {
            this.decrStackSize(i, remove[i]);
        }

        ItemStack out = recipe.getRecipeOutput().copy();
        for (int i = INV_FIRST; i < INV_LAST; i++) {
            ItemStack stack = this.getStackInSlot(i);
            if (stack == null) continue;
            if (stack.isItemEqual(out)) {
                int origSize = stack.stackSize;
                int newSize = Math.min(origSize + out.stackSize,
                        Math.min(stack.getMaxStackSize(), this.getInventoryStackLimit()));
                stack.stackSize = newSize;
                out.stackSize -= (newSize - origSize);
                if (out.stackSize < 1) break;
            }
        }
        if (out.stackSize > 0) {
            for (int i = INV_FIRST; i < INV_LAST; i++) {
                ItemStack stack = this.getStackInSlot(i);
                if (stack == null) {
                    this.setInventorySlotContents(i, out);
                    break;
                }
            }
        }
        this.worldObj.markBlockForUpdate(this.pos);
        this.markDirty();
        return true;
    }
    private boolean isMainInventoryEmpty() {
        boolean empty = true;
        for (int i = INV_FIRST; empty && i <= INV_LAST; i++) {
            if (stacks[i] != null) empty = false;
        }
        return empty;
    }
    private int[] getChangesFromRemoving(Stream<ItemStack> itemStacks) {
        List<ItemStack> it = itemStacks.collect(Collectors.toList());
        int[] want   = it.stream().mapToInt(stack -> stack.stackSize).toArray();
        int[] remove = new int[stacks.length];
        Arrays.fill(remove, 0);
        for (int c = 0; c < it.size(); c++) {
            ItemStack req = it.get(c);
            for (int i = INV_FIRST; i <= INV_LAST; i++) {
                if (stacks[i] == null || !stacks[i].isItemEqual(req)) continue;
                int origSize = stacks[i].stackSize - remove[i];
                int newSize  = Math.max(0, origSize - want[c]);
                remove[i] = stacks[i].stackSize - newSize;
                want[c]  -= (origSize - newSize);
                if (want[c] < 1) break;
            }
            if (want[c] > 0) return null;
        }
        return remove;
    }
    private boolean hasRoomFor(ItemStack stack, int[] remove) {
        int want = stack.stackSize;
        for (int i = INV_FIRST; i <= INV_LAST; i++) {
            if (stacks[i] == null) {
                want -= Math.min(stack.getMaxStackSize(), this.getInventoryStackLimit());
                if (want < 1) break;
            }
            if (!stacks[i].isItemEqual(stack)) continue;
            int origSize = stacks[i].stackSize - remove[i];
            int newSize  = Math.max(0, origSize - want);
            want = (origSize - newSize);
            if (want < 1) break;
        }
        return want < 1;
    }
}
