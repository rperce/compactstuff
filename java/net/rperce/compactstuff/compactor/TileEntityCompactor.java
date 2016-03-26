package net.rperce.compactstuff.compactor;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.rperce.compactstuff.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TileEntityCompactor extends CompactTileEntityInventory implements ISidedInventory, ITickable {
    // 3x9 inventory, 3x3 crafting grid, 1 output, 2x3 compacting
    private static final int SLOT_COUNT = 43;
    private ItemStack[] stacks = new ItemStack[SLOT_COUNT];
    static final IntRange INVENTORY  = IntRange.closed(0, 26),
                          CRAFTING   = IntRange.closed(27, 35),
                          OUTPUT     = IntRange.only(36),
                          SELECTED   = IntRange.closed(37, 42);

    private ItemStackSet enabled;
    TileEntityCompactor() {
        super();
        CompactorRecipes.setup();
        enabled = new ItemStackSet(CompactorRecipes.getDefaultEnabled());
    }

    @Override
    protected ItemStack[] getStacks() {
        return stacks;
    }

    @Override
    protected void setStacks(ItemStack[] stacks) {
        this.stacks = stacks;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return INVENTORY.stream().toArray();
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemStackIn, EnumFacing direction) {
        return direction != EnumFacing.DOWN && isItemValidForSlot(slot, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing direction) {
        return direction != EnumFacing.UP && INVENTORY.contains(slot) &&
                !CompactorRecipes.isEnabledIngredient(this.enabled, stack);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return INVENTORY.contains(index);
    }

    @Override
    public int getFieldCount() {
        return SELECTED.count();
    }

    private final int[] autocompact = new int[this.getFieldCount()];
    @Override
    public int getField(int id) {
        return autocompact[id];
    }

    @Override
    public void setField(int id, int value) {
        System.err.printf("Setting %d to %d\n", id, value);
        autocompact[id] = value;
    }

    void acceptCompactorMessage(CompactorClickMessage message) {
        int globalSlotIndex = message.getSlot();
        MouseButtonType clickType = message.getClickType();
        System.err.printf("Accepting message with %d and %s\n", globalSlotIndex, clickType);
        if (TileEntityCompactor.OUTPUT.contains(globalSlotIndex)) {
            selectOutputStack();
        }
        if (TileEntityCompactor.SELECTED.contains(globalSlotIndex)) {
            alterSelectedStack(globalSlotIndex, clickType);
        }
    }

    private void selectOutputStack() {
        ItemStack output = this.getStackInSlot(TileEntityCompactor.OUTPUT.first());
        if (output == null) return;
        boolean alreadyThere = TileEntityCompactor.SELECTED.stream()
                .mapToObj(this::getStackInSlot)
                .filter(Utilities::isNotNull)
                .anyMatch(output::isItemEqual);
        if (alreadyThere) return;

        TileEntityCompactor.SELECTED.stream()
                .filter(slot -> this.getStackInSlot(slot) == null)
                .findFirst()
                .ifPresent(slot -> {
                    this.setInventorySlotContents(slot, output);

                    int fieldID = slot - TileEntityCompactor.SELECTED.first();
                    boolean enab = CompactorRecipes.isEnabled(enabled, output);
                    this.setField(fieldID, enab ? 1 : 0);
                });
    }

    private void alterSelectedStack(int globalSlotIndex, MouseButtonType clickType) {
        ItemStack stack = this.getStackInSlot(globalSlotIndex);
        if (stack == null) return;
        int fieldID = globalSlotIndex - TileEntityCompactor.SELECTED.first();

        if (clickType.hasShift()) {
            this.setInventorySlotContents(globalSlotIndex, null);
            if (CompactorRecipes.isEnabled(CompactorRecipes.getDefaultEnabled(), stack)) {
                enabled.add(stack);
            } else {
                enabled.remove(stack);
            }
            setField(fieldID, 0);
        }
        if (clickType.equals(MouseButtonType.LEFT)) {
            if (CompactorRecipes.containsRecipe(stack)) {
                Optional<IRecipe> recipe = CompactorRecipes.getRecipesFor(stack).findFirst();
                recipe.ifPresent(this::tryToMake);
            }
        } else if (clickType.equals(MouseButtonType.RIGHT)) {
            if (CompactorRecipes.isEnabled(enabled, stack)) {
                enabled.remove(stack);
                setField(fieldID, 0);
            } else {
                enabled.add(stack);
                setField(fieldID, 1);
            }
        }
        markDirty();
    }

    @Override
    public String getName() {
        return "container.compactor.name";
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        NBTTagList enabledList = compound.getTagList("Enabled", Utilities.NBT_TYPE_LIST);
        this.enabled = new ItemStackSet();
        for (int i = 0; i < enabledList.tagCount(); i++) {
            NBTTagCompound itemTag = enabledList.getCompoundTagAt(i);
            this.enabled.add(ItemStack.loadItemStackFromNBT(itemTag));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

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
        for (IRecipe recipe : recipes) {
            if (this.tryToMake(recipe))
                break;
        }
    }
    private boolean tryToMake(IRecipe recipe) {
        if (recipe == null || this.worldObj.isRemote || this.isMainInventoryEmpty()) return false;

        Stream<ItemStack> reqs = CompactorRecipes.getRequirements(recipe);
        int[] remove = getChangesFromRemoving(reqs);
        if (remove == null || !this.hasRoomFor(recipe.getRecipeOutput(), INVENTORY, remove)) return false;

        applyRemovalArray(remove);
        this.mergeItemStackWithSlots(recipe.getRecipeOutput().copy(), INVENTORY);

        this.markDirty();
        return true;
    }
    private void applyRemovalArray(int[] remove) {
        INVENTORY.stream().forEach(i ->
            this.decrStackSize(i, remove[i])
        );
    }
    private boolean isMainInventoryEmpty() {
        boolean empty = true;
        for (int i = INVENTORY.first(); empty && i <= INVENTORY.last(); i++) {
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
            for (int i = INVENTORY.first(); i <= INVENTORY.last(); i++) {
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

}
