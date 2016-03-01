package net.rperce.compactstuff.blockcompact;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Robert on 2/26/2016.
 */
public class BlockCompact extends Block {
    public static String canonicalName = "blockcompact";

    public BlockCompact(Material m) {
        super(m);
    }
    public BlockCompact() {
        this(Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setStepSound(Block.soundTypeStone);
        this.setHarvestLevel("pickaxe", 2);
    }

    public static ItemStack stack(Meta m) { return stack(1, m); }
    public static ItemStack stack(int amt, Meta m) { return new ItemStack(StartupCommon.compactBlock, amt, m.id);}

    public static final PropertyEnum PROPERTY_NAME = PropertyEnum.create("name", Meta.class);

    @Override
    public int damageDropped(IBlockState state) {
        Meta meta = (Meta) state.getValue(PROPERTY_NAME);
        return meta.id;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
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
        Meta meta = (Meta) state.getValue(PROPERTY_NAME);
        return meta.id;
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, PROPERTY_NAME);
    }

    @Override
    public IBlockState onBlockPlaced(World w, BlockPos p, EnumFacing click, float x, float y, float z, int meta, EntityLivingBase player) {
        return this.getDefaultState().withProperty(PROPERTY_NAME, Meta.fromID(meta));
    }

    @Override
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
        if ((Meta)world.getBlockState(pos).getValue(PROPERTY_NAME) == Meta.COMNETHER) {
            return true;
        }
        return super.isFireSource(world, pos, side);
    }

    @Override
    public float getBlockHardness(World world, BlockPos pos) {
        return ((Meta)world.getBlockState(pos).getValue(PROPERTY_NAME)).hardness;
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        return getBlockHardness(world, pos);
    }

    public enum Meta implements IStringSerializable {
        COMCOBBLE(0,  5),
        IRONSTONE(1,  20),
        COMNETHER(2,  1),
        COMDIAMOND(3, 14),
        COMIRON(4,    14),
        COMGOLD(5,    10),
        STEELBLOCK(6, 5),
        COMSTEEL(7,   14);


        public int id, hardness;

        private Meta(int id, int hardness) {
            this.hardness = hardness;
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
