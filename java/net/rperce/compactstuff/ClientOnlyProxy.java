package net.rperce.compactstuff;

/**
 * Created by Robert on 2/26/2016.
 */
public class ClientOnlyProxy extends CommonProxy {

    public void preInit() {
        super.preInit();
        doStartupFor(wantsInit, "StartupClientOnly", "preInit");
    }

    public void init() {
        super.init();
        doStartupFor(wantsInit, "StartupClientOnly", "init");
    }

    public void postInit() {
        super.postInit();
        doStartupFor(wantsInit, "StartupClientOnly", "postInit");
    }
}
