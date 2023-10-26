package uk.co.lamsey.score5al.ui;

import java.awt.Font;
import java.util.List;

import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;

import uk.co.lamsey.score5al.control.JamControl;
import uk.co.lamsey.score5al.control.MatchControl;
import uk.co.lamsey.score5al.control.Observer;
import uk.co.lamsey.score5al.model.Jam;
import uk.co.lamsey.score5al.model.Match;

/**
 * Displays the jams in the currently-selected heat.
 */
public class ScheduleTableModel extends AbstractTableModel {

	/**
	 * The number of the heat being displayed.
	 */
	private int heatNum;

	/**
	 * The list of jams for the heat being displayed.
	 */
	private List<Jam> jamList;

	/**
	 * A spacer character used to justify scores - ideally the Unicode
	 * number-sized space character, but otherwise the normal space character.
	 */
	private String spacer;

	/**
	 * Creates a new table model which initially shows the jams in heat 0.
	 */
	public ScheduleTableModel() {
		showHeat(0);
		MatchControl.getInstance().addObserver(new MatchListener());
		JamControl.getInstance().addObserver(new JamListener());

		// work out which spacer to use
		spacer = new GradientLabel(SwingConstants.LEFT, Font.PLAIN).getFont()
				.canDisplay('\u2007') ? "\u2007" : " ";
	}

	/**
	 * Makes the table show the specified heat.
	 */
	public void showHeat(int newHeatNum) {
		heatNum = newHeatNum;
		reload(MatchControl.getInstance().getMatch());
	}

	/**
	 * Reloads the heat data from the passed match.
	 */
	private void reload(Match match) {
		jamList = match.getHeats().get(heatNum).getJams();
		fireTableDataChanged();
	}

	/**
	 * Returns 3 (team1, vs, team2).
	 */
	@Override
	public int getColumnCount() {
		return 3;
	}

	/**
	 * Returns the number of jams in the heat.
	 */
	@Override
	public int getRowCount() {
		return jamList.size();
	}

	/**
	 * Returns the table data for the given row and column. The jam is returned
	 * for columns 0 and 2 so the renderer can extract team and LJ data. The
	 * score (column 1) is returned as a string.
	 */
	@Override
	public Object getValueAt(int row, int column) {
		Jam jam = jamList.get(row);
		if (column == 1) {
			if (jam.isCompleted()) {

				// return jam score - pad so that the dash is centered
				String score1 = String.valueOf(jam.getScore1());
				String score2 = String.valueOf(jam.getScore2());
				while (score1.length() > score2.length()) {
					score2 += spacer;
				}
				while (score2.length() > score1.length()) {
					score1 = spacer + score1;
				}
				return score1 + " - " + score2;
			} else {
				// jam not completed - return generic 'vs' string
				return "vs";
			}
		} else {
			return jam;
		}
	}

	/**
	 * Returns the class of the specified column.
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 1) {
			return String.class;
		} else {
			return Jam.class;
		}
	}

	/**
	 * Returns the name of the specified column: 0 = team; 1 = score; 2 = team.
	 */
	@Override
	public String getColumnName(int column) {
		if (column == 1) {
			return "Score";
		} else {
			return "Team";
		}
	}

	/**
	 * Returns the number of the heat being displayed.
	 */
	public int getHeatNo() {
		return heatNum;
	}

	/**
	 * Handles table update events when the current match is edited.
	 */
	private class MatchListener implements Observer<Match> {

		/**
		 * Called whenever the current match is updated.
		 */
		@Override
		public void update(Match match) {
			if (heatNum >= match.getTotalHeats()) {
				heatNum = 0;
			}
			reload(match);
		}
	}

	/**
	 * Handles table update events when a jam is edited.
	 */
	private class JamListener implements Observer<Jam> {

		/**
		 * Called whenever a jam is updated.
		 */
		@Override
		public void update(Jam jam) {
			int index = jamList.indexOf(jam);
			if (index != -1) {
				// jam is in current heat
				fireTableRowsUpdated(index, index);
			} else {
				// jam is not in current heat, reload
				heatNum = JamControl.getInstance().getHeatIndex();
				reload(MatchControl.getInstance().getMatch());
			}
		}
	}
}
