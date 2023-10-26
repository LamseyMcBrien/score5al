package uk.co.lamsey.score5al.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.lamsey.score5al.model.Heat;
import uk.co.lamsey.score5al.model.Jam;
import uk.co.lamsey.score5al.model.Match;
import uk.co.lamsey.score5al.model.Ranking;
import uk.co.lamsey.score5al.model.Team;

/**
 * Handles ranking recalculations and updates.
 */
public class RankingControl extends Observable<List<Ranking>> {

	/**
	 * A sorted list of rankings for all of the teams in the match.
	 */
	private List<Ranking> rankings;

	/**
	 * Initialises the ranking data and sets up listeners to process updates.
	 */
	private RankingControl() {

		// initialise ranking data
		MatchControl matchControl = MatchControl.getInstance();
		MatchObserver matchObserver = new MatchObserver();
		matchObserver.update(matchControl.getMatch());

		// register listeners for updates
		matchControl.addObserver(matchObserver);
		JamControl.getInstance().addObserver(new JamObserver());
	}

	/**
	 * Recalculates the rankings for the passed teams and notifies observers of
	 * the change.
	 */
	private void recalculate(Set<Team> teamsToUpdate) {

		// reset scores for all teams
		for (Team team : teamsToUpdate) {
			team.getRanking().reset();
		}

		// go through all relevant jams and reprocess scores
		for (Heat heat : MatchControl.getInstance().getMatch().getHeats()) {
			for (Jam jam : heat.getJams()) {

				// only process this jam if it's been completed
				if (jam.isCompleted()) {

					// if team 1 is in the update list, process their score
					Team team1 = jam.getTeam1();
					if (teamsToUpdate.contains(team1)) {
						team1.getRanking().processJamScore(jam.getScore1(),
								jam.getScore2(), jam.getLeadJammer() == team1);
					}

					// if team 2 is in the update list, process their score
					Team team2 = jam.getTeam2();
					if (teamsToUpdate.contains(team2)) {
						team2.getRanking().processJamScore(jam.getScore2(),
								jam.getScore1(), jam.getLeadJammer() == team2);
					}
				}
			}
		}

		// sort the rankings and update team ranks
		Collections.sort(rankings);
		for (int row = 0; row < rankings.size(); row++) {
			// rank is equal to position in list
			int rank = row;
			Ranking ranking = rankings.get(row);
			// if we're level with the team(s) above us, increase rank
			while (rank > 0 && ranking.compareTo(rankings.get(rank - 1)) == 0) {
				rank--;
			}
			// actual rank is 1-indexed, not 0-indexed
			String realRank = String.valueOf(rank + 1);
			// if we're level with the team below us, add an '='
			if (rank < rankings.size() - 1
					&& ranking.compareTo(rankings.get(rank + 1)) == 0) {
				realRank += "=";
			}
			rankings.get(row).setRank(realRank);
		}

		// update listeners
		notifyObservers(rankings);
	}

	/**
	 * Returns a sorted list of rankings for all of the teams in the match.
	 */
	public List<Ranking> getRankings() {
		return rankings;
	}

	/**
	 * Returns the singleton instance of this class.
	 */
	public static RankingControl getInstance() {
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
		private static final RankingControl INSTANCE = new RankingControl();
	}

	/**
	 * Listens for changes in the overall match and updates accordingly.
	 */
	private class MatchObserver implements Observer<Match> {
		public void update(Match match) {
			// reinitialise the list of rankings, there may be more teams
			rankings = new ArrayList<Ranking>();
			for (Team team : match.getTeams()) {
				rankings.add(team.getRanking());
			}
			recalculate(new HashSet<Team>(match.getTeams()));
		}
	}

	/**
	 * Listens for changes in the currently-selected jam and updates
	 * accordingly.
	 */
	private class JamObserver implements Observer<Jam> {
		public void update(Jam jam) {
			Set<Team> teams = new HashSet<Team>();
			if (jam.getTeam1() != null) {
				teams.add(jam.getTeam1());
			}
			if (jam.getTeam2() != null) {
				teams.add(jam.getTeam2());
			}
			if (!teams.isEmpty()) {
				recalculate(teams);
			}
		}
	}
}
