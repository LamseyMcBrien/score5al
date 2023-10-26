package uk.co.lamsey.score5al.control;

import java.awt.Color;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.lamsey.score5al.model.Heat;
import uk.co.lamsey.score5al.model.Jam;
import uk.co.lamsey.score5al.model.Match;
import uk.co.lamsey.score5al.model.Ranking;
import uk.co.lamsey.score5al.model.Team;
import uk.co.lamsey.score5al.ui.ExportTeamFormat;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * Used to manipulate the currently-active Match and coordinates updates via the
 * Observer design pattern.
 */
public class MatchControl extends Observable<Match> implements Observer<Jam> {

	/**
	 * The currently-loaded match.
	 */
	private Match match;

	/**
	 * Whether or not the match has been changed since it was last saved.
	 */
	private boolean unsavedChanges;

	/**
	 * Creates a default Match.
	 */
	private MatchControl() {
		super();
		match = new Match();
		unsavedChanges = false;
		addObserver(new ChangeMonitor<Match>());
	}

	/**
	 * Listens for updates from the Jam panel.
	 */
	public void update(Jam updatedObject) {
		// update all observers - match data has changed
		notifyObservers(match);
	}

	/**
	 * Updates the current Match's details.
	 * 
	 * @return true if a change was made, else false.
	 */
	public boolean updateMatch(String name, int numTeams, int numHeats,
			int numJams, int jamDuration) throws LogicException {

		// if any changes were made, notify observers
		boolean changeMade = updateMatch(match, name, numTeams, numHeats,
				numJams, jamDuration);
		if (changeMade) {
			notifyObservers(match);
		}
		return changeMade;
	}

	/**
	 * Updates the passed Match's details. Used when loading or updating a
	 * match.
	 */
	private boolean updateMatch(Match matchToUpdate, String name, int numTeams,
			int numHeats, int numJams, int jamDuration) throws LogicException {

		// trim string input
		if (name != null) {
			name = name.trim();
		}

		// validate the new details
		if (name == null || name.isEmpty()) {
			throw new LogicException("Match name must not be blank.");
		} else if (numTeams < 2) {
			throw new LogicException("Number of teams must be greater than "
					+ "one.");
		} else if (numHeats < 1) {
			throw new LogicException("Number of heats must be greater than "
					+ "zero.");
		} else if (numJams < numHeats) {
			throw new LogicException("Number of jams must be at least one per "
					+ "heat.");
		} else if (jamDuration < 1) {
			throw new LogicException("Jam duration can't be less than one "
					+ "second.");
		}

		// update any changed details
		boolean changeMade = false;
		if (!name.equals(matchToUpdate.getName())) {
			matchToUpdate.setName(name);
			changeMade = true;
		}
		if (numTeams != matchToUpdate.getTotalTeams()) {
			matchToUpdate.setNumTeams(numTeams);
			changeMade = true;
		}
		if (numHeats != matchToUpdate.getTotalHeats()) {
			matchToUpdate.setNumHeats(numHeats);
			changeMade = true;
		}
		if (numJams != matchToUpdate.getTotalJams()) {
			matchToUpdate.setNumJams(numJams);
			changeMade = true;
		}
		if (jamDuration != matchToUpdate.getJamDuration()) {
			matchToUpdate.setJamDuration(jamDuration);
		}

		return changeMade;
	}

	/**
	 * Updates the specified Team's details.
	 * 
	 * @return true if a change was made, else false.
	 */
	public boolean updateTeam(int teamNum, String name, String abbreviation,
			Color fgColour, Color bgColour, int pointsAdjustment)
			throws LogicException {

		// check a valid team was specified
		if (match.getTotalTeams() <= teamNum) {
			throw new LogicException("Invalid team number: " + teamNum);
		}

		// if any changes were made, notify observers
		boolean changeMade = updateTeam(match.getTeams().get(teamNum), name,
				abbreviation, fgColour, bgColour, pointsAdjustment);
		if (changeMade) {
			notifyObservers(match);
		}
		return changeMade;
	}

