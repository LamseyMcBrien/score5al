package uk.co.lamsey.score5al.ui;

import javax.swing.table.AbstractTableModel;

import uk.co.lamsey.score5al.control.MatchControl;
import uk.co.lamsey.score5al.control.Observer;
import uk.co.lamsey.score5al.model.Match;
import uk.co.lamsey.score5al.model.Team;

/**
 * Displays the teams in the current match.
 */
public class EditTeamTableModel extends AbstractTableModel {

	/**
	 * The Match whose teams are being displayed.
	 */
	private Match match;

	/**
	 * The number of teams in the match being displayed (used to track whether
	 * or not rows have been inserted/deleted).
	 */
	private int numTeams;

	/**
	 * Creates a new table model which is always backed by the current match.
	 */
	public EditTeamTableModel() {
		MatchControl matchControl = MatchControl.getInstance();
		match = matchControl.getMatch();
		numTeams = match.getTotalTeams();
		matchControl.addObserver(new MatchListener());
	}

	/**
	 * Returns 4 (number, name, abbreviation, points adjustment).
	 */
	@Override
	public int getColumnCount() {
		return 4;
	}

	/**
	 * Returns the number of teams in the match.
	 */
	@Override
	public int getRowCount() {
		return numTeams;
	}

	/**
	 * Returns the table data for the given row and column. Row determines the
	 * team being queried. Column determines the info being displayed: 0 = team
	 * number; 1 = team name; 2 = team abbreviation; 3 = points adjustment.
	 */
	@Override
	public Object getValueAt(int row, int column) {
		switch (column) {
		case 0:
			return match.getTeams().get(row);
		case 1:
			return match.getTeams().get(row).getName();
		case 2:
			return match.getTeams().get(row).getAbbreviation();
		case 3:
			return match.getTeams().get(row).getPointsAdjustment();
		default:
			throw new RuntimeException("Invalid column requested: " + column);
		}
	}

	/**
	 * Returns the class of the specified column.
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0) {
			return Team.class;
		} else if (columnIndex == 3) {
			return Integer.class;
		} else {
			return String.class;
		}
	}

	/**
	 * Returns the name of the specified column: 0 = team number; 1 = team name;
	 * 2 = team abbreviation; 3 = points adjustment.
	 */
	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "#";
		case 1:
			return "Name";
		case 2:
			return "Abbreviation";
		case 3:
			return "Pts Adj.";
		default:
			throw new RuntimeException("Invalid column requested: " + column);
		}
	}

	/**
	 * Handles table update events when the current match is edited.
	 */
	private class MatchListener implements Observer<Match> {

		/**
		 * Called whenever the current match is updated.
		 */
		@Override
		public void update(Match newMatch) {
			// check if we're dealing with the same match
			if (match != newMatch) {
				// not the same match - perform full table refresh
				match = newMatch;
				numTeams = match.getTotalTeams();
				fireTableDataChanged();
			} else {
				// same match - check if number of teams has changed
				int oldNumTeams = numTeams;
				int newNumTeams = newMatch.getTotalTeams();

				// update rows rather than full refresh (preserves selection)
				numTeams = newNumTeams;
				fireTableRowsUpdated(0, newNumTeams - 1);
				if (newNumTeams > oldNumTeams) {
					fireTableRowsInserted(oldNumTeams, newNumTeams - 1);
				} else if (newNumTeams < oldNumTeams) {
					fireTableRowsDeleted(newNumTeams, oldNumTeams - 1);
				}
			}
		}
	}
}
