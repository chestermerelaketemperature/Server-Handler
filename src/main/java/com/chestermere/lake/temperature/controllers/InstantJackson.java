package com.chestermere.lake.temperature.controllers;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@SuppressWarnings("serial")
public class InstantJackson extends StdSerializer<Instant> {

	public InstantJackson() {
		 this(null);
	}
  
	public InstantJackson(Class<Instant> clazz) {
		 super(clazz);
	}

	@Override
	public void serialize(Instant instant, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		if (instant == null)
			jgen.writeNull();
		else
			jgen.writeString(DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("Z")).format(instant));
	}

}
