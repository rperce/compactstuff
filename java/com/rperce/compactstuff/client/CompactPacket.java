package com.rperce.compactstuff.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import cpw.mods.fml.common.network.PacketDispatcher;

import net.minecraft.network.packet.Packet250CustomPayload;

public class CompactPacket {
	private Packet250CustomPayload packet=null;
	private ByteArrayOutputStream bos=null;
	
	public CompactPacket(String channel) {
		packet = new Packet250CustomPayload();
		packet.channel = channel;
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
		if(bos==null) {
			System.out.println("Nothing to send in CompactPacket!");
			return;
		}
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		PacketDispatcher.sendPacketToServer(packet);
	}

}
