package net.rperce.compactstuff;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import java.util.stream.Stream;

abstract class CommonProxy {
    Stream<BaseStartup> toInit() {
        Stream.Builder<BaseStartup> out = Stream.builder();
        out.add(new net.rperce.compactstuff.blockcompact.StartupCommon());
        out.add(new net.rperce.compactstuff.comglass.StartupCommon());
        out.add(new net.rperce.compactstuff.compactor.StartupCommon());
        return out.build();
    }
    public void preInit() {
        NetworkRegistry.INSTANCE.registerGuiHandler(CompactStuff.instance, GuiHandlerRegistry.getInstance());
        toInit().forEach(BaseStartup::preInit);
    }

    public void init() {
        toInit().forEach(BaseStartup::init);
    }
    public void postInit() {
        toInit().forEach(BaseStartup::postInit);
    }

}
