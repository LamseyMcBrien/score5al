package uk.co.lamsey.score5al.model;

/**
 * Represents a team's match scores, to be displayed in the rankings table.
 */
public class Ranking implements Comparable<Ranking> {

	/**
	 * The number of match points awarded for a win.
	 */
	public static final int WIN_POINTS = 5;

	/**
	 * The number of match points awarded for a draw.
	 */
	public static final int DRAW_POINTS = 2;

	/**
	 * The number of match points awarded for lead jammer status.
	 */
	public static final int LJ_POINTS = 1;

	/**
	 * The team to whom this ranking relates.
	 */
	private Team team;

	/**
	 * The team's current rank in the match.
	 */
	private String rank;

	/**
	 * The number of jams this team has won.
	 */
	private int wins;

	/**
	 * The number of jams this team has drawn.
	 */
	private int draws;

	/**
	 * The number of jams this team has lost.
	 */
	private int losses;

	/**
	 * The number of jams in which this team has had lead jammer status.
	 */
	private int leadJams;

	/**
	 * The total number of jam points scored by this team.
	 */
	private int pointsFor;

	/**
	 * The total number of jam points scored against this team.
	 */
	private int pointsAgainst;

	/**
	 * Creates a new Ranking for the specified team with no score.
	 */
	public Ranking(Team team) {
		this.team = team;
		reset();
	}

	/**
	 * Adjusts this team's ranking data according to the passed jam scores.
	 * 
	 * @param pointsScored
	 *            The points scored by this team in this jam.
	 * @param pointsLost
	 *            The points scored against this team in this jam.
	 * @param isLeadJammer
	 *            Whether or not the team had lead jammer status in this jam.
	 */
	public void processJamScore(int pointsScored, int pointsLost,
			boolean isLeadJammer) {

		// adjust points totals
		pointsFor += pointsScored;
		pointsAgainst += pointsLost;

		// record win/loss/draw
		if (pointsScored > pointsLost) {
			wins++;
		} else if (pointsScored < pointsLost) {
			losses++;
		} else {
			draws++;
		}

		// record LJ
		if (isLeadJammer) {
			leadJams++;
		}
	}

	/**
	 * Resets this team's scores.
	 */
	public void reset() {
		rank = "";
		wins = 0;
		draws = 0;
		losses = 0;
		leadJams = 0;
		pointsFor = 0;
		pointsAgainst = 0;
	}

	/**
	 * Compares first by match score, then by points difference, then by games
	 * won, then by number of jams lead. Natural ordering is reversed so that
	 * teams with more points appear before teams with less points.
	 */
	public int compareTo(Ranking other) {

		// match score
		int difference = other.getMatchScore() - getMatchScore();
		if (difference != 0) {
			return difference;
		}

		// points difference
		difference = other.getPointsDifference() - getPointsDifference();
		if (difference != 0) {
			return difference;
		}

		// games won
		difference = other.getWins() - getWins();
		if (difference != 0) {
			return difference;
		}

		// jams led
		return other.getLedJams() - getLedJams();
	}

	/**
	 * Returns the difference between points for and against this team.
	 */
	public int getPointsDifference() {
		return pointsFor - pointsAgainst;
	}

	/**
	 * Returns the team's overall match score.
	 */
	public int getMatchScore() {
		return (wins * WIN_POINTS) + (draws * DRAW_POINTS)
				+ (leadJams * LJ_POINTS) + team.getPointsAdjustment();
	}

	// simple getters/setters below this point

	/**
	 * Returns the team to whom this ranking relates.
	 */
	public Team getTeam() {
		return team;
	}

	/**
	 * Returns the team's current rank in the match.
	 */
	public String getRank() {
		return rank;
	}

	/**
	 * Updates the team's current rank in the match.
	 */
	public void setRank(String rank) {
		this.rank = rank;
	}

	/**
	 * Returns the number of jams this team has won.
	 */
	public int getWins() {
		return wins;
	}

	/**
	 * Returns the number of jams this team has drawn.
	 */
	public int getDraws() {
		return draws;
	}

	/**
	 * Returns the number of jams this team has lost.
	 */
	public int getLosses() {
		return losses;
	}

	/**
	 * Returns the number of jams in which this team has had lead jammer status.
	 */
	public int getLedJams() {
		return leadJams;
	}

	/**
	 * Returns the total number of jam points scored by this team.
	 */
	public int getPointsFor() {
		return pointsFor;
	}

	/**
	 * Returns the total number of jam points scored against this team.
	 */
	public int getPointsAgainst() {
		return pointsAgainst;
	}
}