	/**
	 * Updates the passed Team's details. Used when loading or updating a match.
	 */
	private boolean updateTeam(Team team, String name, String abbreviation,
			Color fgColour, Color bgColour, int pointsAdjustment)
			throws LogicException {

		// trim string input
		if (name != null) {
			name = name.trim();
		}
		if (abbreviation != null) {
			abbreviation = abbreviation.trim();
		} else {
			abbreviation = "";
		}

		// validate the new details
		if (name == null || name.isEmpty()) {
			throw new LogicException("Team name must not be blank.");
		} else if (fgColour == null) {
			throw new LogicException("Team foreground colour must not be null.");
		} else if (bgColour == null) {
			throw new LogicException("Team background colour must not be null.");
		}

		// update any changed details
		boolean changeMade = false;
		if (!name.equals(team.getName())) {
			team.setName(name);
			changeMade = true;
		}
		if (!abbreviation.equals(team.getAbbreviation())) {
			team.setAbbreviation(abbreviation);
			changeMade = true;
		}
		if (!fgColour.equals(team.getFgColour())) {
			team.setFgColour(fgColour);
			changeMade = true;
		}
		if (!bgColour.equals(team.getBgColour())) {
			team.setBgColour(bgColour);
			changeMade = true;
		}
		if (pointsAdjustment != team.getPointsAdjustment()) {
			team.setPointsAdjustment(pointsAdjustment);
			changeMade = true;
		}

		return changeMade;
	}

	/**
	 * Updates the teams in the specified heat. Zeroes out any scores in a
	 * changed jam.
	 * 
	 * @param heatNum
	 *            The number of the heat being edited.
	 * @param team1List
	 *            A list containing the first teams to be allocated to the jams
	 *            in the heat.
	 * @param team2List
	 *            A list containing the second teams to be allocated to the jams
	 *            in the heat.
	 * @return Whether or not a change was made.
	 */
	public boolean updateHeat(int heatNum, List<Team> team1List,
			List<Team> team2List) throws LogicException {

		// double-check the numbers
		if (heatNum >= match.getTotalHeats()) {
			throw new LogicException("Internal error: invalid heat number.");
		}
		Heat heat = match.getHeats().get(heatNum);
		int jamCount = heat.getTotalJams();
		if (team1List.size() != jamCount) {
			throw new LogicException("Internal error: team 1 list mismatch.");
		} else if (team2List.size() != heat.getTotalJams()) {
			throw new LogicException("Internal error: team 2 list mismatch.");
		}

		// update the teams in each jam in this heat
		boolean heatChangeMade = false;
		for (int jamNo = 0; jamNo < jamCount; jamNo++) {

			// get the new teams
			Team team1 = team1List.get(jamNo);
			Team team2 = team2List.get(jamNo);

			// update the jam if necessary
			boolean jamChangeMade = false;
			Jam jam = heat.getJams().get(jamNo);
			if (team1 != jam.getTeam1()) {
				jam.setTeam1(team1);
				jamChangeMade = true;
			}
			if (team2 != jam.getTeam2()) {
				jam.setTeam2(team2);
				jamChangeMade = true;
			}

			// if a jam change was made, zero its scores
			if (jamChangeMade) {
				jam.setScore1(0);
				jam.setScore2(0);
				jam.setLeadJammer(null);
				jam.setTimeRemaining(match.getJamDuration());
				heatChangeMade = true;
			}
		}

		// notify if a change was made
		if (heatChangeMade) {
			notifyObservers(match);
		}
		return heatChangeMade;
	}

