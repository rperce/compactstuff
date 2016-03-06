package net.rperce.compactstuff;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.rperce.compactstuff.compactor.CompactorRecipes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Robert on 2/26/2016.
 */
public abstract class CommonProxy {
    String[] wantsInit = new String[] { "blockcompact", "comglass", "compactor" };
    public void preInit() {
        NetworkRegistry.INSTANCE.registerGuiHandler(CompactStuff.instance, GuiHandlerRegistry.getInstance());
        doStartupFor(wantsInit, "StartupCommon", "preInit");
    }
    public void init() {
        doStartupFor(wantsInit, "StartupCommon", "init");
    }
    public void postInit() {
        doStartupFor(wantsInit, "StartupCommon", "postInit");
    }

    protected void doStartupFor(String[] classes, String startupName, String initStage) {
        for (String s : classes) {
            try {
                Class<?> cls = Class.forName(String.format("%s.%s", Utilities.enpackage(s), startupName));
                try {
                    Method initMethod = cls.getMethod(initStage, null);
                    initMethod.invoke(null, null);
                } catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } catch(ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }
        }
    }
}
