package com.rperce.compactstuff.tools;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import com.rperce.compactstuff.CompactStuff;
import com.rperce.compactstuff.client.CSIcons;

public class CompactSword extends ItemSword {
    private String path;

    public CompactSword(int id, EnumToolMaterial material, String path) {
        super(id, material);
        this.path = path;
        setCreativeTab(CompactStuff.compactTab);
    }

    @Override
    public void registerIcons(IconRegister i) {
        this.itemIcon = i.registerIcon(CSIcons.PREFIX + this.path);
    }

    @Override
    public boolean getIsRepairable(ItemStack thisOne, ItemStack otherOne) {
        return CompactTool.getIsRepairable(thisOne, otherOne, "Sword");
    }
}