	/**
	 * Attempts to auto-assign teams to all heats, distributing teams as evenly
	 * as possible.
	 * 
	 * @param teamsPerHeat
	 *            The number of teams to be assigned to each heat.
	 * @throws LogicException
	 *             If there are too many teams per heat.
	 */
	public void autoAssign(int teamsPerHeat) throws LogicException {

		// check input
		if (teamsPerHeat > match.getTotalTeams()) {
			throw new LogicException("Error: teams per heat cannot exceed "
					+ "total number of teams");
		} else if (teamsPerHeat < 2) {
			throw new LogicException(
					"Error: Must have at least two teams per heat");
		}

		// keep trying until we have a perfect distribution
		int lowCount, highCount, iterations = 0;
		long startTime = System.currentTimeMillis();
		List<Team> allTeams = match.getTeams();
		do {
			// check we haven't been trying for too long (60s)
			if (System.currentTimeMillis() - startTime > 60000) {
				notifyObservers(match);
				throw new LogicException("Couldn't create a perfect "
						+ "distribution after 60s (" + iterations
						+ " attempts).\nIf retrying doesn't work, you could "
						+ "try increasing the number of teams per heat.");
			}

			// build a map to track team-versus-team assignment count
			Map<Team, Map<Team, Integer>> teamVsCountMap = new HashMap<Team, Map<Team, Integer>>();
			for (Team team : allTeams) {
				Map<Team, Integer> teamMap = new HashMap<Team, Integer>();
				teamVsCountMap.put(team, teamMap);
				for (Team opponent : allTeams) {
					if (opponent != team) {
						teamMap.put(opponent, 0);
					}
				}
			}

			// build a map to track team total assignment count
			Map<Team, Integer> teamJamCountMap = new HashMap<Team, Integer>();
			for (Team team : allTeams) {
				teamJamCountMap.put(team, 0);
			}

			// build a map to track the last jam to which each team was assigned
			Map<Team, Integer> teamLastJamMap = new HashMap<Team, Integer>();
			for (Team team : allTeams) {
				teamLastJamMap.put(team, -1);
			}

			// iterate over each heat and assign teams
			for (Heat heat : match.getHeats()) {

				// find the first team to add to the heat
				List<Team> candidateTeams = allTeams;

				// find the teams which have played least recently
				candidateTeams = getLowestTeams(candidateTeams, teamJamCountMap);

				// of those, find the teams who have played the least jams
				candidateTeams = getLowestTeams(candidateTeams, teamJamCountMap);

				// select the first remaining team
				List<Team> heatTeams = new ArrayList<Team>();
				int randomIndex = (int) (Math.random() * candidateTeams.size());
				heatTeams.add(candidateTeams.get(randomIndex));

				// add the remaining teams to the heat
				while (heatTeams.size() < teamsPerHeat) {

					// map how often each other team has played the heat teams
					candidateTeams = new ArrayList<Team>();
					Map<Team, Integer> heatVsCountMap = new HashMap<Team, Integer>();
					for (Team team : allTeams) {
						if (!heatTeams.contains(team)) {
							candidateTeams.add(team);
							int playedHeatTeamsCount = 0;
							for (Team opponent : heatTeams) {
								playedHeatTeamsCount += teamVsCountMap
										.get(team).get(opponent);
							}
							heatVsCountMap.put(team, playedHeatTeamsCount);
						}
					}

					// find the teams which have played the heat teams least
					candidateTeams = getLowestTeams(candidateTeams,
							heatVsCountMap);

					// of those, find the teams which have played least recently
					candidateTeams = getLowestTeams(candidateTeams,
							teamLastJamMap);

					// of those, find the teams which have played the least jams
					candidateTeams = getLowestTeams(candidateTeams,
							teamJamCountMap);

					// add a random team to the heat
					randomIndex = (int) (Math.random() * candidateTeams.size());
					heatTeams.add(candidateTeams.get(randomIndex));
				}

				// map how often the teams in the heat have played each other
				Map<Team, Integer> heatVsCountMap = new HashMap<Team, Integer>();
				for (Team team : heatTeams) {
					heatVsCountMap.put(team, 0);
					for (Team opponent : heatTeams) {
						if (opponent != team) {
							heatVsCountMap.put(team, heatVsCountMap.get(team)
									+ teamVsCountMap.get(team).get(opponent));
						}
					}
				}

				// iterate over each jam in this heat and assign teams
				for (Jam jam : heat.getJams()) {

					// first find team 1
					candidateTeams = new ArrayList<Team>(heatTeams);

					// find the teams which have played the others least
					candidateTeams = getLowestTeams(candidateTeams,
							heatVsCountMap);

					// of those, find the teams which have played least recently
					candidateTeams = getLowestTeams(candidateTeams,
							teamLastJamMap);

					// of those, find the teams which have played the least jams
					candidateTeams = getLowestTeams(candidateTeams,
							teamJamCountMap);

					// take the first one from the list and use it as team 1
					randomIndex = (int) (Math.random() * candidateTeams.size());
					Team team1 = candidateTeams.get(randomIndex);

					// now find team 2
					candidateTeams = new ArrayList<Team>(heatTeams);
					candidateTeams.remove(team1);

					// find the teams which team 1 has played the fewest times
					candidateTeams = getLowestTeams(candidateTeams,
							teamVsCountMap.get(team1));

					// of those, find the teams which have played least recently
					candidateTeams = getLowestTeams(candidateTeams,
							teamLastJamMap);

					// of those, find the teams which have played the least jams
					candidateTeams = getLowestTeams(candidateTeams,
							teamJamCountMap);

					// take the last one from the list and use it as team 2
					randomIndex = (int) (Math.random() * candidateTeams.size());
					Team team2 = candidateTeams.get(randomIndex);

					// now update the jam
					jam.setTeam1(team1);
					jam.setTeam2(team2);
					jam.setScore1(0);
					jam.setScore2(0);
					jam.setLeadJammer(null);
					jam.setTimeRemaining(match.getJamDuration());

					// work out this jam's overall match number
					int jamNo = (match.getHeats().indexOf(heat) * heat
							.getTotalJams()) + heat.getJams().indexOf(jam);

					// update all the counters for team 1
					Map<Team, Integer> team1VsCountMap = teamVsCountMap
							.get(team1);
					team1VsCountMap.put(team2, team1VsCountMap.get(team2) + 1);
					teamJamCountMap.put(team1, teamJamCountMap.get(team1) + 1);
					teamLastJamMap.put(team1, jamNo);
					heatVsCountMap.put(team1, heatVsCountMap.get(team1) + 1);

					// update counters for team 2
					Map<Team, Integer> team2VsCountMap = teamVsCountMap
							.get(team2);
					team2VsCountMap.put(team1, team2VsCountMap.get(team1) + 1);
					teamJamCountMap.put(team2, teamJamCountMap.get(team2) + 1);
					teamLastJamMap.put(team2, jamNo);
					heatVsCountMap.put(team2, heatVsCountMap.get(team2) + 1);
				}
			}

			// check if we have as perfect a distribution as possible
			highCount = 0;
			lowCount = Integer.MAX_VALUE;
			iterations++;
			for (Team team : allTeams) {
				for (Team opponent : allTeams) {
					if (opponent != team) {
						int count = teamVsCountMap.get(team).get(opponent);
						if (count > highCount) {
							highCount = count;
						}
						if (count < lowCount) {
							lowCount = count;
						}
					}
				}
			}
		} while (highCount - lowCount > 1);

		// rearrange teams in order
		for (Team thisTeam : allTeams) {
			int teamNo = allTeams.indexOf(thisTeam);

			// look for the first jam involving a team >= this teams' number
			Team otherTeam = null;
			for (Heat heat : match.getHeats()) {
				if (otherTeam == null) {
					for (Jam jam : heat.getJams()) {
						if (allTeams.indexOf(jam.getTeam1()) >= teamNo) {
							otherTeam = jam.getTeam1();
							break;
						} else if (allTeams.indexOf(jam.getTeam2()) >= teamNo) {
							otherTeam = jam.getTeam2();
							break;
						}
					}
				} else {
					break;
				}
			}

			// if the other team isn't the expected team, swap them
			if (thisTeam != otherTeam) {
				for (Heat heat : match.getHeats()) {
					for (Jam jam : heat.getJams()) {
						if (jam.getTeam1() == thisTeam) {
							jam.setTeam1(otherTeam);
						} else if (jam.getTeam1() == otherTeam) {
							jam.setTeam1(thisTeam);
						}
						if (jam.getTeam2() == thisTeam) {
							jam.setTeam2(otherTeam);
						} else if (jam.getTeam2() == otherTeam) {
							jam.setTeam2(thisTeam);
						}
					}
				}
			}
		}

		// notify observers
		notifyObservers(match);
	}

