package com.chestermere.lake.temperature.sockets;

import java.net.InetAddress;

import com.chestermere.lake.temperature.Server;
import com.chestermere.lake.temperature.objects.Packet;

public abstract class Handler {

	protected final Server instance;
	protected String[] names;

	public Handler(Server instance, String... names) {
		this.instance = instance;
		this.names = names;
	}

	public String[] getNames() {
		return names;
	}

	/**
	 * Called when the packet handler is requested.
	 * @param packet The incoming packet to handle.
	 * @param address The address the packet came from.
	 * @return boolean If the packet should be accepted or not.
	 */
	protected abstract boolean onPacketCall(Packet packet, InetAddress address);

	/**
	 * The main packet handler. This is what is defined to happen when the packet comes in.
	 * @param packet The incoming packet to handle.
	 * @param address The address the packet came from.
	 * @return The value to be send back to the sender if it's a returnable packet.
	 */
	protected abstract Object handlePacket(Packet packet, InetAddress address);

	public Object callPacket(Packet packet, InetAddress address) {
		if (instance.debug())
			instance.getLogger().atInfo().log("Recieved packet %s", packet.getName());
		if (!onPacketCall(packet, address))
			return null;
		return handlePacket(packet, address);
	}

}
