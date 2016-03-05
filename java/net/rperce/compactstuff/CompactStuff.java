package net.rperce.compactstuff;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = CompactStuff.MODID, version = CompactStuff.VERSION)
public class CompactStuff {
    public static final String MODID = "compactstuff";
    public static final String VERSION = "0.0.1";

    @Mod.Instance(CompactStuff.MODID)
    public static CompactStuff instance;

    @SidedProxy(clientSide = "net.rperce.compactstuff.ClientOnlyProxy", serverSide = "net.rperce.compactstuff.DedicatedServerProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }
}