package net.rperce.compactstuff.blockcompact;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.rperce.compactstuff.BaseStartup;
import net.rperce.compactstuff.CompactStuff;
import net.rperce.compactstuff.Utilities;

public class StartupClientOnly extends BaseStartup {
    @Override
    public void preInit() {
        Item itemCompactBlock = GameRegistry.findItem(CompactStuff.MODID, BlockCompact.canonicalName);
        ResourceLocation[] resources = BlockCompact.Meta.getNames()
                .map(name ->
                    new ModelResourceLocation(Utilities.colonVariant(CompactStuff.MODID,
                            BlockCompact.canonicalName, name), "inventory")
                )
                .toArray(ModelResourceLocation[]::new);
        ModelBakery.registerItemVariants(itemCompactBlock, resources);

        Item itemCompactSquishBlock = GameRegistry.findItem(CompactStuff.MODID, BlockCompactSquishy.canonicalName);
        resources = BlockCompactSquishy.Meta.getNames()
                .map(name ->
                    new ModelResourceLocation(Utilities.colonVariant(CompactStuff.MODID,
                            BlockCompact.canonicalName, name), "inventory") // yes, we're using BlockCompact on purpose
                )
                .toArray(ModelResourceLocation[]::new);
        ModelBakery.registerItemVariants(itemCompactSquishBlock, resources);
    }

    @Override
    public void init() {
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
