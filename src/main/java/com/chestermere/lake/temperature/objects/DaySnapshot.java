package com.chestermere.lake.temperature.objects;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DaySnapshot {

	private final Set<Snapshot> snapshots = new HashSet<>();
	private Date date;

	public DaySnapshot(Collection<Snapshot> snapshots) {
		this.snapshots.addAll(snapshots);
	}

	public DaySnapshot(Date date) {
		this.date = date;
	}

	public Date getDate() {
		if (date == null && !snapshots.isEmpty()) {
			List<Instant> sorted = snapshots.parallelStream()
					.sorted(Comparator.comparing(Snapshot::getCreation))
					.map(snapshot -> snapshot.getCreation())
					.collect(Collectors.toList());
			date = Date.from(sorted.get(sorted.size() / 2));
		}
		return date;
	}

}
