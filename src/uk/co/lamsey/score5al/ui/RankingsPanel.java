package uk.co.lamsey.score5al.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import uk.co.lamsey.score5al.control.MatchControl;
import uk.co.lamsey.score5al.control.Observer;
import uk.co.lamsey.score5al.model.Match;
import uk.co.lamsey.score5al.model.Team;

/**
 * Contains the rankings table which can be displayed in the scoreboard.
 */
public class RankingsPanel {

	/**
	 * The GUI panel represented by this class.
	 */
	private JPanel panel;

	/**
	 * The label which shows the match name.
	 */
	private GradientLabel matchName;

	/**
	 * The label which displays the Sur5al logo.
	 */
	private JLabel logo;

	/**
	 * The table used to display the rankings.
	 */
	private JTable rankingTable;

	/**
	 * The renderer for ranks.
	 */
	private RankRenderer rankRenderer;

	/**
	 * The renderer for team names.
	 */
	private TeamRenderer teamRenderer;

	/**
	 * The renderer for numbers.
	 */
	private NumberRenderer numberRenderer;

	/**
	 * Initialises the GUI panel so it is ready to be added to the UI.
	 */
	private RankingsPanel() {

		// create the labels which show the match data
		Color darkBlue = new Color(30, 40, 60);
		matchName = new GradientLabel(SwingConstants.LEFT, Font.BOLD);
		matchName.setBackground(darkBlue);
		matchName.setForeground(Color.WHITE);
		logo = new GradientLabel(SwingConstants.RIGHT, Font.PLAIN);
		logo.setIcon(Icons.LOGO);
		logo.setBackground(darkBlue);

		// set up the rankings table
		rankingTable = new JTable(new RankingTableModel());
		rankingTable.getColumnModel().getColumn(0).setPreferredWidth(25);
		rankingTable.getColumnModel().getColumn(1).setPreferredWidth(250);
		for (int col = 1; col < 8; col++) {
			rankingTable.getColumnModel().getColumn(col).setPreferredWidth(20);
		}
		rankingTable.setRowSelectionAllowed(false);
		rankingTable.setFillsViewportHeight(true);

		// set up custom renderers
		rankRenderer = new RankRenderer();
		rankingTable.setDefaultRenderer(String.class, rankRenderer);
		teamRenderer = new TeamRenderer();
		rankingTable.setDefaultRenderer(Team.class, teamRenderer);
		numberRenderer = new NumberRenderer();
		rankingTable.setDefaultRenderer(Integer.class, numberRenderer);

		// centre the table headers
		((DefaultTableCellRenderer) rankingTable.getTableHeader()
				.getDefaultRenderer())
				.setHorizontalAlignment(SwingConstants.CENTER);

		// scrollpane containing table
		JScrollPane scrollPane = new JScrollPane(rankingTable);
		scrollPane.setPreferredSize(new Dimension(430, 250));

		// create the panel and layout manager
		panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.BLACK);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;

		// first row - match name and logo
		constraints.gridy = 0;
		constraints.weighty = 0;
		constraints.weightx = 1;
		panel.add(matchName, constraints);
		constraints.weightx = 0;
		panel.add(logo, constraints);

		// next row - table
		constraints.gridy++;
		constraints.gridwidth = 2;
		constraints.weightx = 1;
		constraints.weighty = 1;
		panel.add(scrollPane, constraints);

		// add match listener and force update
		MatchControl matchControl = MatchControl.getInstance();
		MatchUpdater matchUpdater = new MatchUpdater();
		matchControl.addObserver(matchUpdater);
		matchUpdater.update(matchControl.getMatch());