	/**
	 * Selects the teams with the lowest count from the passed Map.
	 * 
	 * @param includeList
	 *            The list of candidate teams which should be checked.
	 * @param valueMap
	 *            The map whose values should be inspected.
	 * @return The list of teams who have the minimum value from the passed Map.
	 */
	private List<Team> getLowestTeams(List<Team> includeList,
			Map<Team, Integer> valueMap) {

		// find the lowest value in the map
		int lowestValue = Integer.MAX_VALUE;
		for (Team team : includeList) {
			int teamValue = valueMap.get(team);
			if (teamValue < lowestValue) {
				lowestValue = teamValue;
			}
		}

		// find the teams who have the lowest value
		List<Team> lowestTeams = new ArrayList<Team>();
		for (Team team : includeList) {
			if (valueMap.get(team) == lowestValue) {
				lowestTeams.add(team);
			}
		}
		return lowestTeams;
	}

	/**
	 * Updates the passed Jam's details. Used when loading or updating a match.
	 */
	private boolean updateJam(Jam jam, Team team1, Team team2, Team leadJammer,
			int score1, int score2, int timeRemaining) throws LogicException {

		// validate the new details
		if (leadJammer != null && (team1 == null || team2 == null)) {
			throw new LogicException("Lead jammer can't be set without both "
					+ "teams being set.");
		} else if (score1 < 0) {
			throw new LogicException("Team 1 score can't be negative.");
		} else if (score2 < 0) {
			throw new LogicException("Team 2 score can't be negative.");
		} else if (timeRemaining < 0) {
			throw new LogicException("Time remaining can't be negative.");
		}

		// update any changed details
		boolean changeMade = false;
		if (team1 != jam.getTeam1()) {
			jam.setTeam1(team1);
			changeMade = true;
		}
		if (team2 != jam.getTeam2()) {
			jam.setTeam2(team2);
			changeMade = true;
		}
		if (leadJammer != jam.getLeadJammer()) {
			jam.setLeadJammer(leadJammer);
			changeMade = true;
		}
		if (score1 != jam.getScore1()) {
			jam.setScore1(score1);
			changeMade = true;
		}
		if (score1 != jam.getScore1()) {
			jam.setScore2(score2);
			changeMade = true;
		}
		if (timeRemaining != jam.getTimeRemaining()) {
			jam.setTimeRemaining(timeRemaining);
			changeMade = true;
		}

		return changeMade;
	}

