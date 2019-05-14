package com.chestermere.lake.temperature.objects;

import java.time.Instant;

public class Snapshot {

	private final Instant instant;
	
	public Snapshot(Instant instant) {
		this.instant = instant;
	}

	/**
	 * @return The instantaneous point in time that this Snapshot was created.
	 */
	public Instant getCreation() {
		return instant;
	}

}
