package com.chestermere.lake.temperature.database;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

import com.chestermere.lake.temperature.database.serializers.InstantSerializer;
import com.chestermere.lake.temperature.database.serializers.SnapshotSerializer;
import com.chestermere.lake.temperature.objects.Snapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class Database<T> {

	protected final Gson gson;

	public Database(Map<Type, Serializer<?>> serializers) {
		GsonBuilder builder = new GsonBuilder()
				.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
				.registerTypeAdapter(Snapshot.class, new SnapshotSerializer())
				.registerTypeAdapter(Instant.class, new InstantSerializer())
				.enableComplexMapKeySerialization()
				.serializeNulls();
		serializers.forEach((type, serializer) -> builder.registerTypeAdapter(type, serializer));
		gson = builder.create();
	}

	public Database() {
		gson = new GsonBuilder()
				.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
				.registerTypeAdapter(Snapshot.class, new SnapshotSerializer())
				.registerTypeAdapter(Instant.class, new InstantSerializer())
				.enableComplexMapKeySerialization()
				.serializeNulls().create();
	}

	public abstract void put(String key, T value);

	public abstract T get(String key, T def);

	public abstract boolean has(String key);

	public abstract Set<String> getKeys();

	public T get(String key) {
		return get(key, null);
	}

	public void delete(String key) {
		put(key, null);
	}

	public abstract void clear();

	public String serialize(Object object, Type type) {
		return gson.toJson(object, type);
	}

	public Object deserialize(String json, Type type) {
		return gson.fromJson(json, type);
	}

}
