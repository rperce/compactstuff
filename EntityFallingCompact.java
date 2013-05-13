package mods.CompactStuff;

import java.util.ArrayList;
import java.util.Iterator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFallingCompact extends Entity {
	public int blockID,
    	metadata;

    public int fallTime;
    public boolean shouldDropItem;
    private int field_82156_g;
    private float field_82158_h;

    public EntityFallingCompact(World world) {
    	super(world);
    	this.fallTime=0;
    	this.shouldDropItem=true;
    	this.field_82156_g = 40;
    	this.field_82158_h = 2.0F;
    }
    public EntityFallingCompact(World par1World, double x, double y, double z, int id, int meta) {
        super(par1World);
        this.fallTime = 0;
        this.shouldDropItem = true;
        this.field_82156_g = 40;
        this.field_82158_h = 2.0F;
        this.blockID = id;
        this.metadata = meta;
        this.setAir(meta);
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.98F);
        this.yOffset = this.height / 2.0F;
        this.setPosition(x, y, z);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }
    public int getMetadata() {
    	return this.metadata;
    }
    @Override public int getPortalCooldown() {
    	return this.metadata;
    }
    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
    
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        ++this.fallTime;
        this.motionY -= 0.043999999910593033D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if (!this.worldObj.isRemote) {
            int var1 = MathHelper.floor_double(this.posX);
            int var2 = MathHelper.floor_double(this.posY);
            int var3 = MathHelper.floor_double(this.posZ);

            if (this.fallTime == 1) {
                if (this.fallTime != 1 || this.worldObj.getBlockId(var1, var2, var3) != this.blockID) {
                    this.setDead();
                    return;
                }

                this.worldObj.setBlockToAir(var1, var2, var3);
            }

            if (this.onGround) {
                this.motionX *= 0.699999988079071D;
                this.motionZ *= 0.699999988079071D;
                this.motionY *= -0.5D;

                if (this.worldObj.getBlockId(var1, var2, var3) != Block.pistonMoving.blockID) {
                    this.setDead();

                    if(this.worldObj.canPlaceEntityOnSide(this.blockID, var1, var2, var3, true, 1, (Entity)null, (ItemStack)null) &&
                    		!BlockCompressed.canFallBelow(this.worldObj, var1, var2 - 1, var3) &&
                    		this.worldObj.setBlock(var1, var2, var3, this.blockID, this.metadata, 3));
                    else if (this.shouldDropItem) {
                        this.entityDropItem(new ItemStack(this.blockID, 1, Block.blocksList[this.blockID].damageDropped(this.metadata)), 0.0F);
                    }
                }
            }
            else if (this.fallTime > 100 && !this.worldObj.isRemote && (var2 < 1 || var2 > 256) || this.fallTime > 600) {
                if (this.shouldDropItem)
                	this.entityDropItem(new ItemStack(this.blockID, 1, Block.blocksList[this.blockID].damageDropped(this.metadata)), 0.0F);

                this.setDead();
            }
        }
    }

    @Override protected void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setByte("Tile", (byte)this.blockID);
        nbt.setByte("Data", (byte)this.metadata);
        nbt.setByte("Time", (byte)this.fallTime);
    }

    @Override protected void readEntityFromNBT(NBTTagCompound nbt) {
        this.blockID = nbt.getByte("Tile") & 255;
        this.metadata = nbt.getByte("Data") & 255;
        this.fallTime = nbt.getByte("Time") & 255;

        if (this.blockID == 0)
            this.blockID = CompactStuff.comBlock.blockID;
    }
    @Override protected boolean canTriggerWalking() { return false; }
    @Override protected void entityInit() {}
    @Override public boolean canBeCollidedWith() { return !isDead; }
    @SideOnly(Side.CLIENT) public float getShadowSize() { return 0.0F; }
    @SideOnly(Side.CLIENT) public World getWorld() { return this.worldObj; }
    @SideOnly(Side.CLIENT) public boolean canRenderOnFire() { return false;}
}
