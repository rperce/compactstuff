package com.rperce.compactstuff.furnace;

import com.rperce.compactstuff.Commons;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class TileEntityCobbleFurnace extends TileEntityCompactFurnace {
    private ItemStack[] furnaceItemStacks   = new ItemStack[3];
    private int         furnaceBurnTime     = 0;
    private int         currentItemBurnTime = 0;
    private int         furnaceCookTime     = 0;

    public TileEntityCobbleFurnace() {
        super();
    }

    @Override
    public boolean isCobbleFurnace() {
        return true;
    }

    @Override
    public String getInvName() {
        return "compactstuff.cobblefurnace";
    }

    @Override
    public void updateEntity() {
        boolean burning = this.furnaceBurnTime > 0;
        boolean invChange = false;

        if (this.furnaceBurnTime > 0) this.furnaceBurnTime -= 4;

        if (!this.worldObj.isRemote) {
            if (this.furnaceBurnTime == 0 && this.canSmelt()) {
                this.currentItemBurnTime = this.furnaceBurnTime = Commons
                        .getItemBurnTime(this.furnaceItemStacks[1]);

                if (this.furnaceBurnTime > 0) {
                    invChange = true;

                    if (this.furnaceItemStacks[1] != null) {
                        this.furnaceItemStacks[1].stackSize--;

                        if (this.furnaceItemStacks[1].stackSize == 0) {
                            this.furnaceItemStacks[1] = this.furnaceItemStacks[1]
                                    .getItem().getContainerItemStack(
                                            this.furnaceItemStacks[1]);
                        }
                    }
                }
            }

            if (this.isBurning() && this.canSmelt()) {
                this.furnaceCookTime += 4;

                if (this.furnaceCookTime >= 200) {
                    this.furnaceCookTime = 0;
                    this.smeltItem();
                    invChange = true;
                }
            } else {
                this.furnaceCookTime = 0;
            }

            if (burning != this.furnaceBurnTime > 0) {
                invChange = true;
                updateBlock();
            }
        }

        if (invChange) {
            this.onInventoryChanged();
        }
    }

    @Override
    public boolean canSmelt() {
        if (this.furnaceItemStacks[0] == null) {
            return false;
        }
        ItemStack var1 = FurnaceRecipes.smelting().getSmeltingResult(
                this.furnaceItemStacks[0]);
        if (var1 == null) return false;
        if (this.furnaceItemStacks[2] == null) return true;
        if (!this.furnaceItemStacks[2].isItemEqual(var1)) return false;
        int result = this.furnaceItemStacks[2].stackSize + var1.stackSize;
        return (result <= getInventoryStackLimit() && result <= var1
                .getMaxStackSize());
    }

    @Override
    public void smeltItem() {
        if (this.canSmelt()) {
            ItemStack var1 = FurnaceRecipes.smelting().getSmeltingResult(
                    this.furnaceItemStacks[0]);

            if (this.furnaceItemStacks[2] == null)
                this.furnaceItemStacks[2] = var1.copy();
            else if (this.furnaceItemStacks[2].isItemEqual(var1))
                this.furnaceItemStacks[2].stackSize += var1.stackSize;

            this.furnaceItemStacks[0].stackSize--;

            if (this.furnaceItemStacks[0].stackSize < 1)
                this.furnaceItemStacks[0] = null;
        }
    }

    @Override
    public int getSizeInventory() {
        return this.furnaceItemStacks.length;
    }

    @Override
    public int getFurnaceCookTime() {
        return this.furnaceCookTime;
    }

    @Override
    public int getFurnaceBurnTime() {
        return this.furnaceBurnTime;
    }

    @Override
    public int getCurrentItemBurnTime() {
        return this.currentItemBurnTime;
    }

    @Override
    public void setFurnaceCookTime(int time) {
        this.furnaceCookTime = time;
    }

    @Override
    public void setFurnaceBurnTime(int time) {
        this.furnaceBurnTime = time;
    }

    @Override
    public void setCurrentItemBurnTime(int time) {
        this.currentItemBurnTime = time;
    }

    @Override
    public ItemStack[] getFurnaceItemStacks() {
        return this.furnaceItemStacks;
    }

    @Override
    public void setFurnaceItemStacks(ItemStack[] i) {
        this.furnaceItemStacks = i;
    }
}
