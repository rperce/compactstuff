package net.rperce.compactstuff.compactor;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by robert on 3/6/16.
 */
public class RecipePair {
    public ItemStack output, input;
    public RecipePair(ItemStack a, ItemStack b) { output = a; input = b; }
    public RecipePair(ItemStack a, Block b) { this(a, new ItemStack(b)); }
    public RecipePair(Block a, ItemStack b) { this(new ItemStack(a), b); }
    public RecipePair(Block a, Item b) { this(new ItemStack(a), new ItemStack(b)); }
    public RecipePair(Item a, Item b) { this(new ItemStack(a), new ItemStack(b)); }
    public String toString() { return String.format("(o=<%s>, i=<%s>)", output.toString(), input.toString()); }
}
