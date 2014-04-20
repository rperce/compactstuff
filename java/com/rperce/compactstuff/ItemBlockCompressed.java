package com.rperce.compactstuff;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockCompressed extends ItemBlock {
    public ItemBlockCompressed(int id) {
        super(id);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int d) {
        return d;
    }

    /**
     * Warning suppressed due to override constraints
     */
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void getSubItems(int i, CreativeTabs tab, List list) {
        for (Integer meta : BlockCompressed.names.keySet())
            list.add(new ItemStack(i, 1, meta));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        try {
            return BlockCompressed.names.get(stack.getItemDamage());
        } catch (NullPointerException e) {
            return "Compressed Block";
        }
    }
}
