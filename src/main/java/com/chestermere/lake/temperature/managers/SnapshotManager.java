package com.chestermere.lake.temperature.managers;

import java.sql.SQLException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

import com.chestermere.lake.temperature.Server;
import com.chestermere.lake.temperature.database.Database;
import com.chestermere.lake.temperature.database.H2Database;
import com.chestermere.lake.temperature.objects.Snapshot;

public class SnapshotManager {

	private final AtomicLong counter = new AtomicLong();
	private Database<Snapshot> database;
	private Snapshot last;

	public SnapshotManager(Server instance) {
		try {
			database = new H2Database<Snapshot>(instance, "snapshots", Snapshot.class);
			last = database.get("last");
			if (last != null)
				counter.set(last.getID());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void addSnapshot(boolean manual, double water, double air) {
		long id = counter.getAndIncrement();
		Snapshot snapshot = new Snapshot(Instant.now(), manual, water, air, id);
		database.put(id + "", snapshot);
		last = snapshot;
	}

	public Snapshot getLatest() {
		return last;
	}

}
