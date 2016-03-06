package net.rperce.compactstuff.compactor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.rperce.compactstuff.BlankStartup;
import net.rperce.compactstuff.CompactStuff;
import net.rperce.compactstuff.Utilities;

/**
 * Created by robert on 3/5/16.
 */
public class StartupClientOnly extends BlankStartup {
    public static void init() {
        Item itemComGlass = GameRegistry.findItem(CompactStuff.MODID, BlockCompactor.canonicalName);
        ModelResourceLocation resourceLocation = new ModelResourceLocation(Utilities.colonize(
                CompactStuff.MODID, BlockCompactor.canonicalName), "inventory");
        int meta = 0;
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(itemComGlass, meta, resourceLocation);
    }
}