	/**
	 * Discards the current match and creates a new one.
	 */
	public void newMatch() {
		match = new Match();
		notifyObservers(match);
		unsavedChanges = false;
	}

	/**
	 * Saves the current match to disk.
	 * 
	 * @param saveFilePath
	 *            The path to the file to be written.
	 * @throws LogicException
	 *             If an I/O error occurs.
	 */
	public void saveMatch(String saveFilePath) throws LogicException {

		// check a file path has been defined
		if (saveFilePath == null) {
			throw new LogicException("Error: save file name undefined.");
		}

		// build the save file data
		List<String[]> csvData = new ArrayList<String[]>();

		// first line: total match data
		csvData.add(new String[] { "-----", "NAME", "# TEAMS", "# HEATS",
				"# JAMS", "JAM DURATION" });
		csvData.add(new String[] { "MATCH", match.getName(),
				String.valueOf(match.getTotalTeams()),
				String.valueOf(match.getTotalHeats()),
				String.valueOf(match.getTotalJams()),
				String.valueOf(match.getJamDuration()) });

		// following lines: team data
		csvData.add(new String[] { "-----", "INDEX", "NAME", "ABBREVATION",
				"BG COLOUR", "FG COLOUR", "POINTS ADJUSTMENT" });
		List<Team> teams = match.getTeams();
		for (Team team : teams) {
			csvData.add(new String[] { "TEAM",
					String.valueOf(team.getNumber() - 1), team.getName(),
					team.getAbbreviation(),
					String.valueOf(team.getBgColour().getRGB()),
					String.valueOf(team.getFgColour().getRGB()),
					String.valueOf(team.getPointsAdjustment()) });
		}

		// following lines: jam data
		csvData.add(new String[] { "-----", "JAM INDEX", "TEAM 1 INDEX",
				"TEAM 2 INDEX", "LEAD JAMMER INDEX", "SCORE 1", "SCORE 2",
				"TIME REMAINING" });
		int jamIndex = 0;
		for (Heat heat : match.getHeats()) {
			for (Jam jam : heat.getJams()) {
				csvData.add(new String[] { "JAM", String.valueOf(jamIndex),
						String.valueOf(teams.indexOf(jam.getTeam1())),
						String.valueOf(teams.indexOf(jam.getTeam2())),
						String.valueOf(teams.indexOf(jam.getLeadJammer())),
						String.valueOf(jam.getScore1()),
						String.valueOf(jam.getScore2()),
						String.valueOf(jam.getTimeRemaining()) });
				jamIndex++;
			}
		}

		// CSV data complete, write to disk
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(saveFilePath));
			writer.writeAll(csvData);
			writer.close();

			// check for IO errors
			if (writer.checkError()) {
				throw new LogicException(
						"Couldn't save file - an I/O error occurred.");
			}

