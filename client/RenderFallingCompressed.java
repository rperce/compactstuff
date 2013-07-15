package mods.CompactStuff.client;

import mods.CompactStuff.CompactStuff;
import mods.CompactStuff.EntityFallingCompact;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderFallingSand;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFallingCompressed extends RenderFallingSand {
	private RenderBlocks renderBlocks = new RenderBlocks();

	public RenderFallingCompressed() {
		this.shadowSize = 0.5F;
	}

	/**
	* The actual render method that is used in doRender
	*/
	public void doRenderFalling(EntityFallingCompact entityFalling, double x, double y, double z, float useless, float floats) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        Block block = CompactStuff.comBlock;
        World world = entityFalling.getWorld();
        this.loadTexture("/terrain.png");
        GL11.glDisable(GL11.GL_LIGHTING);
        
        int xx = MathHelper.floor_double(entityFalling.posX),
        	yy = MathHelper.floor_double(entityFalling.posY),
        	zz = MathHelper.floor_double(entityFalling.posZ),
        	meta=entityFalling.getAir(); //totally where I'm storing metadata.

        if (block != null) {
            this.renderBlocks.setRenderBoundsFromBlock(block);
            this.renderBlocks.renderBlockSandFalling(block, world, xx, yy, zz, meta);
        }

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    @Override public void doRender(Entity entity, double x, double y, double z, float par8, float par9) {
        this.doRenderFalling((EntityFallingCompact)entity, x, y, z, par8, par9);
    }
}
