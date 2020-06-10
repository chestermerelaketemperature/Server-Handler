package com.chestermere.lake.temperature;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.annotation.Nullable;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import com.chestermere.lake.temperature.managers.SnapshotManager;
import com.chestermere.lake.temperature.utils.Encryption;
import com.google.common.flogger.FluentLogger;
import com.sitrica.japson.gson.JsonObject;
import com.sitrica.japson.server.JapsonServer;
import com.sitrica.japson.shared.Handler;

public class Server {

	private static final FluentLogger logger = FluentLogger.forEnclosingClass();
	private final SnapshotManager snapshots;
	private final Encryption encryption;
	private Configuration configuration;
	private JapsonServer japson;

	Server() {
		Configurations configurations = new Configurations();
		try {
			configuration = configurations.properties(new File("configuration.properties"));
		}
		catch (ConfigurationException exception) {
			exception.printStackTrace();
		}
		this.encryption = new Encryption(this);
		this.snapshots = new SnapshotManager(this);
		try {
			japson = new JapsonServer(1337);
			japson.registerHandlers(new Handler(0x01) {
				@Override
				public String handle(InetAddress address, int port, JsonObject object) {
					if (!object.has("temperature"))
						return null;
					float temperature = object.get("temperature").getAsFloat();
					//TODO handle air temperature into SnapshotManager.
					// Use a public REST API for now.
					snapshots.addSnapshot(false, temperature, 0);
					return null;
				}
			});
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
		}
	}

	@Nullable
	public Configuration getConfiguration() {
		return configuration;
	}

	public SnapshotManager getSnapshots() {
		return snapshots;
	}

	public Encryption getEncryption() {
		return encryption;
	}

	public FluentLogger getLogger() {
		return logger;
	}

	public File getDataFolder() {
		try {
			return new File(".").getCanonicalFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean debug() {
		return configuration.getBoolean("debug", false);
	}

}
