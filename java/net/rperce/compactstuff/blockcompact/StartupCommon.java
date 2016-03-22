package net.rperce.compactstuff.blockcompact;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.rperce.compactstuff.BaseStartup;

public class StartupCommon extends BaseStartup {
    public static BlockCompact compactBlock;
    public static BlockCompactSquishy compactBlockSquishy;

    @Override
    public void preInit() {
        compactBlock = (BlockCompact)(new BlockCompact().setUnlocalizedName(BlockCompact.canonicalName));
        GameRegistry.registerBlock(compactBlock, ItemCompactBlock.class, BlockCompact.canonicalName);

        compactBlockSquishy = (BlockCompactSquishy)(new BlockCompactSquishy().setUnlocalizedName(BlockCompactSquishy.canonicalName));
        GameRegistry.registerBlock(compactBlockSquishy, ItemCompactBlockSquishy.class, BlockCompactSquishy.canonicalName);
    }

    @Override
    public void postInit() {
        GameRegistry.addRecipe(BlockCompact.stack(BlockCompact.Meta.COMCOBBLE),
                "ccc",
                "ccc",
                "ccc",
                'c', Blocks.cobblestone);
    }
}
