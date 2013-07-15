package mods.CompactStuff;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import mods.CompactStuff.compactor.TileEntityCompactor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {
	@Override public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(packet.data));
		if(packet.channel.equals("compactorClick")) {
			int[] is = new int[5];
			try {
				for(int i=0; i<is.length; i++) {
					is[i] = in.readInt();
				}
			} catch(Exception e) { e.printStackTrace(); }
			TileEntity te = ((EntityPlayer)player).worldObj.getBlockTileEntity(is[0], is[1], is[2]);
			if(te==null || !(te instanceof TileEntityCompactor)) {
				System.out.printf("No Compactor at %d, %d, %d where there should have been%n",is[0],is[1],is[2]);
				return;
			}

			if(is[4]==0) {
				ItemStack slotStack = null;
				ItemStack held = ((EntityPlayer)player).inventory.getItemStack();
				if(held!=null) slotStack = held.copy();
				if(slotStack!=null) slotStack.stackSize = 1;
				((TileEntityCompactor)te).setInventorySlotContents(is[3], slotStack);
			} else if(is[4]==1) {
				((TileEntityCompactor)te).setInventorySlotContents(is[3], null);
			}
		}
	}

}
