package com.chestermere.lake.temperature.managers;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.chestermere.lake.temperature.Server;
import com.chestermere.lake.temperature.database.Database;
import com.chestermere.lake.temperature.database.H2Database;
import com.chestermere.lake.temperature.objects.DaySnapshot;
import com.chestermere.lake.temperature.objects.Snapshot;
import com.weatherapi.api.models.Current;

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

	public void addSnapshot(boolean manual, double water, Current weather) {
		long id = counter.incrementAndGet();
		Snapshot snapshot = new Snapshot(Instant.now(), manual, water, weather.getTempC(), weather.getWindKph(), id);
		database.put(id + "", snapshot);
		database.put("last", snapshot);
		last = snapshot;
	}

	public DaySnapshot getToday() {
		List<Snapshot> snapshots = new ArrayList<>();
		long start = last.getID();
		for (long i = start; i >= 0; i--) {
			snapshots.add(database.get(i + ""));
		}
//		Iterator<Snapshot> iterator = snapshots.iterator();
//		while (iterator.hasNext()) {
//			Snapshot snapshot = iterator.next();
//			if (last.getDateTime().getHourOfDay() - snapshot.getDateTime().getHourOfDay() > 24)
//				iterator.remove();
//		}
		return new DaySnapshot(snapshots);
	}

	public Snapshot getLatest() {
		return last;
	}

}
