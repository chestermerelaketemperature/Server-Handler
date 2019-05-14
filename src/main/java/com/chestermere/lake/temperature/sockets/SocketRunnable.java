package com.chestermere.lake.temperature.sockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.apache.commons.configuration2.Configuration;

import com.chestermere.lake.temperature.Server;
import com.chestermere.lake.temperature.utils.Encryption;
import com.google.common.flogger.FluentLogger;

public class SocketRunnable implements Runnable {

	private final Configuration configuration;
	private final Encryption encryption;
	private final FluentLogger logger;
	private final Socket socket;

	public SocketRunnable(Server instance, Socket socket) {
		this.configuration = instance.getConfiguration();
		this.encryption = instance.getEncryption();
		this.logger = instance.getLogger();
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			String algorithm = configuration.getString("cipherAlgorithm", "AES/CBC/PKCS5Padding");
			String keyString = configuration.getString("cipherKey", "insert 16 length");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
			Object object = objectInputStream.readObject();
			if (object != null) {
				@SuppressWarnings("unused") //For now
				Object packet = null; //TODO probably make this byte[] later?
				try {
					packet = encryption.decrypt(keyString, algorithm, (byte[]) object);
				} catch (ClassCastException e) {
					logger.atWarning()
							.withCause(e)
							.log("Could not decrypt an incoming packet");
					objectInputStream.close();
					objectOutputStream.close();
					return;
				}
//				if (packet.getPassword() != null) {
//					byte[] password = encryption.hash();
//					if (!Arrays.equals(password, packet.getPassword())) {
//						objectInputStream.close();
//						objectOutputStream.close();
//						return;
//					}
//				}
//				Optional<PacketHandler> handler = PacketHandler.getHandler(packet.getType());
//				Object packetData = null;
//				if (handler.isPresent())
//					packetData = handler.get().callPacket(packet, socket.getInetAddress());
//				if (packetData != null && packet.isReturnable()) {
//					byte[] serialized = encryption.serialize(packetData);
//					byte[] encrypted = encryption.encrypt(keyString, algorithm, serialized);
//					objectOutputStream.writeObject(encrypted);
//				}
			}
			objectInputStream.close();
			objectOutputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			logger.atWarning()
					.withCause(e)
					.log("Could not decrypt an incoming packet");
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
