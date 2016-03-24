package net.rperce.compactstuff;

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
    public static final int GUI_ID_COMPACTOR = 1;
    public static int channelID = 0;
    public static int nextChannelID() { return channelID++; }

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