			// make sure the match has this file name set
			match.setSaveFilePath(saveFilePath);

			// if successful, then there are no unsaved changes
			unsavedChanges = false;

		} catch (IOException error) {
			throw new LogicException("Couldn't save file - I/O error: " + error);
		}
	}

	/**
	 * Loads the current match from disk.
	 * 
	 * @param saveFilePath
	 *            The path to the file containing the save data.
	 * @return A list of non-fatal warnings - empty implies a successful load.
	 * @throws LogicException
	 *             If an I/O error or fatal parsing error occurs.
	 */
	public List<String> loadMatch(String saveFilePath) throws LogicException {

		// check a file path has been defined
		if (saveFilePath == null) {
			throw new LogicException("Error: save file name undefined.");
		}

		// try to read the data from file
		List<String[]> lines;
		try {
			CSVReader reader = new CSVReader(new FileReader(saveFilePath));
			lines = reader.readAll();
			reader.close();
		} catch (IOException error) {
			throw new LogicException("Error: couldn't read from file: "
					+ error.getMessage());
		}

		// create a new match object to be updated with the file data
		Match newMatch = new Match();
		newMatch.setSaveFilePath(saveFilePath);

		// count how many lines of each type there are to check file consistency
		int matchLines = 0;
		int teamLines = 0;
		int jamLines = 0;
		int incompleteLines = 0;
		int unknownLines = 0;

		// iterate over lines, tracking line number for reporting errors
		int lineNum = 0;
		for (String[] line : lines) {
			lineNum++;

			try {
				// ignore blank lines
				if (line.length > 0 && line[0].length() > 0) {

					/*
					 * When parsing lines below, any missing data is ignored and
					 * left as per the program defaults. This will allow old
					 * save files to be read even if new fields are added to the
					 * file format in future versions, and might even allow new
					 * save files to be read by old software versions.
					 */

					// token 0 is the keyword describing line type
					if (line[0].equalsIgnoreCase("MATCH")) {

						// check there hasn't already been a MATCH line
						if (matchLines > 0) {
							throw new LogicException(
									"Highlander error: second MATCH at line "
											+ lineNum
											+ " - there can be only one.");
						}

						// check line is complete
						if (line.length < 6) {
							incompleteLines++;
						}

						// parse the line data
						String name = line.length > 1 ? line[1] : newMatch
								.getName();
						int teams = line.length > 2 ? Integer.parseInt(line[2])
								: newMatch.getTotalTeams();
						int heats = line.length > 3 ? Integer.parseInt(line[3])
								: newMatch.getTotalHeats();
						int jams = line.length > 4 ? Integer.parseInt(line[4])
								: newMatch.getTotalJams();
						int duration = line.length > 5 ? Integer
								.parseInt(line[5]) : newMatch.getJamDuration();

						// update the new match object and increment counter
						updateMatch(newMatch, name, teams, heats, jams,
								duration);
						matchLines++;

					} else if (line[0].equalsIgnoreCase("TEAM")) {

						// token 1 is team index and is not optional
						if (line.length == 1) {
							throw new LogicException("Error: no team index "
									+ "specified on line " + lineNum + ".");
						}
						int index = Integer.parseInt(line[1]);
						if (index < 0 || index >= newMatch.getTotalTeams()) {
							throw new LogicException("Error: invalid team "
									+ "index specified on line " + lineNum
									+ ".");
						}

						// check line is complete
						if (line.length < 7) {
							incompleteLines++;
						}

						// parse the rest of the team data
						Team team = newMatch.getTeams().get(index);
						String name = line.length > 2 ? line[2] : team
								.getName();
						String abbr = line.length > 3 ? line[3] : team
								.getAbbreviation();
						Color bg = line.length > 4 ? new Color(
								Integer.parseInt(line[4])) : team.getBgColour();
						Color fg = line.length > 5 ? new Color(
								Integer.parseInt(line[5])) : team.getFgColour();
						int pointsAdjustment = line.length > 6 ? Integer
								.parseInt(line[6]) : team.getPointsAdjustment();

						// update the team object and increment counter
						updateTeam(team, name, abbr, fg, bg, pointsAdjustment);
						teamLines++;

					} else if (line[0].equalsIgnoreCase("JAM")) {

						// token 1 is jam index and is not optional
						if (line.length == 1) {
							throw new LogicException("Error: no jam index "
									+ "specified on line " + lineNum + ".");
						}
						int index = Integer.parseInt(line[1]);
						if (index < 0 || index >= newMatch.getTotalJams()) {
							throw new LogicException("Error: invalid jam "
									+ "index specified on line " + lineNum
									+ ".");
						}
						Jam jam = newMatch.getJam(index);

						// check line is complete
						if (line.length < 8) {
							incompleteLines++;
						}

						// parse the team indexes
						List<Team> teams = newMatch.getTeams();
						index = line.length > 2 ? Integer.parseInt(line[2])
								: teams.indexOf(jam.getTeam1());
						if (index < -1 || index >= teams.size()) {
							throw new LogicException("Error: invalid team 1 "
									+ "index specified on line " + lineNum
									+ ".");
						}
						Team team1 = index == -1 ? null : teams.get(index);
						index = line.length > 3 ? Integer.parseInt(line[3])
								: teams.indexOf(jam.getTeam2());
						if (index < -1 || index >= teams.size()) {
							throw new LogicException("Error: invalid team 2 "
									+ "index specified on line " + lineNum
									+ ".");
						}
						Team team2 = index == -1 ? null : teams.get(index);
						index = line.length > 4 ? Integer.parseInt(line[4])
								: teams.indexOf(jam.getLeadJammer());
						if (index < -1 || index >= teams.size()) {
							throw new LogicException("Error: invalid LJ team "
									+ "index specified on line " + lineNum
									+ ".");
						}
						Team teamLJ = index == -1 ? null : teams.get(index);

						// parse the rest of the jam data
						int score1 = line.length > 5 ? Integer
								.parseInt(line[5]) : jam.getScore1();
						int score2 = line.length > 6 ? Integer
								.parseInt(line[6]) : jam.getScore2();
						int time = line.length > 7 ? Integer.parseInt(line[7])
								: jam.getTimeRemaining();

						// update the jam object and increment counter
						updateJam(jam, team1, team2, teamLJ, score1, score2,
								time);
						jamLines++;

					} else if (!line[0].matches("-+")) {
						// unknown keyword and not a comment line
						unknownLines++;
					}
				}
			} catch (NumberFormatException error) {
				throw new LogicException("Number parsing error at line "
						+ lineNum + ": " + error.getMessage());
			}
		}

		// check there was at least something in the file
		if (matchLines == 0 && teamLines == 0 && jamLines == 0) {
			throw new LogicException("Error: no Score5al data found in file.");
		}

		// build list of warnings
		List<String> warnings = new ArrayList<String>();
		if (matchLines == 0) {
			warnings.add("   No MATCH line found - match initialised with defaults.");
		}
		if (teamLines != newMatch.getTotalTeams()) {
			warnings.add("   " + teamLines + " TEAM lines found, expected "
					+ newMatch.getTotalTeams());
		}
		if (jamLines != newMatch.getTotalJams()) {
			warnings.add("   " + jamLines + " JAM lines found, expected "
					+ newMatch.getTotalJams());
		}
		if (incompleteLines > 0) {
			warnings.add("   " + incompleteLines
					+ " incomplete lines found and initialised with defaults.");
		}
		if (unknownLines > 0) {
			warnings.add("   " + unknownLines
					+ " unknown lines found and ignored.");
		}
		if (!warnings.isEmpty()) {
			warnings.add(0, "The file was loaded, but the following non-fatal "
					+ "errors occurred:");
			warnings.add("The file may be corrupt or from an old Score5al version.");
			warnings.add("Please verify the match data is correct.");
		}

		// all good, replace current match with the new one and notify observers
		match = newMatch;
		notifyObservers(match);

		// if successful, then there are no unsaved changes
		unsavedChanges = false;

		// return the list of warnings
		return warnings;
	}

	/**
	 * Exports the match's details to a CSV file.
	 * 
	 * @param csvPath
	 *            The path to the file to be saved to.
	 * @param teamFormat
	 *            The method to be used to format teams in the results.
	 */
	public void exportMatch(String csvPath, ExportTeamFormat teamFormat)
			throws LogicException {

		// check we have a valid path
		if (csvPath == null) {
			throw new LogicException("Error: CSV file name undefined.");
		}

		// build the export data
		List<String[]> csvData = new ArrayList<String[]>();

		// include time data at top of file
		csvData.add(new String[] { "GENERATED AT: ", new Date().toString() });
		csvData.add(new String[] {});

		// match details
		csvData.add(new String[] { "MATCH DETAILS" });
		csvData.add(new String[] { "Name:", match.getName() });
		csvData.add(new String[] { "Number of teams:",
				String.valueOf(match.getTotalTeams()) });
		csvData.add(new String[] { "Number of heats:",
				String.valueOf(match.getTotalHeats()) });
		csvData.add(new String[] { "Number of jams:",
				String.valueOf(match.getTotalJams()) });
		int jamDuration = match.getJamDuration();
		csvData.add(new String[] { "Jam duration:",
				String.format("%d:%02d", jamDuration / 60, jamDuration % 60) });
		csvData.add(new String[] {});

		// team details
		csvData.add(new String[] { "TEAM DETAILS" });
		csvData.add(new String[] { "Number", "Name", "Abbreviated Name",
				"Background Colour (RGB/hex)", "Foreground Colour (RGB/hex)",
				"Points Adjustment" });
		for (Team team : match.getTeams()) {
			String bg = String.format("%08x", team.getBgColour().getRGB())
					.substring(2);
			String fg = String.format("%08x", team.getFgColour().getRGB())
					.substring(2);
			csvData.add(new String[] { String.valueOf(team.getNumber()),
					team.getName(), team.getAbbreviation(), "#" + bg, "#" + fg,
					String.valueOf(team.getPointsAdjustment()) });
		}
		csvData.add(new String[] {});

		// schedule/results
		csvData.add(new String[] { "JAM SCHEDULE/RESULTS" });
		int heatNum = 0;
		for (Heat heat : match.getHeats()) {
			heatNum++;
			csvData.add(new String[] { "Heat " + heatNum });
			for (Jam jam : heat.getJams()) {
				String score = jam.isCompleted() ? jam.getScore1() + " - "
						+ jam.getScore2() : "vs";
				csvData.add(new String[] { teamFormat.format(jam.getTeam1()),
						score, teamFormat.format(jam.getTeam2()) });
			}
		}
		csvData.add(new String[] {});

		// rankings table
		csvData.add(new String[] { "RANKINGS TABLE" });
		csvData.add(new String[] { "Position", "Team", "Wins", "Draws",
				"Losses", "Jams Led", "Points For", "Points Against",
				"Points Difference", "Match Score" });
		for (Ranking ranking : RankingControl.getInstance().getRankings()) {
			csvData.add(new String[] { ranking.getRank(),
					teamFormat.format(ranking.getTeam()),
					String.valueOf(ranking.getWins()),
					String.valueOf(ranking.getDraws()),
					String.valueOf(ranking.getLosses()),
					String.valueOf(ranking.getLedJams()),
					String.valueOf(ranking.getPointsFor()),
					String.valueOf(ranking.getPointsAgainst()),
					String.valueOf(ranking.getPointsDifference()),
					String.valueOf(ranking.getMatchScore()) });
		}
		csvData.add(new String[] {});

		// CSV data complete, write to disk
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(csvPath));
			writer.writeAll(csvData);
			writer.close();

			// check for IO errors
			if (writer.checkError()) {
				throw new LogicException(
						"Couldn't save file - an I/O error occurred.");
			}
		} catch (IOException error) {
			throw new LogicException("Couldn't save file - I/O error: " + error);
		}
	}

	/**
	 * Returns the currently-loaded match.
	 */
	public Match getMatch() {
		return match;
	}

	/**
	 * Returns whether or not the match has been changed since it was last
	 * saved.
	 */
	public boolean isUnsavedChanges() {
		return unsavedChanges;
	}

	/**
	 * Updates whether or not the match has been changed since it was last
	 * saved.
	 */
	public void setUnsavedChanges(boolean unsavedChanges) {
		this.unsavedChanges = unsavedChanges;
	}

	/**
	 * Returns the singleton instance of this class.
	 */
	public static MatchControl getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * A 'lazy-loaded' implementation of the Singleton pattern.
	 * 
	 * @see "http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom"
	 * @author Bill Pugh
	 */
	private static class SingletonHolder {

		/** The Singleton instance of this class. */
		private static final MatchControl INSTANCE = new MatchControl();
	}
}
