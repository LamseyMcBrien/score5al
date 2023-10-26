package uk.co.lamsey.score5al.model;


/**
 * A container for statistics on the distribution of teams in a match.
 */
public class DistributionStats {

	/**
	 * A string-based graph for the moment until I make it into a real one.
	 */
	private String distributionGraph;

	/**
	 * Calculates and stores the distribution stats for the passed match.
	 */
	public DistributionStats(Match match) {

		// headings
		StringBuilder sb = new StringBuilder("Heat    |");
		for (int heat = 0; heat < match.getHeats().size(); heat++) {
			sb.append(String.format(" %2d", heat));
		}
		sb.append("\n--------+");
		for (int heat = 0; heat < match.getHeats().size(); heat++) {
			sb.append("---");
		}

		// one row per team showing number of completed jams in each heat
		for (Team team : match.getTeams()) {
			sb.append(String.format("\nTeam %2d |", team.getNumber()));
			int count = 0;
			for (Heat heat : match.getHeats()) {
				boolean inJam = false;
				for (Jam jam : heat.getJams()) {
					if (jam.getTeam1() == team || jam.getTeam2() == team) {
						count++;
						inJam = true;
					}
				}
				sb.append(inJam ? String.format(" %2d", count) : "   ");
			}
		}
		distributionGraph = sb.toString();
	}

	/**
	 * Returns a String-based graph of the jam distribution.
	 */
	public String getDistributionGraph() {
		return distributionGraph;
	}
}
