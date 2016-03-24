package net.rperce.compactstuff;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.util.stream.Stream;

public abstract class CommonProxy {
    Stream<BaseStartup> toInit() {
        Stream.Builder<BaseStartup> out = Stream.builder();
        out.add(new net.rperce.compactstuff.blockcompact.StartupCommon());
        out.add(new net.rperce.compactstuff.comglass.StartupCommon());
        out.add(new net.rperce.compactstuff.compactor.StartupCommon());
        return out.build();
    }
    public static SimpleNetworkWrapper networkWrapper;
    public void preInit() {
        NetworkRegistry.INSTANCE.registerGuiHandler(CompactStuff.instance, GuiHandlerRegistry.getInstance());
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(CompactStuff.MODID);
        toInit().forEach(BaseStartup::preInit);
    }

    public void init() {
        toInit().forEach(BaseStartup::init);
    }
    public void postInit() {
        toInit().forEach(BaseStartup::postInit);
    }

}
