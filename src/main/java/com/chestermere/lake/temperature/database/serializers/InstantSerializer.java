package com.chestermere.lake.temperature.database.serializers;

import java.lang.reflect.Type;
import java.time.Instant;

import com.chestermere.lake.temperature.database.Serializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

public class InstantSerializer implements Serializer<Instant> {

	@Override
	public JsonElement serialize(Instant instant, Type type, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		if (instant == null)
			return json;
		json.addProperty("seconds", instant.getEpochSecond());
		json.addProperty("nanos", instant.getNano());
		return json;
	}

	@Override
	public Instant deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = json.getAsJsonObject();
		JsonElement epochElement = object.get("seconds");
		if (epochElement == null || epochElement.isJsonNull())
			return null;
		JsonElement nanoElement = object.get("nanos");
		if (nanoElement == null || nanoElement.isJsonNull())
			return null;
		return Instant.ofEpochSecond(epochElement.getAsLong(), nanoElement.getAsInt());
	}

}
