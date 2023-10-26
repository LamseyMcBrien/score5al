package uk.co.lamsey.score5al.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.table.AbstractTableModel;

import uk.co.lamsey.score5al.control.MatchControl;
import uk.co.lamsey.score5al.control.Observer;
import uk.co.lamsey.score5al.model.Heat;
import uk.co.lamsey.score5al.model.Jam;
import uk.co.lamsey.score5al.model.Match;
import uk.co.lamsey.score5al.model.Team;

/**
 * Stores the teams assigned to each jam in the currently-selected heat.
 */
public class EditHeatTableModel extends AbstractTableModel {

	/**
	 * The number of the heat being displayed.
	 */
	private int heatNum;

	/**
	 * The list containing the first teams assigned to each jam in this heat.
	 */
	private List<Team> team1List;

	/**
	 * The list containing the second teams assigned to each jam in this heat.
	 */
	private List<Team> team2List;

	/**
	 * Creates a new editable model for heat 0 initially.
	 */
	public EditHeatTableModel() {
		showHeat(0);
		MatchControl.getInstance().addObserver(new MatchListener());
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

		team1List = new ArrayList<Team>();
		team2List = new ArrayList<Team>();

		List<Heat> heats = match.getHeats();
		if (heatNum < heats.size()) {
			for (Jam jam : heats.get(heatNum).getJams()) {
				team1List.add(jam.getTeam1());
				team2List.add(jam.getTeam2());
			}
		}
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
		return team1List.size();
	}

	/**
	 * Returns the table data for the given row and column. Row determines the
	 * team being queried. Column determines the info being displayed: 0 =
	 * team1; 1 = "vs"; 2 = team2.
	 */
	@Override
	public Object getValueAt(int row, int column) {
		switch (column) {
		case 0:
			return team1List.get(row);
		case 1:
			return new JLabel("vs", null, JLabel.CENTER);
		case 2:
			return team2List.get(row);
		default:
			throw new RuntimeException("Invalid column requested: " + column);
		}
	}

	/**
	 * Updates the table data for the given row and column.
	 */
	@Override
	public void setValueAt(Object team, int row, int column) {
		if (column == 0) {
			team1List.set(row, (Team) team);
		} else if (column == 2) {
			team2List.set(row, (Team) team);
		}
	}

	/**
	 * Returns the class of the specified column.
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 1) {
			return JLabel.class;
		} else {
			return Team.class;
		}
	}

	/**
	 * Returns the name of the specified column: 0 = team 1; 1 = ""; 2 = team 2.
	 */
	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Team 1";
		case 1:
			return "";
		case 2:
			return "Team 2";
		default:
			throw new RuntimeException("Invalid column requested: " + column);
		}
	}

	/**
	 * Returns true for columns 0 and 2.
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex != 1;
	}

	/**
	 * Returns the number of the heat being displayed.
	 */
	public int getHeatNo() {
		return heatNum;
	}

	/**
	 * Returns the list containing the first teams assigned to each jam in this
	 * heat.
	 */
	public List<Team> getTeam1List() {
		return team1List;
	}

	/**
	 * Returns the list containing the second teams assigned to each jam in this
	 * heat.
	 */
	public List<Team> getTeam2List() {
		return team2List;
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
			reload(match);
		}
	}
}
