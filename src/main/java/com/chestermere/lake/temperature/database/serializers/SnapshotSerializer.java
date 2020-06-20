package com.chestermere.lake.temperature.database.serializers;

import java.lang.reflect.Type;
import java.time.Instant;

import com.chestermere.lake.temperature.database.Serializer;
import com.chestermere.lake.temperature.objects.Snapshot;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

public class SnapshotSerializer implements Serializer<Snapshot> {

	@Override
	public JsonElement serialize(Snapshot snapshot, Type type, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		if (snapshot == null)
			return json;
		json.add("instant", context.serialize(snapshot.getCreation(), Instant.class));
		json.addProperty("water", snapshot.getWaterTemperature());
		json.addProperty("air", snapshot.getAirTemperature());
		json.addProperty("manual", snapshot.isManual());
		json.addProperty("wind", snapshot.getWindKph());
		json.addProperty("id", snapshot.getID());
		return json;
	}

	@Override
	public Snapshot deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = json.getAsJsonObject();
		Instant instant = context.deserialize(object.get("instant"), Instant.class);
		if (instant == null)
			return null;
		JsonElement manual = object.get("manual");
		if (manual == null || manual.isJsonNull())
			return null;
		JsonElement water = object.get("water");
		if (water == null || water.isJsonNull())
			return null;
		JsonElement air = object.get("air");
		if (air == null || air.isJsonNull())
			return null;
		JsonElement id = object.get("id");
		if (id == null || id.isJsonNull())
			return null;
		JsonElement wind = object.get("wind");
		if (wind == null || wind.isJsonNull())
			return null;
		return new Snapshot(instant, manual.getAsBoolean(), water.getAsDouble(), air.getAsDouble(), wind.getAsDouble(), id.getAsLong());
	}

}
