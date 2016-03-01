package net.rperce.compactstuff.comglass;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.rperce.compactstuff.CompactStuff;

import java.io.IOException;

/**
 * Created by Robert on 2/27/2016.
 */
public class ModelLoaderComGlass implements ICustomModelLoader {
    public static final String SMART_MODEL_RESOURCE_LOC = "models/block/smartmodel/";

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws IOException {
        String resourcePath = modelLocation.getResourcePath();
        if (!resourcePath.startsWith(SMART_MODEL_RESOURCE_LOC)) {
            throw new IllegalArgumentException("loadModel expected " + SMART_MODEL_RESOURCE_LOC + " but received " + resourcePath);
        }
        String modelName = resourcePath.substring(SMART_MODEL_RESOURCE_LOC.length());

        if (modelName.equals("comglass_model")) {
            return new ComGlassModel();
        } else {
            return ModelLoaderRegistry.getMissingModel();
        }
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return modelLocation.getResourceDomain().equals(CompactStuff.MODID)
                &&  modelLocation.getResourcePath().startsWith(SMART_MODEL_RESOURCE_LOC);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }
    private IResourceManager resourceManager;
}
