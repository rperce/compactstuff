package com.rperce.compactstuff;

import java.util.List;

import com.rperce.compactstuff.client.CSIcons;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPlantBall extends Item {
    public static String[] names          = { "Oak Sapling Ball",
            "Spruce Sapling Ball", "Birch Sapling Ball", "Jungle Sapling Ball",
            "Seed Ball"                  };
    private static Icon[]  plantBallIcons = new Icon[5];

    public ItemPlantBall(int id) {
        super(id);
        setHasSubtypes(true);
        setMaxStackSize(64);
        setCreativeTab(CompactStuff.compactTab);
        setUnlocalizedName("plantBall");
    }

    @Override
    public void registerIcons(IconRegister ir) {
        for (int i = 0; i < 5; i++) {
            plantBallIcons[i] = ir.registerIcon(CSIcons.PREFIX + "plantBall"
                    + i);
        }
    }

    @Override
    public Icon getIconFromDamage(int dmg) {
        return plantBallIcons[dmg];
    }

    @Override
    public String getUnlocalizedName(ItemStack i) {
        try {
            return names[i.getItemDamage()];
        } catch (NullPointerException e) {
            return "Seed Ball";
        }
    }

    /** Warnings suppressed due to override constraints */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(int i, CreativeTabs tab, List list) {
        list.add(new ItemStack(i, 1, 0));
        list.add(new ItemStack(i, 1, 1));
        list.add(new ItemStack(i, 1, 2));
        list.add(new ItemStack(i, 1, 3));
        list.add(new ItemStack(i, 1, 4));
    }
}
