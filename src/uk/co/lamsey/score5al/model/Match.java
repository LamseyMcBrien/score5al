package uk.co.lamsey.score5al.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Represents a match, which consists of a number of heats which in turn consist
 * of a number of jams.
 */
public class Match {

	/**
	 * The match's official name (e.g. "Summer Sur5al").
	 */
	private String name;

	/**
	 * An ordered list of the teams in this match.
	 */
	private List<Team> teams;

	/**
	 * An ordered list of the heats in this match.
	 */
	private List<Heat> heats;

	/**
	 * The number of seconds for which each jam lasts.
	 */
	private int jamDuration;

	/**
	 * The file name used to save/load this match's data.
	 */
	private String saveFilePath;

	/**
	 * Creates a new Match with default settings.
	 */
	public Match() {
		name = "New match";
		teams = new ArrayList<Team>(15);
		heats = new ArrayList<Heat>(15);
		jamDuration = 120;
		setNumTeams(15);
		setNumHeats(15);
		setNumJams(105);
		saveFilePath = null;
	}

	/**
	 * Updates the number of teams in the match.
	 */
	public void setNumTeams(int numTeams) {
		int currentTeams = teams.size();

		// if there are now more teams, add default teams
		if (numTeams > currentTeams) {
			for (int x = currentTeams + 1; x <= numTeams; x++) {
				teams.add(new Team(x));
			}
		}

		// if there are now less teams, remove highest-numbered teams
		if (numTeams < currentTeams) {
			Set<Team> removedTeams = new HashSet<Team>();
			for (int x = currentTeams; x > numTeams; x--) {
				removedTeams.add(teams.remove(x - 1));
			}

			// make sure there aren't any jams with these teams
			for (Heat heat : heats) {
				for (Jam jam : heat.getJams()) {
					if (removedTeams.contains(jam.getTeam1())) {
						jam.setTeam1(null);
						jam.setScore1(0);
						jam.setScore2(0);
						jam.setLeadJammer(null);
						jam.setTimeRemaining(jamDuration);
					}
					if (removedTeams.contains(jam.getTeam2())) {
						jam.setTeam2(null);
						jam.setScore1(0);
						jam.setScore2(0);
						jam.setLeadJammer(null);
						jam.setTimeRemaining(jamDuration);
					}
				}
			}
		}
	}

	/**
	 * Updates the number of heats in the match. Any existing jams are
	 * distributed amongst the remaining heats.
	 */
	public void setNumHeats(int numHeats) {

		// build an ordered list of the current jams
		List<Jam> allJams = new ArrayList<Jam>();
		for (Heat heat : heats) {
			allJams.addAll(heat.getJams());
		}

		// rebuild the list of heats
		heats = new ArrayList<Heat>();
		int jamsPerHeat = allJams.size() / numHeats;
		int remainder = allJams.size() % numHeats;
		for (int x = 0; x < numHeats; x++) {
			Heat heat = new Heat();
			heats.add(heat);
			for (int y = 0; y < jamsPerHeat; y++) {
				heat.getJams().add(allJams.get(0));
			}
			if (x < remainder) {
				heat.getJams().add(allJams.get(0));
			}
		}
	}

	/**
	 * Updates the number of jams in the match. Any additions or removals are
	 * distributed evenly amongst the heats.
	 */
	public void setNumJams(int numJams) {

		int currentJams = getTotalJams();
		int currentHeats = getTotalHeats();

		// check if jams need to be added
		if (numJams > currentJams) {
			for (int x = currentJams; x < numJams; x++) {
				heats.get(x % currentHeats).getJams().add(new Jam(jamDuration));
			}
		}

		// check if jams need to be removed
		if (numJams < currentJams) {
			for (int x = currentJams; x > numJams; x--) {
				List<Jam> heatJams = heats.get(x % currentHeats).getJams();
				heatJams.remove(heatJams.size() - 1);
			}
		}
	}

	/**
	 * Returns the match's official name (e.g. "Summer Sur5al").
	 */
	public String getName() {
		return name;
	}

	/**
	 * Updates the match's official name (e.g. "Summer Sur5al").
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the number of seconds for which each jam lasts.
	 */
	public int getJamDuration() {
		return jamDuration;
	}

	/**
	 * Updates the number of seconds for which each jam lasts.
	 */
	public void setJamDuration(int newDuration) {
		// update any jams that have the old default duration
		for (Heat heat : heats) {
			for (Jam jam : heat.getJams()) {
				if (jam.getTimeRemaining() == jamDuration
						|| jam.getTimeRemaining() > newDuration) {
					jam.setTimeRemaining(newDuration);
				}
			}
		}
		jamDuration = newDuration;
	}

	/**
	 * Returns the ordered list of the heats in this match.
	 */
	public List<Heat> getHeats() {
		return heats;
	}

	/**
	 * Returns the ordered list of the teams in this match.
	 */
	public List<Team> getTeams() {
		return teams;
	}

	/**
	 * Returns the number of teams in this match.
	 */
	public int getTotalTeams() {
		return teams.size();
	}

	/**
	 * Returns the number of heats in this match.
	 */
	public int getTotalHeats() {
		return heats.size();
	}

	/**
	 * Returns the number of jams in this match.
	 */
	public int getTotalJams() {
		int count = 0;
		for (Heat heat : heats) {
			count += heat.getTotalJams();
		}
		return count;
	}

	/**
	 * Returns the jam with the specified match-wide index (first jam in first
	 * heat is 0, last jam in last heat is (total jams - 1). Returns null if the
	 * jam index is invalid.
	 */
	public Jam getJam(int jamIndex) {

		// can't have a negative index
		if (jamIndex < 0) {
			return null;
		}

		Iterator<Heat> iterator = heats.iterator();
		while (iterator.hasNext()) {
			List<Jam> jams = iterator.next().getJams();
			if (jamIndex < jams.size()) {
				return jams.get(jamIndex);
			} else {
				jamIndex -= jams.size();
			}
		}

		// invalid index
		return null;
	}

	/**
	 * Returns the jam with the specified heat and jam indexes. Returns null if
	 * either index is invalid.
	 * 
	 * @param heatIndex
	 *            The index of the heat, starting from 0 for the first heat.
	 * @param jamIndex
	 *            The index of the jam, starting from 0 for the first jam in
	 *            that heat.
	 */
	public Jam getJam(int heatIndex, int jamIndex) {
		if (heatIndex < 0 || heatIndex >= heats.size()) {
			return null;
		} else {
			List<Jam> jams = heats.get(heatIndex).getJams();
			if (jamIndex < 0 || jamIndex >= jams.size()) {
				return null;
			} else {
				return jams.get(jamIndex);
			}
		}
	}

	/**
	 * Returns the file name used to save/load this match's data.
	 */
	public String getSaveFilePath() {
		return saveFilePath;
	}

	/**
	 * Updates the file name used to save/load this match's data.
	 */
	public void setSaveFilePath(String saveFilePath) {
		this.saveFilePath = saveFilePath;
	}
}
