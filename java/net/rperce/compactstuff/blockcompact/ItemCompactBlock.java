package net.rperce.compactstuff.blockcompact;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemCompactBlock extends ItemBlock {
    public ItemCompactBlock(Block block) {
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
        BlockCompact.Meta name = BlockCompact.Meta.fromID(stack.getMetadata());
        return String.format("%s.%s", super.getUnlocalizedName(), name.getName());
    }
}
