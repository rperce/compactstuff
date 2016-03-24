package net.rperce.compactstuff.compactor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.rperce.compactstuff.BaseStartup;
import net.rperce.compactstuff.CompactStuff;
import net.rperce.compactstuff.Utilities;

public class StartupClientOnly extends BaseStartup {
    @Override
    public void init() {
        Item itemComGlass = GameRegistry.findItem(CompactStuff.MODID, BlockCompactor.canonicalName);
        ModelResourceLocation resourceLocation = new ModelResourceLocation(Utilities.colonize(
                CompactStuff.MODID, BlockCompactor.canonicalName), "inventory");
        int meta = 0;
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(itemComGlass, meta, resourceLocation);
    }
}
