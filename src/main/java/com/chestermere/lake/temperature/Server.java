package com.chestermere.lake.temperature;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.annotation.Nullable;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import com.chestermere.lake.temperature.database.Database;
import com.chestermere.lake.temperature.database.H2Database;
import com.chestermere.lake.temperature.managers.HandlerManager;
import com.chestermere.lake.temperature.managers.SnapshotManager;
import com.chestermere.lake.temperature.objects.DaySnapshot;
import com.chestermere.lake.temperature.sockets.SocketListener;
import com.chestermere.lake.temperature.utils.Encryption;
import com.google.common.flogger.FluentLogger;

public class Server {

	private static final FluentLogger logger = FluentLogger.forEnclosingClass();
	private final SocketListener socketListener;
	private final HandlerManager handlerManager;
	private final SnapshotManager snapshots;
	private Database<DaySnapshot> database;
	private final Encryption encryption;
	private Configuration configuration;

	Server() {
		Configurations configurations = new Configurations();
		try {
			configuration = configurations.properties(new File("configuration.properties"));
		}
		catch (ConfigurationException exception) {
			exception.printStackTrace();
		}
		try {
			this.database = new H2Database<DaySnapshot>(this, "day-snapshots", DaySnapshot.class);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		this.encryption = new Encryption(this);
		this.socketListener = new SocketListener(this);
		this.handlerManager = new HandlerManager(this);
		this.snapshots = new SnapshotManager(this);
	}

	public HandlerManager getHandlerManager() {
		return handlerManager;
	}

	public Database<DaySnapshot> getDatabase() {
		return database;
	}

	public SocketListener getSocketListener() {
		return socketListener;
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
