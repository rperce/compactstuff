package net.rperce.compactstuff.blockcompact;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
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
        this.setStepSound(Block.soundTypeGravel);
        this.setHarvestLevel("shovel", 1);
    }
    public static final PropertyEnum PROPERTY_NAME = PropertyEnum.create("name", Meta.class);

    public static ItemStack stack(Meta m) { return stack(1, m); }
    public static ItemStack stack(int amt, Meta m) { return new ItemStack(StartupCommon.compactBlockSquishy, amt, m.id);}

    @Override
    public float getBlockHardness(World world, BlockPos pos) {
        return 4f;
    }

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
    public enum Meta implements IStringSerializable {
        COMDIRT(0),
        COMSAND(1),
        COMGRAVEL(2);

        public int id;
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
