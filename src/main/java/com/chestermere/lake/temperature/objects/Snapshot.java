package com.chestermere.lake.temperature.objects;

import java.time.Instant;

import org.joda.time.DateTime;

import com.chestermere.lake.temperature.controllers.InstantJackson;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Remember new additions require serialization adjustments.
 */
public class Snapshot {

	// Wind = kph, water = f, air = c
	private final double water, air, wind;
	@JsonSerialize(using = InstantJackson.class)
	private final Instant instant;
	private final boolean manual;
	private final long id;

	public Snapshot(Instant instant, boolean manual, double water, double air, double wind, long id) {
		this.instant = instant;
		this.manual = manual;
		this.water = water;
		this.wind = wind;
		this.air = air;
		this.id = id;
	}

	public Snapshot(boolean manual, int water, int air, double wind, long id) {
		this(Instant.now(), manual, water, air, wind, id);
	}

	public double getWaterTemperature() {
		return water;
	}

	public double getAirTemperature() {
		return air;
	}

	@JsonIgnore
	public DateTime getDateTime() {
		return new DateTime(instant);
	}

	/**
	 * @return The instantaneous point in time that this Snapshot was created.
	 */
	@JsonIgnore
	public Instant getCreation() {
		return instant;
	}

	public double getWindKph() {
		return wind;
	}

	public boolean isManual() {
		return manual;
	}

	public long getID() {
		return id;
	}

}
