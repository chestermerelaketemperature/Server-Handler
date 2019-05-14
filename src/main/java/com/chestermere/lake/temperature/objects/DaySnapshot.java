package com.chestermere.lake.temperature.objects;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

public class DaySnapshot {

	private final Set<Snapshot> snapshots = new HashSet<>();
	private DateTime date;

	/**
	 * If the day/date of the Snapshots is unknown, use this constructor.
	 * <p>
	 * The getDate method will automatically determine the date of the Snapshots based on average.
	 * 
	 * @param snapshots An unknown date collection of Snapshots.
	 */
	public DaySnapshot(Collection<Snapshot> snapshots) {
		this.snapshots.addAll(snapshots);
	}

	public DaySnapshot(Collection<Snapshot> snapshots, DateTime date) {
		this.snapshots.addAll(snapshots);
		this.date = date;
	}

	public DateTime getDate() {
		if (date == null && !snapshots.isEmpty()) {
			Map<Integer, Integer> averages = new HashMap<>();
			for (Snapshot snapshot : snapshots) {
				DateTime date = new DateTime(snapshot.getCreation());
				int day = date.getDayOfMonth();
				int amount = Optional.ofNullable(averages.get(day)).orElse(0);
				averages.put(date.getDayOfMonth(), amount++);
			}
			int day = 1;
			int highest = 0;
			for (Entry<Integer, Integer> entry : averages.entrySet()) {
				int value = entry.getValue();
				if (value > highest) {
					highest = value;
					day = entry.getKey();
				}
			}
			int dayMonth = day;
			Optional<DateTime> optional = snapshots.parallelStream()
					.map(snapshot -> new DateTime(snapshot.getCreation()))
					.filter(date -> date.getDayOfMonth() == dayMonth)
					.findFirst();
			if (optional.isPresent())
				date = optional.get();
		}
		return date;
	}

	public Set<Snapshot> getCheckedSnapshots() {
		return snapshots.parallelStream()
				.filter(snapshot -> new DateTime(snapshot.getCreation()).getDayOfMonth() == getDate().getDayOfMonth())
				.collect(Collectors.toSet());
	}

	public Set<Snapshot> getSnapshots() {
		return snapshots;
	}

	/**
	 * If the date of the snapshots date is unknown,
	 * <p>
	 * this method will remove all dates not matching the same day.
	 */
	public void removeNonMatches() {
		Iterator<Snapshot> iterator = snapshots.iterator();
		int day = getDate().getDayOfMonth();
		while (iterator.hasNext()) {
			Snapshot snapshot = iterator.next();
			DateTime date = new DateTime(snapshot.getCreation());
			if (date.getDayOfMonth() != day)
				iterator.remove();
		}
	}

}
