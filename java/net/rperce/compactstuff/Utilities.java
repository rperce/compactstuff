package net.rperce.compactstuff;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.Arrays;

/**
 * Created by Robert on 2/26/2016.
 */
public class Utilities {
    public static final int NBT_TYPE_LIST = 10;
    public static String colonize(String s, String x) {
        return String.format("%s:%s", s, x);
    }

    public static String colonVariant(String s, String x, String v) {
        return String.format("%s:%s_%s", s, x, v);
    }

    public static boolean inRange(int lo, int x, int hi) {
        return lo <= x && x <= hi;
    }

    public static NBTTagCompound writeStacksToNBT(NBTTagCompound data, ItemStack... stacks) {
        return writeStacksToNBT(data, "itemsList", stacks);
    }

    public static NBTTagCompound writeStacksToNBT(NBTTagCompound data, String tagName, ItemStack... stacks) {
        if (stacks == null) {
            throw new IllegalArgumentException("null stacks input");
        }
        NBTTagList itemsList = new NBTTagList();
        for (int slot = 0; slot < stacks.length; slot++) {
            ItemStack stack = stacks[slot];
            if (stack != null) {
                NBTTagCompound item  = new NBTTagCompound();
                stack.writeToNBT(item);
                item.setByte("Slot", (byte) slot);
                itemsList.appendTag(item);
            }
        }
        data.setTag(tagName, itemsList);
        data.setShort(tagName + "Length", (short)stacks.length);
        return data;
    }

    public static ItemStack[] readStacksFromNBT(NBTTagCompound data) {
        return readStacksFromNBT(data, "itemsList");
    }

    public static ItemStack[] readStacksFromNBT(NBTTagCompound data, String tagName) {
        if (!data.hasKey(tagName) || !data.hasKey(tagName + "Length")) {
            throw new IllegalArgumentException("given data has no data for tag " + tagName);
        }
        ItemStack[] stacks = new ItemStack[data.getShort(tagName + "Length")];
        Arrays.fill(stacks, null);
        NBTTagList itemsList = data.getTagList(tagName, NBT_TYPE_LIST); // NBTBase.createNewByType()
        for (int i = 0; i < itemsList.tagCount(); i++) {
            NBTTagCompound item = itemsList.getCompoundTagAt(i);
            ItemStack stack = ItemStack.loadItemStackFromNBT(item);
            int slot = item.getByte("Slot") & 255;
            if (slot >= 0 && slot < stacks.length)
                stacks[slot] = stack;
        }
        return stacks;
    }

    public static void checkID(int got, int expected, String word) {
        if (got != expected) {
            System.err.printf("ERROR: Invalid ID for %s GUI! Expected %d, got %d.\n",
                    word,
                    expected,
                    got);
        }
    }
}
