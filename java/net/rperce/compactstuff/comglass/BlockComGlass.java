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
        //this.setCreativeTab(CreativeTabs.tabBlock);
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
    public boolean isFullCube() {
        return false;
    }

    @Override
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
        if (adjacencies.containsKey(side)) {
            return world.getBlockState(pos).getBlock() != StartupCommon.blockComGlass;
        }
        return super.shouldSideBeRendered(world, pos, side);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        for (EnumFacing dir : adjacencies.keySet()) {
            BlockPos pos1 = pos.offset(dir);
            boolean b = world.getBlockState(pos1).getBlock() == StartupCommon.blockComGlass;
            state = state.withProperty(adjacencies.get(dir), b);
        }
        return state;
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, adjacencies.values().toArray(new IProperty[adjacencies.values().size()]));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    public static HashMap<EnumFacing, IProperty<Boolean>> adjacencies;
    static {
        adjacencies = new HashMap<>();
        EnumFacing[] dirs = EnumFacing.values();
        //EnumFacing[] dirs = new EnumFacing[] { EnumFacing.DOWN, EnumFacing.UP };
        for (EnumFacing dir : dirs) {
            adjacencies.put(dir, PropertyBool.create(dir.getName()));
        }
    }
}
