package com.rperce.compactstuff.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.rperce.compactstuff.CompactStuff;
import com.rperce.compactstuff.client.CSIcons;

import net.minecraft.block.Block;
import net.minecraft.block.StepSound;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SmeltOnAStick extends Item {
    private static ArrayList<ItemStack> food = new ArrayList<ItemStack>();
    static {
        for (Item i : Arrays.asList(Item.porkRaw, Item.beefRaw,
                Item.chickenRaw, Item.potato, Item.fishRaw))
            food.add(new ItemStack(i));
    }

    public SmeltOnAStick(int id) {
        super(id);
        setMaxStackSize(1);
        setCreativeTab(CompactStuff.compactTab);
        setUnlocalizedName("Smelt-On-A-Stick");
        setMaxDamage(64);
        setFull3D();
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon(CSIcons.PREFIX + "smeltstick");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack thisStack, World world,
            EntityPlayer player) {
        if (!player.isSneaking() || thisStack.getItemDamage() < 0
                || world.isRemote) return thisStack;
        int slot = player.inventory.currentItem;
        if (thisStack.getItemDamage() < 64) {
            for (int i = -1; i < 2; i += 2) {
                int s = Math.min(8, Math.max(0, slot + i));
                ItemStack stack = player.inventory.getStackInSlot(s);
                if (stack == null) continue;
                for (ItemStack is : food) {
                    // System.out.println("Checking "+is.getItemName());
                    if (is.isItemEqual(stack)) {
                        player.inventory.decrStackSize(s, 1);
                        ItemStack smelt = FurnaceRecipes.smelting()
                                .getSmeltingResult(stack);
                        if (!player.inventory.addItemStackToInventory(smelt
                                .copy())) {
                            if (!world.isRemote)
                                world.spawnEntityInWorld(new EntityItem(world,
                                        player.posX, player.posY, player.posZ,
                                        smelt.copy()));
                        }
                        thisStack.damageItem(1, player);
                        return thisStack;
                    }
                }
            }
        }
        if (thisStack.getItemDamage() == 0) return thisStack;
        for (int i = -1; i < 2; i += 2) {
            int s = Math.min(8, Math.max(0, slot + i));
            ItemStack stack = player.inventory.getStackInSlot(s);
            if (stack == null) continue;
            if (stack.isItemEqual(new ItemStack(Item.coal))) {
                thisStack.setItemDamage(Math.max(0,
                        thisStack.getItemDamage() - 8));
                player.inventory.decrStackSize(s, 1);
                return thisStack;
            } else if (stack.isItemEqual(new ItemStack(Item.coal, 1, 1))) {
                thisStack.setItemDamage(Math.max(0,
                        thisStack.getItemDamage() - 8));
                player.inventory.decrStackSize(s, 1);
                return thisStack;
            } else if (stack.isItemEqual(new ItemStack(Item.blazeRod))) {
                thisStack.setItemDamage(Math.max(0,
                        thisStack.getItemDamage() - 12));
                player.inventory.decrStackSize(s, 1);
                return thisStack;
            } else if (stack.itemID == CompactStuff.plantBall.itemID) {
                int amt = 4;
                if (stack.isItemEqual(new ItemStack(CompactStuff.plantBall, 1,
                        4))) amt = 1;
                thisStack.setItemDamage(Math.max(0, thisStack.getItemDamage()
                        - amt));
                player.inventory.decrStackSize(s, 1);
                return thisStack;
            }
        }
        return thisStack;
    }

    @Override
    public boolean onItemUse(ItemStack thisStack, EntityPlayer player,
            World world, int x, int y, int z, int _, float __, float ___,
            float ____) {
        if (world.isAirBlock(x, y, z) || world.isRemote)
            return super.onItemUse(thisStack, player, world, x, y, z, _, __,
                    ___, ____);
        if (player.isSneaking()) return false;
        if (thisStack.getItemDamage() == 64) return false;
        ItemStack smelt = FurnaceRecipes.smelting().getSmeltingResult(
                new ItemStack(world.getBlockId(x, y, z), 1, world
                        .getBlockMetadata(x, y, z)));
        if (world.getBlockId(x, y, z) == Block.oreRedstone.blockID
                || world.getBlockId(x, y, z) == Block.oreRedstoneGlowing.blockID)
            smelt = new ItemStack(Item.redstone);
        if (smelt != null) {
            StepSound sound = Block.blocksList[smelt.itemID].stepSound;
            if (smelt.getItem() instanceof ItemBlock) {
                world.setBlock(x, y, z, smelt.itemID, smelt.getItemDamage(), 3);
                thisStack.damageItem(1, player);
                world.playSoundEffect(x + .5d, y + .5d, z + .5d,
                        sound.getPlaceSound(), (sound.getVolume() + 1f) / 2f,
                        sound.getPitch() * 0.8F);
                return true;
            }
            world.setBlockToAir(x, y, z);
            world.spawnEntityInWorld(new EntityItem(world, x + .5, y + .5,
                    z + .5, smelt.copy()));
            thisStack.damageItem(1, player);
            world.playSoundEffect(x + .5d, y + .5d, z + .5d,
                    sound.getBreakSound(), (sound.getVolume() + 1f) / 2f,
                    sound.getPitch() * .8f);
            return true;
        }
        return super.onItemUse(thisStack, player, world, x, y, z, _, __, ___,
                ____);
    }

    /** Warnings suppressed due to override constraints */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack thisStack, EntityPlayer player,
            List list, boolean boo) {
        list.add("Right-click on a block to smelt");
        list.add("Shift-click to charge or cook food");
        list.add("Fuel: (char)coal, blaze rods, plant balls");
    }
}
