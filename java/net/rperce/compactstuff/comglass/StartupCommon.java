package net.rperce.compactstuff.comglass;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.rperce.compactstuff.BaseStartup;

/**
 * Created by Robert on 2/27/2016.
 */
public class StartupCommon extends BaseStartup {
    public static BlockComGlass blockComGlass;
    @Override
    public void preInit() {
        blockComGlass = (BlockComGlass)(new BlockComGlass(true).setUnlocalizedName(BlockComGlass.canonicalName));
        GameRegistry.registerBlock(blockComGlass, blockComGlass.canonicalName);
    }
}
