package mods.CompactStuff.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import cpw.mods.fml.common.network.PacketDispatcher;

import net.minecraft.network.packet.Packet250CustomPayload;

public class CompactPacket extends Packet250CustomPayload {
	private ByteArrayOutputStream bos;
	
	public CompactPacket(String channel) {
		this.channel = channel;
	}
	public void writeInts(int... ints) {
		bos = new ByteArrayOutputStream(ints.length*4);
		DataOutputStream dos = new DataOutputStream(bos);
		for(int i : ints) {
			try {
				dos.writeInt(i);
			} catch(Exception e) { e.printStackTrace(); }
		}
	}
	
	public void send() {
		this.data = bos.toByteArray();
		this.length = bos.size();
		PacketDispatcher.sendPacketToServer(this);
	}

}
