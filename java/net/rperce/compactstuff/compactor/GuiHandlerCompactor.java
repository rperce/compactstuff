package net.rperce.compactstuff.compactor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.rperce.compactstuff.CompactStuff;
import net.rperce.compactstuff.Utilities;

class GuiHandlerCompactor implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        Utilities.checkID(ID, CompactStuff.GUI_ID_COMPACTOR, "Compactor");
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCompactor) {
            return new ContainerCompactor(player.inventory, (TileEntityCompactor)te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        Utilities.checkID(ID, CompactStuff.GUI_ID_COMPACTOR, "Compactor");
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCompactor) {
            return new GuiCompactor(player.inventory, (TileEntityCompactor)te);
        }
        return null;
    }
}
