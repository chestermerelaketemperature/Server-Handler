package com.chestermere.lake.temperature.objects;

import java.time.Instant;

import org.joda.time.DateTime;

/**
 * Remember new additions require serialization adjustments.
 */
public class Snapshot {

	private final double water, air;
	private final Instant instant;
	private final boolean manual;
	private final long id;

	public Snapshot(Instant instant, boolean manual, double water, double air, long id) {
		this.instant = instant;
		this.manual = manual;
		this.water = water;
		this.air = air;
		this.id = id;
	}

	public Snapshot(boolean manual, int water, int air, long id) {
		this(Instant.now(), manual, water, air, id);
	}

	public double getWaterTemperature() {
		return water;
	}

	public double getAirTemperature() {
		return air;
	}

	public DateTime getDateTime() {
		return new DateTime(instant);
	}

	/**
	 * @return The instantaneous point in time that this Snapshot was created.
	 */
	public Instant getCreation() {
		return instant;
	}

	public boolean isManual() {
		return manual;
	}

	public long getID() {
		return id;
	}

}
