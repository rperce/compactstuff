package net.rperce.compactstuff.blockcompact;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * Created by Robert on 2/26/2016.
 */
public class ItemCompactBlockSquishy extends ItemBlock {
    public ItemCompactBlockSquishy(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        BlockCompactSquishy.Meta name = BlockCompactSquishy.Meta.fromID(stack.getMetadata());
        return String.format("%s.%s", super.getUnlocalizedName(), name.getName());
    }
}