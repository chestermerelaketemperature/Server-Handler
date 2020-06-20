package com.chestermere.lake.temperature;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
import com.weatherapi.api.WeatherAPIClient;
import com.weatherapi.api.controllers.APIsController;
import com.weatherapi.api.http.client.APICallBack;
import com.weatherapi.api.http.client.HttpContext;
import com.weatherapi.api.models.Current;
import com.weatherapi.api.models.CurrentJsonResponse;

public class Server {

	private static final FluentLogger logger = FluentLogger.forEnclosingClass();
	private final APIsController weatherApi;
	private final SnapshotManager snapshots;
	private final Encryption encryption;
	private Configuration configuration;
	private JapsonServer japson;
	private Current current;

	Server() {
		Configurations configurations = new Configurations();
		try {
			configuration = configurations.properties(new File("configuration.properties"));
		}
		catch (ConfigurationException exception) {
			exception.printStackTrace();
		}
		this.weatherApi = new WeatherAPIClient(configuration.getString("weather-api-key")).getAPIs();
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
			weatherApi.getRealtimeWeatherAsync("Chestermere", null, new APICallBack<CurrentJsonResponse>() {
				@Override
				public void onSuccess(HttpContext context, CurrentJsonResponse response) {
					current = response.getCurrent();
				}
				@Override
				public void onFailure(HttpContext context, Throwable error) {
					error.printStackTrace();
				}
			});
		}, 0, 15, TimeUnit.MINUTES);
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
					snapshots.addSnapshot(false, temperature, current);
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
