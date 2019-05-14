package com.chestermere.lake.temperature.handlers;

import java.net.InetAddress;

import com.chestermere.lake.temperature.Server;
import com.chestermere.lake.temperature.objects.Packet;
import com.chestermere.lake.temperature.sockets.Handler;

public class SnapshotHandler extends Handler {

	public SnapshotHandler(Server instance) {
		super(instance, "snapshot", "collection", "collect");
	}

	@Override
	protected boolean onPacketCall(Packet packet, InetAddress address) {
		return true;
	}

	@Override
	protected Object handlePacket(Packet packet, InetAddress address) {
		return null; //TODO handle incoming snapshots from the Relay. Handle it all in SnapshotManager.class
	}

}
