package net.rperce.compactstuff.blockcompact;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.rperce.compactstuff.BlankStartup;

/**
 * Created by Robert on 2/26/2016.
 */
public class StartupCommon extends BlankStartup {
    public static BlockCompact compactBlock;
    public static BlockCompactSquishy compactBlockSquishy;

    public static void preInit() {
        compactBlock = (BlockCompact)(new BlockCompact().setUnlocalizedName(BlockCompact.canonicalName));
        GameRegistry.registerBlock(compactBlock, ItemCompactBlock.class, BlockCompact.canonicalName);

        compactBlockSquishy = (BlockCompactSquishy)(new BlockCompactSquishy().setUnlocalizedName(BlockCompactSquishy.canonicalName));
        GameRegistry.registerBlock(compactBlockSquishy, ItemCompactBlockSquishy.class, BlockCompactSquishy.canonicalName);
    }

    public static void postInit() {

        GameRegistry.addRecipe(BlockCompact.stack(BlockCompact.Meta.COMCOBBLE),
                "ccc",
                "ccc",
                "ccc",
                'c', Blocks.cobblestone);
    }
}
