package uk.co.lamsey.score5al.model;

/**
 * A jam is a two-minute game between two teams. Each team is awarded one or
 * more points during the jam, and one team may be awarded 'lead jammer' status.
 */
public class Jam {

	/**
	 * The first team in the jam.
	 */
	private Team team1;

	/**
	 * The second team in the jam.
	 */
	private Team team2;

	/**
	 * The team which got lead jammer status (null if neither team has lead
	 * jammer status).
	 */
	private Team leadJammer;

	/**
	 * Team 1's score in this jam.
	 */
	private int score1;

	/**
	 * Team 2's score in this jam.
	 */
	private int score2;

	/**
	 * The time remaining for this jam in seconds.
	 */
	private int timeRemaining;

	/**
	 * Creates a new Jam with no teams set and the specified duration in
	 * seconds.
	 */
	public Jam(int duration) {
		team1 = null;
		team2 = null;
		leadJammer = null;
		score1 = 0;
		score2 = 0;
		timeRemaining = duration;
	}

	/**
	 * Returns the first team in the jam.
	 */
	public Team getTeam1() {
		return team1;
	}

	/**
	 * Updates the first team in the jam.
	 */
	public void setTeam1(Team team1) {
		this.team1 = team1;
	}

	/**
	 * Returns the second team in the jam.
	 */
	public Team getTeam2() {
		return team2;
	}

	/**
	 * Updates the second team in the jam.
	 */
	public void setTeam2(Team team2) {
		this.team2 = team2;
	}

	/**
	 * Returns the team which got lead jammer status (null if neither team has
	 * lead jammer status).
	 */
	public Team getLeadJammer() {
		return leadJammer;
	}

	/**
	 * Updates the team which got lead jammer status (null if neither team has
	 * lead jammer status).
	 */
	public void setLeadJammer(Team leadJammer) {
		this.leadJammer = leadJammer;
	}

	/**
	 * Returns Team 1's score in this jam.
	 */
	public int getScore1() {
		return score1;
	}

	/**
	 * Updates Team 1's score in this jam.
	 */
	public void setScore1(int points) {
		score1 = points;
	}

	/**
	 * Returns Team 2's score in this jam.
	 */
	public int getScore2() {
		return score2;
	}

	/**
	 * Updates Team 2's score in this jam.
	 */
	public void setScore2(int points) {
		score2 = points;
	}

	/**
	 * Returns the time remaining for this jam in seconds.
	 */
	public int getTimeRemaining() {
		return timeRemaining;
	}

	/**
	 * Updates the time remaining for this jam in seconds.
	 */
	public void setTimeRemaining(int timeRemaining) {
		this.timeRemaining = timeRemaining;
	}

	/**
	 * Returns whether or not this jam has been completed.
	 */
	public boolean isCompleted() {
		return timeRemaining == 0;
	}
}
