package com.chestermere.lake.temperature.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class DaySnapshot {

	private final List<Snapshot> snapshots = new ArrayList<>();

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

	/**
	 * @return The average of all the snapshots.
	 */
	public LocalDate getDate() {
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
		Optional<LocalDate> optional = snapshots.parallelStream()
				.map(snapshot -> new DateTime(snapshot.getCreation()))
				.filter(date -> date.getDayOfMonth() == dayMonth)
				.map(date -> date.toLocalDate())
				.findFirst();
		if (optional.isPresent())
			return optional.get();
		return null;
	}

	public Set<Snapshot> getCheckedSnapshots() {
		return snapshots.parallelStream()
				.filter(snapshot -> new DateTime(snapshot.getCreation()).getDayOfMonth() == getDate().getDayOfMonth())
				.collect(Collectors.toSet());
	}

	public List<Snapshot> getSnapshots() {
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