		// add scaler
		panel.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent event) {
				scale();
			}
		});
	}

	/**
	 * Adjusts the scale of the scoreboard in response to resize events.
	 */
	private void scale() {
		int width = panel.getWidth();
		int height = panel.getHeight();

		// resize table columns
		int posCol = (int) (width * 0.08);
		int teamCol = (int) (width * 0.50);
		int gameCol = (int) (width * 0.06);
		int ptsCol = (int) (width * 0.09);
		rankingTable.getColumnModel().getColumn(0).setPreferredWidth(posCol);
		rankingTable.getColumnModel().getColumn(1).setPreferredWidth(teamCol);
		rankingTable.getColumnModel().getColumn(2).setPreferredWidth(gameCol);
		rankingTable.getColumnModel().getColumn(3).setPreferredWidth(gameCol);
		rankingTable.getColumnModel().getColumn(4).setPreferredWidth(gameCol);
		rankingTable.getColumnModel().getColumn(5).setPreferredWidth(gameCol);
		rankingTable.getColumnModel().getColumn(6).setPreferredWidth(ptsCol);
		rankingTable.getColumnModel().getColumn(7).setPreferredWidth(ptsCol);

		// set row height proportionally to panel height
		int rowHeight = height / 18;
		rankingTable.setRowHeight(rowHeight);

		// set font size proportionally to row height
		float fontSize = rowHeight * 0.75f;
		JTableHeader header = rankingTable.getTableHeader();
		header.setFont(header.getFont().deriveFont(fontSize));
		rankRenderer.scaleFont(fontSize);
		teamRenderer.scaleFont(fontSize);
		numberRenderer.scaleFont(fontSize);
		matchName.scaleFont(fontSize);

		// scale the logo
		int logoSize = height / 16;
		logo.setIcon(new ImageIcon(Icons.LOGO.getImage().getScaledInstance(
				logoSize, logoSize, Image.SCALE_SMOOTH)));

		// calculate border sizes
		int border = Math.max(2, height / 75);
		int pad = border * 2;

		// apply borders
		Color black = Color.BLACK;
		panel.setBorder(BorderFactory.createLineBorder(black, border));
		matchName.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, border / 2, 0, black),
				BorderFactory.createEmptyBorder(0, pad, 0, pad)));
		logo.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, border / 2, 0, black),
				BorderFactory.createEmptyBorder(0, 0, 0, pad)));
	}

	/**
	 * Returns the GUI panel represented by this class.
	 */
	public JPanel getPanel() {
		return panel;
	}

	/**
	 * Returns the singleton instance of this class.
	 */
	public static RankingsPanel getInstance() {
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
		private static final RankingsPanel INSTANCE = new RankingsPanel();
	}

	/**
	 * The renderer for ranks in the rankings table.
	 */
	private static class RankRenderer extends GradientLabel implements
			TableCellRenderer {

		/**
		 * Creates a new renderer for ranks in the table.
		 */
		public RankRenderer() {
			super(SwingConstants.CENTER, Font.PLAIN);
			setBackground(Color.WHITE);
		}

		/**
		 * Updates the label with the passed string.
		 */
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object object, boolean isSelected, boolean hasFocus, int row,
				int column) {
			setText(object.toString());
			return this;
		}
	}

	/**
	 * The renderer for teams in the rankings table.
	 */
	private static class TeamRenderer extends GradientLabel implements
			TableCellRenderer {

		/**
		 * Creates a new renderer for teams in the table.
		 */
		public TeamRenderer() {
			super(SwingConstants.LEFT, Font.BOLD);
		}

		/**
		 * Updates the label based on the selected team.
		 */
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object object, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Team team = (Team) object;
			showTeam(team, true, true);
			return this;
		}
	}

	/**
	 * The renderer for numbers in the rankings table.
	 */
	private static class NumberRenderer extends GradientLabel implements
			TableCellRenderer {

		/**
		 * Creates a new renderer for numbers in the table.
		 */
		public NumberRenderer() {
			super(SwingConstants.RIGHT, Font.PLAIN);
			setBackground(Color.WHITE);
		}

		/**
		 * Updates the label with the passed number.
		 */
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object object, boolean isSelected, boolean hasFocus, int row,
				int column) {

			// use bold for match score
			setFont(getFont().deriveFont(column == 7 ? Font.BOLD : Font.PLAIN));

			setText(object.toString());
			return this;
		}
	}

	/**
	 * Updates the panel with details of the current match.
	 */
	private class MatchUpdater implements Observer<Match> {
		public void update(Match match) {
			matchName.setText(match.getName());
		}
	}
}
