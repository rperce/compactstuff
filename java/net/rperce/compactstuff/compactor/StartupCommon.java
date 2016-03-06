package net.rperce.compactstuff.compactor;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.rperce.compactstuff.BlankStartup;
import net.rperce.compactstuff.CompactStuff;
import net.rperce.compactstuff.GuiHandlerRegistry;

/**
 * Created by robert on 3/5/16.
 */
public class StartupCommon extends BlankStartup {
    public static BlockCompactor blockCompactor;
    public static void preInit() {
        blockCompactor = (BlockCompactor)(new BlockCompactor().setUnlocalizedName(BlockCompactor.canonicalName));
        GameRegistry.registerBlock(blockCompactor, blockCompactor.canonicalName);
        GameRegistry.registerTileEntity(TileEntityCompactor.class, "tile_entity_" + BlockCompactor.canonicalName);

        GuiHandlerRegistry.getInstance().registerGuiHandler(new GuiHandlerCompactor(), CompactStuff.GUI_ID_COMPACTOR);
    }
}
