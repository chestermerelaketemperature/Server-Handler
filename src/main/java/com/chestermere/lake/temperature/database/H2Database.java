package com.chestermere.lake.temperature.database;

import java.io.File;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.chestermere.lake.temperature.Server;
import com.google.gson.JsonSyntaxException;

public class H2Database<T> extends Database<T> {

	private final String tablename;
	private Connection connection;
	private final Type type;

	public H2Database(Server instance, String tablename, Type type) throws SQLException, ClassNotFoundException {
		this.type = type;
		this.tablename = tablename;
		Class.forName("org.h2.Driver");
		String url = "jdbc:h2:" + instance.getDataFolder().getAbsolutePath() + File.separator + "database";
		connection = DriverManager.getConnection(url);
		if (connection == null)
			return;
		PreparedStatement stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS %table (`id` CHAR(36) PRIMARY KEY, `data` TEXT);".replace("%table", tablename));
		stmt.executeUpdate();
		stmt.close();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(String key, T def) {
		T result = def;
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT `data` FROM %table WHERE `id` = ?;".replace("%table", tablename));
			statement.setString(1, key);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String json = rs.getString("data");
				try {
					result = (T) deserialize(json, type);
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
					return def;
				}
				if (result == null)
					return def;
			}
			statement.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void put(String key, T value) {
		new Thread(() -> {
			try {
				if (value != null) {
					PreparedStatement statement = connection.prepareStatement("REPLACE INTO %table (`id`,`data`) VALUES(?,?);".replace("%table", tablename));
					statement.setString(1, key);
					String json = serialize(value, type);
					statement.setString(2, json);
					statement.executeUpdate();
					statement.close();
				} else {
					PreparedStatement statement = connection.prepareStatement("DELETE FROM %table WHERE id = ?".replace("%table", tablename));
					statement.setString(1, key);
					statement.executeUpdate();
					statement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}).start();
	}

	@Override
	public boolean has(String key) {
		boolean result = false;
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT `id` FROM %table WHERE `id` = ?;".replace("%table", tablename));
			statement.setString(1, key);
			ResultSet rs = statement.executeQuery();
			result = rs.next();
			rs.close();
			statement.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void clear() {
		new Thread(() -> {
			try {
				PreparedStatement statement = connection.prepareStatement("DELETE FROM %table;".replace("%table", tablename));
				statement.executeQuery();
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}).start();
	}

	public Set<String> getKeys() {
		Set<String> tempset = new HashSet<>();
		new Thread(() -> {
			try {
				PreparedStatement statement = connection.prepareStatement("SELECT `id` FROM %table;".replace("%table", tablename));
				ResultSet result = statement.executeQuery();
				while (result.next())
					tempset.add(result.getString("id"));
				result.close();
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}).start();
		return tempset;
	}

}