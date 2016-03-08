package net.rperce.compactstuff.compactor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;
import net.rperce.compactstuff.CompactStuff;

import java.util.stream.IntStream;

public class BlockCompactor extends BlockContainer {
    public static final String canonicalName = "compactor";

    public BlockCompactor() {
        super(Material.iron);
        this.setStepSound(Block.soundTypeMetal);
        this.setHardness(4f);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCompactor();
    }

    @Override
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.SOLID;
    }

    @Override
    public int getRenderType() {
        return 3;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        playerIn.openGui(CompactStuff.instance, CompactStuff.GUI_ID_COMPACTOR, world,
                pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntityCompactor te = (TileEntityCompactor)world.getTileEntity(pos);
        if (te != null) {
            IntStream.rangeClosed(TileEntityCompactor.INV_FIRST, TileEntityCompactor.CRAFT_LAST).forEach(slot -> {
                ItemStack stack = te.getStackInSlot(slot);
                if (stack != null) {
                    EntityItem item = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5,
                            pos.getZ() + 0.5, stack);
                    float mx = (world.rand.nextFloat() - 0.5f) * 0.1f;
                    float my = (world.rand.nextFloat() - 0.5f) * 0.1f;
                    float mz = (world.rand.nextFloat() - 0.5f) * 0.1f;
                    item.motionX = mx;
                    item.motionY = my;
                    item.motionZ = mz;
                    world.spawnEntityInWorld(item);
                }
            });
            te.clear();
        }
        super.breakBlock(world, pos, state);
    }
}
