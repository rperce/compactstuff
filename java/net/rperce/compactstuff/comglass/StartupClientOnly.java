package net.rperce.compactstuff.comglass;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.rperce.compactstuff.BlankStartup;
import net.rperce.compactstuff.CompactStuff;
import net.rperce.compactstuff.Utilities;

/**
 * Created by Robert on 2/27/2016.
 */
public class StartupClientOnly extends BlankStartup {
    public static void preInit() {
        StateMapperBase ignoreState = new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(Utilities.colonize(CompactStuff.MODID, BlockComGlass.canonicalName));
            }
        };
        ModelLoader.setCustomStateMapper(StartupCommon.blockComGlass, ignoreState);
        ModelLoaderRegistry.registerLoader(new ModelLoaderComGlass());
    }
}
