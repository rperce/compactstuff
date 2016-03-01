package net.rperce.compactstuff.comglass;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;

import java.util.HashMap;

/**
 * Created by Robert on 2/26/2016.
 */
public class BlockComGlass extends Block {
    private boolean fancy;
    public static String canonicalName = "comglass";

    public BlockComGlass(boolean fancy) {
        super(Material.glass);
        this.setStepSound(Block.soundTypeGlass);
        this.setHardness(0.7f);
        this.setResistance(21f);
        this.setLightOpacity(0);
        this.fancy = fancy;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT_MIPPED;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return super.getActualState(state, worldIn, pos);
    }

    @Override
    protected BlockState createBlockState() {
        IProperty[] listedProperties = new IProperty[0];
        IUnlistedProperty<Boolean>[] unlistedProperties = new IUnlistedProperty[] { ABOVE, BELOW, NORTH, SOUTH, EAST, WEST };
        return new ExtendedBlockState(this, listedProperties, unlistedProperties);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState ret = (IExtendedBlockState)state;
            for (EnumFacing dir : EnumFacing.values()) {
                BlockPos pos1 = pos.offset(dir);
                boolean b = world.getBlockState(pos1).getBlock() == StartupCommon.blockComGlass;
                ret = ret.withProperty(ulprops.get(dir), b);
            }
            return ret;
        }
        return state;
    }

    public static HashMap<EnumFacing, IUnlistedProperty<Boolean>> ulprops;
    static {
        for (EnumFacing dir : EnumFacing.values()) {
            ulprops.put(dir, new Properties.PropertyAdapter<>(PropertyBool.create("block_" + dir.getName())));
        }
    }
}
