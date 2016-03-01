package net.rperce.compactstuff.blockcompact;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.rperce.compactstuff.BlankStartup;
import net.rperce.compactstuff.CompactStuff;
import net.rperce.compactstuff.Utilities;

/**
 * Created by Robert on 2/26/2016.
 */
public class StartupClientOnly extends BlankStartup {
    public static void preInit() {
        Item itemCompactBlock = GameRegistry.findItem(CompactStuff.MODID, BlockCompact.canonicalName);
        ResourceLocation[] resources = BlockCompact.Meta.getNames()
                .map(name -> {
                    return new ModelResourceLocation(Utilities.colonVariant(CompactStuff.MODID,
                            BlockCompact.canonicalName, name), "inventory");
                })
                .toArray(ModelResourceLocation[]::new);
        ModelBakery.registerItemVariants(itemCompactBlock, resources);

        Item itemCompactSquishBlock = GameRegistry.findItem(CompactStuff.MODID, BlockCompactSquishy.canonicalName);
        resources = BlockCompactSquishy.Meta.getNames()
                .map(name -> {
                    return new ModelResourceLocation(Utilities.colonVariant(CompactStuff.MODID,
                            BlockCompact.canonicalName, name), "inventory"); // yes, we're using BlockCompact on purpose
                })
                .toArray(ModelResourceLocation[]::new);
        ModelBakery.registerItemVariants(itemCompactSquishBlock, resources);
    }
    public static void init() {
        Item itemCompactBlock = GameRegistry.findItem(CompactStuff.MODID, BlockCompact.canonicalName);
        for (BlockCompact.Meta m : BlockCompact.Meta.values()) {
            String name = m.getName();
            ModelResourceLocation itemModelResourceLocation =
                    new ModelResourceLocation(Utilities.colonVariant(CompactStuff.MODID,
                            BlockCompact.canonicalName, name), "inventory");
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(itemCompactBlock,
                    m.id, itemModelResourceLocation);
        }

        itemCompactBlock = GameRegistry.findItem(CompactStuff.MODID, BlockCompactSquishy.canonicalName);
        for (BlockCompactSquishy.Meta m : BlockCompactSquishy.Meta.values()) {
            String name = m.getName();
            ModelResourceLocation itemModelResourceLocation =
                    new ModelResourceLocation(Utilities.colonVariant(CompactStuff.MODID,
                            BlockCompact.canonicalName, name), "inventory");
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(itemCompactBlock,
                    m.id, itemModelResourceLocation);
        }
    }
}
