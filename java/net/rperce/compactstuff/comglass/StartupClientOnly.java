package net.rperce.compactstuff.comglass;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.rperce.compactstuff.BlankStartup;
import net.rperce.compactstuff.CompactStuff;
import net.rperce.compactstuff.Utilities;

/**
 * Created by Robert on 2/27/2016.
 */
public class StartupClientOnly extends BlankStartup {

    public static void init() {
        Item itemComGlass = GameRegistry.findItem(CompactStuff.MODID, BlockComGlass.canonicalName);
        ModelResourceLocation resourceLocation = new ModelResourceLocation(Utilities.colonize(
                CompactStuff.MODID, BlockComGlass.canonicalName), "inventory");
        int meta = 0;
        ModelLoader.setCustomModelResourceLocation(itemComGlass, meta, resourceLocation);
    }
}
