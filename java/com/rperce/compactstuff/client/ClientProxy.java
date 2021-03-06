package com.rperce.compactstuff.client;

import com.rperce.compactstuff.CommonProxy;

import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

    /*
     * Override public void registerRenderers() { for(ImageFiles i :
     * ImageFiles.values()) { MinecraftForgeClient.preloadTexture(i.path); }
     * RenderingRegistry
     * .registerEntityRenderingHandler(EntityFallingCompact.class, new
     * RenderFallingCompressed()); }
     */

    @Override
    public int addArmor(String armor) {
        return RenderingRegistry.addNewArmourRendererPrefix(armor);
    }
}