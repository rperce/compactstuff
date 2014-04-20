package com.rperce.compactstuff;

import com.rperce.compactstuff.boh.BagOfHoldingGUI;
import com.rperce.compactstuff.boh.ContainerBagOfHolding;
import com.rperce.compactstuff.boh.ItemBagOfHolding;
import com.rperce.compactstuff.compactor.CompactorGUI;
import com.rperce.compactstuff.compactor.ContainerCompactor;
import com.rperce.compactstuff.compactor.TileEntityCompactor;
import com.rperce.compactstuff.furnace.BlazeFurnaceGUI;
import com.rperce.compactstuff.furnace.CompactFurnaceGUI;
import com.rperce.compactstuff.furnace.ContainerBlazeFurnace;
import com.rperce.compactstuff.furnace.ContainerCompactFurnace;
import com.rperce.compactstuff.furnace.TileEntityBlazeFurnace;
import com.rperce.compactstuff.furnace.TileEntityCompactFurnace;
import com.rperce.compactstuff.tmog.ContainerTmog;
import com.rperce.compactstuff.tmog.TileEntityTransmog;
import com.rperce.compactstuff.tmog.TransmogGUI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler {
    // Client stuff
    public void registerRenderers() {
        // Nothing here as this is the server side proxy
    }

    /**
     * @param armor
     *            Name to add to registry client-side
     * @return
     */
    public int addArmor(String armor) {
        return 0; // server doesn't give a crap
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world,
            int x, int y, int z) {
        TileEntity te = world.getBlockTileEntity(x, y, z);
        switch (id) {
            case 0:
            case 1:
                return new ContainerCompactFurnace(player.inventory,
                        (TileEntityCompactFurnace) te);
            case 2:
                if (player.getHeldItem().getItem() instanceof ItemBagOfHolding)
                    return new ContainerBagOfHolding(player.inventory,
                            ((ItemBagOfHolding) player.getHeldItem().getItem())
                                    .getInventory(player));
            case 3:
                return new ContainerCompactor(player.inventory,
                        (TileEntityCompactor) te);
            case 4:
                return new ContainerTmog(player.inventory,
                        (TileEntityTransmog) te);
            case 5:
                return new ContainerBlazeFurnace(player.inventory,
                        (TileEntityBlazeFurnace) te);
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world,
            int x, int y, int z) {
        TileEntity te = world.getBlockTileEntity(x, y, z);
        switch (id) {
            case 0:
            case 1:
                return new CompactFurnaceGUI(player.inventory,
                        (TileEntityCompactFurnace) te);
            case 2:
                if (player.getHeldItem() != null
                        && player.getHeldItem().getItem() instanceof ItemBagOfHolding)
                    return new BagOfHoldingGUI(player.inventory,
                            ((ItemBagOfHolding) player.getHeldItem().getItem())
                                    .getInventory(player), player.getHeldItem());
            case 3:
                return new CompactorGUI(player.inventory,
                        (TileEntityCompactor) te);
            case 4:
                return new TransmogGUI(player.inventory,
                        (TileEntityTransmog) te);
            case 5:
                return new BlazeFurnaceGUI(player.inventory,
                        (TileEntityBlazeFurnace) te);
            default:
                return null;
        }
    }
}
