package net.rperce.compactstuff.comglass;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.rperce.compactstuff.BaseStartup;
import net.rperce.compactstuff.CompactStuff;
import net.rperce.compactstuff.Utilities;

public class StartupClientOnly extends BaseStartup {

    @Override
    public void init() {
        Item itemComGlass = GameRegistry.findItem(CompactStuff.MODID, BlockComGlass.canonicalName);
        ModelResourceLocation resourceLocation = new ModelResourceLocation(Utilities.colonize(
                CompactStuff.MODID, BlockComGlass.canonicalName), "inventory");
        int meta = 0;
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(itemComGlass, meta, resourceLocation);
    }
}
