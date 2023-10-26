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
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import uk.co.lamsey.score5al.control.JamControl;
import uk.co.lamsey.score5al.control.MatchControl;
import uk.co.lamsey.score5al.control.Observer;
import uk.co.lamsey.score5al.model.Jam;
import uk.co.lamsey.score5al.model.Match;
import uk.co.lamsey.score5al.model.Team;

/**
 * Contains the schedule panel which can be displayed in the scoreboard.
 */
public class SchedulePanel {

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
	 * The table used to display the match schedule.
	 */
	private JTable scheduleTable;

	/**
	 * The renderer for team names.
	 */
	private TeamRenderer teamRenderer;

	/**
	 * The renderer for scores.
	 */
	private ScoreRenderer scoreRenderer;

	/**
	 * The label which shows the heat number.
	 */
	private GradientLabel heatNumber;

	/**
	 * Initialises the GUI panel so it is ready to be added to the UI.
	 */
	private SchedulePanel() {

		// create the labels which show the match data
		Color darkBlue = new Color(30, 40, 60);
		Color white = Color.WHITE;
		matchName = new GradientLabel(SwingConstants.LEFT, Font.BOLD);
		matchName.setBackground(darkBlue);
		matchName.setForeground(white);
		logo = new GradientLabel(SwingConstants.RIGHT, Font.PLAIN);
		logo.setIcon(Icons.LOGO);
		logo.setBackground(darkBlue);
		heatNumber = new GradientLabel(SwingConstants.CENTER, Font.BOLD);
		heatNumber.setBackground(darkBlue);
		heatNumber.setForeground(white);

		// set up the schedule table
		scheduleTable = new JTable(new ScheduleTableModel());
		scheduleTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		scheduleTable.getColumnModel().getColumn(1).setPreferredWidth(30);
		scheduleTable.getColumnModel().getColumn(2).setPreferredWidth(200);
		scheduleTable.setRowSelectionAllowed(false);
		scheduleTable.setFillsViewportHeight(true);

		// set up custom renderers
		teamRenderer = new TeamRenderer();
		scheduleTable.setDefaultRenderer(Jam.class, teamRenderer);
		scoreRenderer = new ScoreRenderer();
		scheduleTable.setDefaultRenderer(String.class, scoreRenderer);

		// centre the table headers
		((DefaultTableCellRenderer) scheduleTable.getTableHeader()
				.getDefaultRenderer())
				.setHorizontalAlignment(SwingConstants.CENTER);

		// scrollpane containing table
		JScrollPane scrollPane = new JScrollPane(scheduleTable);
		scrollPane.setPreferredSize(new Dimension(430, 75));

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

		// next row - schedule
		constraints.gridy++;
		constraints.gridwidth = 2;
		constraints.weightx = 1;
		constraints.weighty = 1;
		panel.add(scrollPane, constraints);

		// next row - heat number
		constraints.gridy++;
		constraints.weighty = 0.14;
		panel.add(heatNumber, constraints);

		// add listeners and force updates
		MatchControl matchControl = MatchControl.getInstance();
		MatchUpdater matchUpdater = new MatchUpdater();
		matchControl.addObserver(matchUpdater);
		JamUpdater jamUpdater = new JamUpdater();
		JamControl.getInstance().addObserver(jamUpdater);
		Match match = matchControl.getMatch();
		matchUpdater.update(match);
		jamUpdater.update(match.getJam(0));

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
		int scoreColSize = (int) (width * 0.12);
		int teamColSize = (int) (width * 0.44);
		scheduleTable.getColumnModel().getColumn(0)
				.setPreferredWidth(teamColSize);
		scheduleTable.getColumnModel().getColumn(1)
				.setPreferredWidth(scoreColSize);
		scheduleTable.getColumnModel().getColumn(2)
				.setPreferredWidth(teamColSize);

		// set row height proportionally to panel size
		int rowHeight = height / 18;
		scheduleTable.setRowHeight(rowHeight);

		// set font size proportionally to row height
		float fontSize = rowHeight * 0.70f;
		JTableHeader header = scheduleTable.getTableHeader();
		header.setFont(header.getFont().deriveFont(fontSize));
		teamRenderer.scaleFont(fontSize);
		scoreRenderer.scaleFont(fontSize);

		// scale label text
		float headerFooter = (float) Math.floor(height * 0.06);
		matchName.scaleFont(headerFooter);
		heatNumber.scaleFont(headerFooter);

		// scale the logo
		int logoSize = height / 8;
		logo.setIcon(new ImageIcon(Icons.LOGO.getImage().getScaledInstance(
				logoSize, logoSize, Image.SCALE_SMOOTH)));

		// calculate border sizes
		int border = Math.max(2, height / 75);
		int pad = border * 2;

		// apply borders
		Color black = Color.BLACK;
		panel.setBorder(BorderFactory.createLineBorder(black, border));
		Border padding = BorderFactory.createEmptyBorder(0, pad, 0, pad);
		matchName.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, border, 0, black),
				padding));
		logo.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, border, 0, black),
				padding));
		heatNumber.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(border, 0, 0, 0, black),
				padding));
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
	public static SchedulePanel getInstance() {
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
		private static final SchedulePanel INSTANCE = new SchedulePanel();
	}

	/**
	 * The renderer for teams in the schedule table.
	 */
	private static class TeamRenderer extends GradientLabel implements
			TableCellRenderer {

		/**
		 * Creates a new renderer for teams in the table.
		 */
		public TeamRenderer() {
			super(SwingConstants.CENTER, Font.BOLD);
		}

		/**
		 * Updates the label based on the selected team.
		 */
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object object, boolean isSelected, boolean hasFocus, int row,
				int column) {

			// check if we can do a Unicode star for the LJ status
			String star = getFont().canDisplay('\u2605') ? "\u2605" : "*";

			// show the team's name and LJ status
			Jam jam = (Jam) object;
			Team leadJammer = jam.getLeadJammer();
			if (column == 0) {
				showTeam(jam.getTeam1(), true, true);
				setHorizontalAlignment(RIGHT);
				if (jam.isCompleted() && leadJammer != null
						&& leadJammer == jam.getTeam1()) {
					setText(star + " " + getText());
				}
			} else {
				showTeam(jam.getTeam2(), true, true);
				setHorizontalAlignment(LEFT);
				if (jam.isCompleted() && leadJammer != null
						&& leadJammer == jam.getTeam2()) {
					setText(getText() + " " + star);
				}
			}

			return this;
		}
	}

	/**
	 * The renderer for scores in the schedule table.
	 */
	private static class ScoreRenderer extends GradientLabel implements
			TableCellRenderer {

		/**
		 * Creates a new renderer for scores in the table.
		 */
		public ScoreRenderer() {
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
	 * Updates the panel with details of the current match.
	 */
	private class MatchUpdater implements Observer<Match> {
		public void update(Match match) {
			matchName.setText(match.getName());
		}
	}

	/**
	 * Updates the panel with details of the current heat.
	 */
	private class JamUpdater implements Observer<Jam> {
		public void update(Jam jam) {

			// update heat number
			JamControl jc = JamControl.getInstance();
			heatNumber.setText("Heat " + (jc.getHeatIndex() + 1));
		}
	}
}
