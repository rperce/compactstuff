package net.rperce.compactstuff.blockcompact;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class BlockCompactSquishy extends Block {
    public static final String canonicalName = "blockcompact_squish";
    public BlockCompactSquishy() {
        super(Material.ground);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setStepSound(SoundType.GROUND);
        this.setHarvestLevel("shovel", 1);
    }
    public static final PropertyEnum<Meta> PROPERTY_NAME = PropertyEnum.create("name", Meta.class);

    public static ItemStack stack(Meta m) { return stack(1, m); }
    public static ItemStack stack(int amt, Meta m) { return new ItemStack(StartupCommon.compactBlockSquishy, amt, m.id);}

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PROPERTY_NAME);
    }

    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        return 4f;
    }

    @Override
    public int damageDropped(IBlockState state) {
        Meta meta = state.getValue(PROPERTY_NAME);
        return meta.id;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (Meta m : Meta.values()) {
            list.add(new ItemStack(itemIn, 1, m.id));
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(PROPERTY_NAME, Meta.fromID(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        Meta meta = state.getValue(PROPERTY_NAME);
        return meta.id;
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(PROPERTY_NAME, Meta.fromID(meta));
    }

    public enum Meta implements IStringSerializable {
        COMDIRT(0),
        COMSAND(1),
        COMGRAVEL(2);

        public final int id;
        Meta(int id) {
            this.id = id;
        }

        @Override
        public String getName() {
            return this.toString();
        }

        public static Meta fromID(int id) {
            if (id < 0 || id > Meta.values().length) {
                id = 0;
            }
            return Meta.values()[id];
        }

        public static Stream<String> getNames() {
            return Arrays.stream(Meta.values()).map(Meta::toString);
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }
}
