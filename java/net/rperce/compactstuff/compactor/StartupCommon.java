package net.rperce.compactstuff.compactor;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.rperce.compactstuff.BaseStartup;
import net.rperce.compactstuff.CompactStuff;
import net.rperce.compactstuff.GuiHandlerRegistry;

public class StartupCommon extends BaseStartup {
    public static BlockCompactor blockCompactor;
    @Override
    public void preInit() {
        blockCompactor = (BlockCompactor)(new BlockCompactor().setUnlocalizedName(BlockCompactor.canonicalName));
        GameRegistry.registerBlock(blockCompactor, BlockCompactor.canonicalName);
        GameRegistry.registerTileEntity(TileEntityCompactor.class, "tile_entity_" + BlockCompactor.canonicalName);

        GuiHandlerRegistry.getInstance().registerGuiHandler(new GuiHandlerCompactor(), CompactStuff.GUI_ID_COMPACTOR);
    }
}
