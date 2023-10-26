package uk.co.lamsey.score5al.ui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import uk.co.lamsey.score5al.control.Observer;
import uk.co.lamsey.score5al.control.RankingControl;
import uk.co.lamsey.score5al.model.Ranking;
import uk.co.lamsey.score5al.model.Team;

/**
 * Displays the rankings table for the teams in the current match.
 */
public class RankingTableModel extends AbstractTableModel {

	/**
	 * The list of team rankings being displayed.
	 */
	private List<Ranking> rankings;

	/**
	 * Creates a new table model which updates itself whenever the rankings data
	 * changes.
	 */
	public RankingTableModel() {
		RankingControl rankingControl = RankingControl.getInstance();
		rankings = rankingControl.getRankings();
		rankingControl.addObserver(new RankingListener());
	}

	/**
	 * Returns 8 (rank, team, W, D, L, LJ, PD, Pts).
	 */
	@Override
	public int getColumnCount() {
		return 8;
	}

	/**
	 * Returns the number of teams in the match.
	 */
	@Override
	public int getRowCount() {
		return rankings.size();
	}

	/**
	 * Returns the table data for the given row and column. Row determines the
	 * team being queried. Column determines the info being displayed: 0 =
	 * ranking; 1 = team; 2 = wins; 3 = draws; 4 = losses; 5 = LJ; 6 = points
	 * difference; 7 = match points.
	 */
	@Override
	public Object getValueAt(int row, int column) {
		switch (column) {
		case 0: // ranking
			return rankings.get(row).getRank();
		case 1: // team
			return rankings.get(row).getTeam();
		case 2: // wins
			return rankings.get(row).getWins();
		case 3: // draws
			return rankings.get(row).getDraws();
		case 4: // losses
			return rankings.get(row).getLosses();
		case 5: // LJ
			return rankings.get(row).getLedJams();
		case 6: // difference
			return rankings.get(row).getPointsDifference();
		case 7: // match points
			return rankings.get(row).getMatchScore();
		default:
			throw new RuntimeException("Invalid column requested: " + column);
		}
	}

	/**
	 * Returns the class of the specified column.
	 */
	@Override
	public Class<?> getColumnClass(int column) {
		switch (column) {
		case 0: // ranking
			return String.class;
		case 1: // team
			return Team.class;
		case 2: // wins
			return Integer.class;
		case 3: // draws
			return Integer.class;
		case 4: // losses
			return Integer.class;
		case 5: // LJ
			return Integer.class;
		case 6: // difference
			return Integer.class;
		case 7: // match points
			return Integer.class;
		default:
			throw new RuntimeException("Invalid column requested: " + column);
		}
	}

	/**
	 * Returns the name of the specified column: 0 = ranking; 1 = team; 2 =
	 * wins; 3 = draws; 4 = draws; 5 = losses; 6 = points difference; 7 = match
	 * points.
	 */
	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Pos";
		case 1:
			return "Team";
		case 2:
			return "W";
		case 3:
			return "D";
		case 4:
			return "L";
		case 5:
			return "LJ";
		case 6:
			return "PD";
		case 7:
			return "Pts";
		default:
			throw new RuntimeException("Invalid column requested: " + column);
		}
	}

	/**
	 * Handles table update events when the rankings in the current match are
	 * updated.
	 */
	private class RankingListener implements Observer<List<Ranking>> {

		/**
		 * Called whenever the rankings in the current match are updated.
		 */
		@Override
		public void update(List<Ranking> newRankings) {
			// store the new data and redraw
			rankings = newRankings;
			fireTableDataChanged();
		}
	}
}
