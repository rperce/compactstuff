package com.rperce.compactstuff;

import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.IFuelHandler;

public class CompactFuelHandler implements IFuelHandler {

    @Override
    public int getBurnTime(ItemStack fuel) {
        if (fuel.itemID == CompactStuff.plantBall.itemID) {
            if (fuel.getItemDamage() == 4) return 200;
            return 800;
        }
        return 0;
    }
}
