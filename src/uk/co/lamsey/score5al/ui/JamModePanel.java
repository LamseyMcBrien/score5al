package uk.co.lamsey.score5al.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import uk.co.lamsey.score5al.control.JamControl;
import uk.co.lamsey.score5al.control.MatchControl;
import uk.co.lamsey.score5al.control.Observer;
import uk.co.lamsey.score5al.model.Jam;
import uk.co.lamsey.score5al.model.Match;
import uk.co.lamsey.score5al.model.Team;

/**
 * Represents the panel which is shown when the scoreboard is in Jam Mode.
 */
public class JamModePanel {

	/**
	 * The GUI panel which should be added to the scoreboard window.
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
	 * The label which shows team 1's name.
	 */
	private GradientLabel team1Name;

	/**
	 * The label which shows team 2's name.
	 */
	private GradientLabel team2Name;

	/**
	 * The label which shows team 1's score.
	 */
	private GradientLabel team1Score;

	/**
	 * The label which shows team 2's score.
	 */
	private GradientLabel team2Score;

	/**
	 * The label which shows the jam timer.
	 */
	private GradientLabel timer;

	/**
	 * The label which shows the heat/jam number.
	 */
	private GradientLabel jamNumber;

	/**
	 * Sets up the panel so it is ready to be added to the UI.
	 */
	private JamModePanel() {

		// create the labels which show the jam data
		Color darkBlue = new Color(30, 40, 60);
		Color white = Color.WHITE;
		matchName = new GradientLabel(SwingConstants.LEFT, Font.BOLD);
		matchName.setBackground(darkBlue);
		matchName.setForeground(white);
		logo = new GradientLabel(SwingConstants.RIGHT, Font.BOLD);
		logo.setIcon(Icons.LOGO);
		logo.setBackground(darkBlue);
		team1Name = new GradientLabel(SwingConstants.LEFT, Font.BOLD);
		team1Score = new GradientLabel(SwingConstants.CENTER, Font.BOLD);
		timer = new GradientLabel(SwingConstants.CENTER, Font.BOLD);
		timer.setBackground(darkBlue);
		timer.setForeground(white);
		team2Score = new GradientLabel(SwingConstants.CENTER, Font.BOLD);
		team2Name = new GradientLabel(SwingConstants.RIGHT, Font.BOLD);
		jamNumber = new GradientLabel(SwingConstants.CENTER, Font.BOLD);
		jamNumber.setBackground(darkBlue);
		jamNumber.setForeground(white);

		// create the panel and layout manager
		panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.BLACK);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;

		// 'y' weight values for each row
		double footer = 0.6;
		double teamName = 1;
		double scores = 0.15;

		// first row - match name and logo
		constraints.gridy = 0;
		constraints.weighty = 0;
		constraints.weightx = 1;
		panel.add(matchName, constraints);
		constraints.weightx = 0;
		panel.add(logo, constraints);

		// next row - team 1 name
		constraints.gridy++;
		constraints.gridwidth = 2;
		constraints.weightx = 1;
		constraints.weighty = teamName;
		panel.add(team1Name, constraints);

		// next row - scores and timer (in a subpanel to lock their widths)
		constraints.gridy++;
		constraints.weighty = scores;
		JPanel scoresPanel = new JPanel(new GridLayout(1, 0));
		scoresPanel.add(team1Score);
		scoresPanel.add(timer);
		scoresPanel.add(team2Score);
		scoresPanel.setBackground(Color.BLACK); // account for rounding errors
		panel.add(scoresPanel, constraints);

		// next row - team 2 name
		constraints.gridy++;
		constraints.weighty = teamName;
		panel.add(team2Name, constraints);

		// next row - jam number
		constraints.gridy++;
		constraints.weighty = footer;
		panel.add(jamNumber, constraints);

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
	 * Scales the labels to fit the new panel size.
	 */
	private void scale() {
		int panelHeight = panel.getHeight();

		// calculate heights for label text
		float headerFooter = (float) Math.floor(panelHeight * 0.06);
		float teamNames = (float) Math.floor(panelHeight * 0.07);
		float scores = (float) Math.floor(panelHeight * 0.2);
		float time = (float) Math.floor(panelHeight * 0.15);

		// scale fonts
		matchName.scaleFont(headerFooter);
		team1Name.scaleFont(teamNames);
		team1Score.scaleFont(scores);
		timer.scaleFont(time);
		team2Score.scaleFont(scores);
		team2Name.scaleFont(teamNames);
		jamNumber.scaleFont(headerFooter);

		// scale the logo
		int logoSize = panelHeight / 8;
		logo.setIcon(new ImageIcon(Icons.LOGO.getImage().getScaledInstance(
				logoSize, logoSize, Image.SCALE_SMOOTH)));

		// calculate border sizes
		int border = Math.max(2, panelHeight / 75);
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
		team1Name.setBorder(padding);
		team1Score.setBorder(BorderFactory.createMatteBorder(0, 0, border,
				border, black));
		timer.setBorder(BorderFactory.createMatteBorder(border, 0, border, 0,
				black));
		team2Score.setBorder(BorderFactory.createMatteBorder(border, border, 0,
				0, black));
		team2Name.setBorder(padding);
		jamNumber.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(border, 0, 0, 0, black),
				padding));
	}

	/**
	 * Returns the GUI panel which should be added to the scoreboard window.
	 */
	public JPanel getPanel() {
		return panel;
	}

	/**
	 * Returns the singleton instance of this class.
	 */
	public static JamModePanel getInstance() {
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
		private static final JamModePanel INSTANCE = new JamModePanel();
	}

	/**
	 * Updates the panel with details of the current jam.
	 */
	private class JamUpdater implements Observer<Jam> {
		public void update(Jam jam) {

			Team team1 = jam.getTeam1();
			Team team2 = jam.getTeam2();
			// update team names/colours - make sure gradients are smooth
			team1Name.showTeam(team1, true, false);
			team2Name.showTeam(team2, false, true);
			team1Score.showTeam(team1, false, true);
			team2Score.showTeam(team2, true, false);

			// check if we can do a Unicode star for the LJ status
			String star = team1Name.getFont().canDisplay('\u2605') ? "\u2605"
					: "*";

			// update LJ status
			Team leadJammer = jam.getLeadJammer();
			if (leadJammer != null) {
				if (team1 == leadJammer) {
					team1Name.setText(team1Name.getText() + " " + star);
				} else {
					team2Name.setText(star + " " + team2Name.getText());
				}
			}

			// update scores
			team1Score.setText(String.valueOf(jam.getScore1()));
			team2Score.setText(String.valueOf(jam.getScore2()));

			// update timer (turn it red as time runs out)
			int secs = jam.getTimeRemaining();
			timer.setText(String.format("%d:%02d", secs / 60, secs % 60));
			timer.setForeground(secs <= 10 ? Color.RED : Color.WHITE);

			// update heat number
			JamControl jc = JamControl.getInstance();
			jamNumber.setText("Heat " + (jc.getHeatIndex() + 1) + " / Jam "
					+ (jc.getJamIndex() + 1));
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
