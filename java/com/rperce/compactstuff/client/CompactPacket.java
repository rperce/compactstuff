package com.rperce.compactstuff.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.PacketDispatcher;

public class CompactPacket {
	private Packet250CustomPayload packet=null;
	private ByteArrayOutputStream bos=null;
	
	public CompactPacket(String channel) {
		this.packet = new Packet250CustomPayload();
		this.packet.channel = channel;
	}
	public void writeInts(int... ints) {
		this.bos = new ByteArrayOutputStream(ints.length*4);
		DataOutputStream dos = new DataOutputStream(this.bos);
		for(int i : ints) {
			try {
				dos.writeInt(i);
			} catch(Exception e) { e.printStackTrace(); }
		}
	}
	
	public void send() {
		if(this.bos==null) {
			System.out.println("Nothing to send in CompactPacket!");
			return;
		}
		this.packet.data = this.bos.toByteArray();
		this.packet.length = this.bos.size();
		PacketDispatcher.sendPacketToServer(this.packet);
	}

}
