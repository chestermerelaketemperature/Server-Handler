package com.chestermere.lake.temperature.sockets;

import java.io.IOException;
import java.net.ServerSocket;

import javax.annotation.Nullable;

import com.chestermere.lake.temperature.Server;
import com.google.common.flogger.FluentLogger;

public class SocketListener {

	private final FluentLogger logger;
	private ServerSocket serverSocket;

	public SocketListener(Server instance) {
		this.logger = instance.getLogger();
		int port = instance.getConfiguration().getInt("port", 1337);
		try {
			this.serverSocket = new ServerSocket(port, 69);
		} catch (IOException cause) {
			logger.atWarning()
					.withCause(cause)
					.log("Socket couldn't be setup on port %s", port);
			return;
		}
		logger.atInfo().log("connection established on port %s", port);
		new Thread(() -> {
			while (!serverSocket.isClosed()) {
				try {
					new Thread(new SocketRunnable(instance, serverSocket.accept())).start();
				} catch (IOException e) {
					logger.atWarning()
							.withCause(e)
							.log("Socket couldn't accept incoming data.");
				}
			}
		}).start();
	}

	@Nullable
	public ServerSocket getServerSocket() {
		return serverSocket;
	}

}
