package uk.co.lamsey.score5al.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.lamsey.score5al.control.JamControl;
import uk.co.lamsey.score5al.control.MatchControl;
import uk.co.lamsey.score5al.control.Observer;
import uk.co.lamsey.score5al.model.Heat;
import uk.co.lamsey.score5al.model.Jam;
import uk.co.lamsey.score5al.model.Match;
import uk.co.lamsey.score5al.model.Team;

/**
 * The panel used to edit jams while they are in progress, including jam
 * selection, score controls, and timer controls.
 */
public class EditJamsPanel {

	/**
	 * The spinner used to select the heat to be displayed.
	 */
	private JSpinner heatSpinner;

	/**
	 * The label showing the total number of heats in the match.
	 */
	private JLabel heatCountLabel;

	/**
	 * The spinner used to select the jam to be displayed.
	 */
	private JSpinner jamSpinner;

	/**
	 * The label showing the total number of jams in the heat.
	 */
	private JLabel jamCountLabel;

	/**
	 * The button used to choose the previous jam.
	 */
	private JButton prevButton;

	/**
	 * The button used to choose the next jam.
	 */
	private JButton nextButton;

	/**
	 * The spinner used to contain the number of seconds on the timer.
	 */
	private JSpinner timeSpinner;

	/**
	 * The label used to display the time in minutes and seconds.
	 */
	private JLabel timeLabel;

	/**
	 * The button which starts the jam timer.
	 */
	private JButton startTimer;

	/**
	 * The button which stops the jam timer.
	 */
	private JButton stopTimer;

	/**
	 * The button which resets the jam timer.
	 */
	private JButton resetTimer;

	/**
	 * The label used to display team 1's name.
	 */
	private GradientLabel team1Label;

	/**
	 * The label used to display team 2's name.
	 */
	private GradientLabel team2Label;

	/**
	 * The checkbox used to toggle team 1's lead jammer status.
	 */
	private JCheckBox team1LJ;

	/**
	 * The checkbox used to toggle team 2's lead jammer status.
	 */
	private JCheckBox team2LJ;

	/**
	 * The spinner used to contain team 1's score.
	 */
	private JSpinner team1Score;

	/**
	 * The spinner used to contain team 2's score.
	 */
	private JSpinner team2Score;

	/**
	 * The label used to display a warning if scores are set but not LJ status.
	 */
	private JLabel ljWarning;

	/**
	 * The GUI panel represented by this class.
	 */
	private JPanel panel;

	/**
	 * Initialises the GUI panel so it is ready to be added to the UI.
	 */
	private EditJamsPanel() {

		// jam controller is used to retrieve/update values
		final JamControl jamControl = JamControl.getInstance();

		// create the panel and layout manager (controls in subpanels)
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		HoverStatus hoverInfo;

		// subpanel for jam selection
		JPanel jamPanel = new JPanel();
		jamPanel.setBorder(BorderFactory.createTitledBorder("Jam selection"));
		GroupLayout layout = new GroupLayout(jamPanel);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		jamPanel.setLayout(layout);

		// jam selection controls
		prevButton = new JButton(Icons.PREVIOUS);
		prevButton.setEnabled(false);
		prevButton.addMouseListener(new HoverStatus(
				"Switches your view to the previous jam in the match."));
		JLabel heatLabel = new JLabel("Heat");
		heatSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 15, 1));
        HoverStatus.addToContainer(heatSpinner, new HoverStatus(
				"Changes the heat being displayed."));
		heatCountLabel = new JLabel("of 15 / Jam");
		jamSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 15, 1));
        HoverStatus.addToContainer(jamSpinner, new HoverStatus(
				"Changes the jam being displayed."));
		jamCountLabel = new JLabel("of 7");
		nextButton = new JButton(Icons.NEXT);
		nextButton.addMouseListener(new HoverStatus(
				"Switches your view to the next jam in the match."));

		// listeners for jam selection controls
		prevButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				jamControl.prevJam();
			}
		});
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				jamControl.nextJam();
			}
		});
		heatSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				jamControl.setHeatIndex((int) heatSpinner.getValue() - 1);
			}
		});
		jamSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				jamControl.setJamIndex((int) jamSpinner.getValue() - 1);
			}
		});

		// lay out jam selection panel
		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				// left padding
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(prevButton)
				.addComponent(heatLabel)
				// suppress spinner expansion
				.addComponent(heatSpinner, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(heatCountLabel)
				// suppress spinner expansion
				.addComponent(jamSpinner, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jamCountLabel).addComponent(nextButton)
				// right padding
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout
				.createParallelGroup(Alignment.CENTER)
				.addComponent(prevButton)
				.addComponent(heatLabel)
				// suppress spinner expansion
				.addComponent(heatSpinner, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(heatCountLabel)
				// suppress spinner expansion
				.addComponent(jamSpinner, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(jamCountLabel).addComponent(nextButton));

		// subpanel for timer controls
		JPanel timerPanel = new JPanel();
		timerPanel
				.setBorder(BorderFactory.createTitledBorder("Timer controls"));
		layout = new GroupLayout(timerPanel);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		timerPanel.setLayout(layout);

		// timer control buttons/labels
		JLabel timerLabel = new JLabel("Jam timer:", Icons.TIMER,
				SwingUtilities.LEFT);
		timerLabel.addMouseListener(new HoverStatus(
				"The time remaining for this jam, measured in seconds."));
		timeSpinner = new JSpinner(new SpinnerNumberModel(120, 0,
				Integer.MAX_VALUE, 1));
        HoverStatus.addToContainer(timeSpinner, new HoverStatus(
				"Adjusts the time remaining for this jam."));
		timeLabel = new JLabel("(2:00)");
		timeLabel.addMouseListener(new HoverStatus(
				"The time remaining for this jam in minutes and seconds."));
		startTimer = new JButton("Start", Icons.TIME_START);
		startTimer.addMouseListener(new HoverStatus(
				"Begins counting down the jam time."));
		stopTimer = new JButton("Stop", Icons.TIME_STOP);
		stopTimer.addMouseListener(new HoverStatus(
				"Stops counting down the jam time."));
		resetTimer = new JButton("Reset", Icons.TIME_RESET);
		resetTimer.addMouseListener(new HoverStatus(
				"Resets the jam timer to the jam duration for this match."));
		Component spacer = Box.createHorizontalGlue();

		// listeners for timer controls
		timeSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				jamControl.setTimeRemaining((int) timeSpinner.getValue());
			}
		});
		startTimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				jamControl.startTimer();
				if (jamControl.isTimerRunning()) {
					stopTimer.setEnabled(true);
					stopTimer.requestFocusInWindow();
					startTimer.setEnabled(false);
				}
			}
		});
		stopTimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				startTimer.requestFocusInWindow();
				jamControl.stopTimer();
			}
		});
		resetTimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				timeSpinner.requestFocusInWindow();
				jamControl.resetTimer();
			}
		});

		// lay out timer panel
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(timerLabel).addComponent(timeSpinner)
				.addComponent(timeLabel).addGap(20).addComponent(startTimer)
				.addComponent(stopTimer).addGap(10).addComponent(resetTimer)
				.addComponent(spacer));
		layout.setVerticalGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(timerLabel).addComponent(timeSpinner)
				.addComponent(timeLabel).addComponent(startTimer)
				.addComponent(stopTimer).addComponent(resetTimer)
				.addComponent(spacer));
		layout.linkSize(startTimer, stopTimer, resetTimer);

		// subpanel for score controls
		JPanel scorePanel = new JPanel();
		scorePanel
				.setBorder(BorderFactory.createTitledBorder("Score controls"));
		layout = new GroupLayout(scorePanel);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		scorePanel.setLayout(layout);

		// score control buttons/labels
		team1Label = new GradientLabel(SwingConstants.LEFT, Font.BOLD);
		team1Label.scaleFont(team1Label.getFont().getSize2D() * 1.2f);
		team2Label = new GradientLabel(SwingConstants.RIGHT, Font.BOLD);
		team2Label.scaleFont(team2Label.getFont().getSize2D() * 1.2f);
		team1LJ = new JCheckBox("Lead jammer");
		team2LJ = new JCheckBox("Lead jammer");
		hoverInfo = new HoverStatus(
				"Sets/unsets Lead Jammer status for this team.");
		team1LJ.addMouseListener(hoverInfo);
		team2LJ.addMouseListener(hoverInfo);
		team1Score = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
		team2Score = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
		hoverInfo = new HoverStatus("Adjusts this team's score for this jam.");
        HoverStatus.addToContainer(team1Score, hoverInfo);
        HoverStatus.addToContainer(team2Score, hoverInfo);
		JButton team1Minus1 = new JButton("1", Icons.MINUS);
		JButton team2Minus1 = new JButton("1", Icons.MINUS);
		hoverInfo = new HoverStatus(
				"Subtracts 1 from this team's score for this jam.");
		team1Minus1.addMouseListener(hoverInfo);
		team2Minus1.addMouseListener(hoverInfo);
		JButton team1Plus1 = new JButton("1", Icons.ADD);
		JButton team2Plus1 = new JButton("1", Icons.ADD);
		hoverInfo = new HoverStatus("Adds 1 to this team's score for this jam.");
		team1Plus1.addMouseListener(hoverInfo);
		team2Plus1.addMouseListener(hoverInfo);
		JButton team1Plus4 = new JButton("4", Icons.ADD);
		JButton team2Plus4 = new JButton("4", Icons.ADD);
		hoverInfo = new HoverStatus("Adds 4 to this team's score for this jam.");
		team1Plus4.addMouseListener(hoverInfo);
		team2Plus4.addMouseListener(hoverInfo);
		JButton team1Plus5 = new JButton("5", Icons.ADD);
		JButton team2Plus5 = new JButton("5", Icons.ADD);
		hoverInfo = new HoverStatus("Adds 5 to this team's score for this jam.");
		team1Plus5.addMouseListener(hoverInfo);
		team2Plus5.addMouseListener(hoverInfo);
		JSeparator separator = new JSeparator();
		ljWarning = new JLabel();
		ljWarning.setForeground(new Color(255, 128, 32));

		// listeners for controls
		team1LJ.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				jamControl.setTeam1LJ(team1LJ.isSelected());
			}
		});
		team2LJ.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				jamControl.setTeam2LJ(team2LJ.isSelected());
			}
		});
		team1Score.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				jamControl.setTeam1Score((int) (team1Score.getValue()));
			}
		});
		team2Score.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				jamControl.setTeam2Score((int) (team2Score.getValue()));
			}
		});
		team1Minus1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				jamControl.adjustTeam1Score(-1);
			}
		});
		team2Minus1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				jamControl.adjustTeam2Score(-1);
			}
		});
		team1Plus1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				jamControl.adjustTeam1Score(1);
			}
		});
		team2Plus1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				jamControl.adjustTeam2Score(1);
			}
		});
		team1Plus4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				jamControl.adjustTeam1Score(4);
			}
		});
		team2Plus4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				jamControl.adjustTeam2Score(4);
			}
		});
		team1Plus5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				jamControl.adjustTeam1Score(5);
			}
		});
		team2Plus5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				jamControl.adjustTeam2Score(5);
			}
		});

		// lay out score panel
		layout.setHorizontalGroup(layout
				.createParallelGroup()
				.addComponent(team1Label, GroupLayout.PREFERRED_SIZE,
						GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
				.addComponent(team2Label, GroupLayout.PREFERRED_SIZE,
						GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
				.addComponent(separator)
				.addComponent(ljWarning)
				.addGroup(
						layout.createSequentialGroup()
								.addGroup(
										layout.createParallelGroup()
												.addComponent(team1LJ)
												.addComponent(team2LJ))
								.addGroup(
										layout.createParallelGroup()
												.addComponent(team1Minus1)
												.addComponent(team2Minus1))
								.addGroup(
										layout.createParallelGroup()
												.addComponent(team1Score)
												.addComponent(team2Score))
								.addGroup(
										layout.createParallelGroup()
												.addComponent(team1Plus1)
												.addComponent(team2Plus1))
								.addGroup(
										layout.createParallelGroup()
												.addComponent(team1Plus4)
												.addComponent(team2Plus4))
								.addGroup(
										layout.createParallelGroup()
												.addComponent(team1Plus5)
												.addComponent(team2Plus5))));
		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addComponent(team1Label)
				.addGroup(
						layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(team1LJ)
								.addComponent(team1Minus1)
								.addComponent(team1Score)
								.addComponent(team1Plus1)
								.addComponent(team1Plus4)
								.addComponent(team1Plus5))
				.addComponent(separator, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGroup(
						layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(team2LJ)
								.addComponent(team2Minus1)
								.addComponent(team2Score)
								.addComponent(team2Plus1)
								.addComponent(team2Plus4)
								.addComponent(team2Plus5))
				.addComponent(team2Label)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(ljWarning));

		// jam panel, then timer panel, then score panel (logical progression)
		panel.add(jamPanel);
		panel.add(timerPanel);
		panel.add(scorePanel);
		panel.add(Box.createGlue());

		// register for updates and force an initial update
		PanelUpdater updater = new PanelUpdater();
		updater.update(MatchControl.getInstance().getMatch().getJam(0));
		jamControl.addObserver(updater);
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
	public static EditJamsPanel getInstance() {
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
		private static final EditJamsPanel INSTANCE = new EditJamsPanel();
	}

	/**
	 * Updates the UI with the new data from any changes.
	 */
	private class PanelUpdater implements Observer<Jam> {
		public void update(Jam jam) {

			// update jam selection status
			JamControl jamControl = JamControl.getInstance();
			int heatNumber = jamControl.getHeatIndex() + 1;
			if (!heatSpinner.getValue().equals(heatNumber)) {
				heatSpinner.setValue(heatNumber);
			}
			int jamNumber = jamControl.getJamIndex() + 1;
			if (!jamSpinner.getValue().equals(jamNumber)) {
				jamSpinner.setValue(jamNumber);
			}

			// enable/disable prev/next jam selection appropriately
			prevButton.setEnabled(heatNumber > 1 || jamNumber > 1);
			Match match = MatchControl.getInstance().getMatch();
			List<Heat> heats = match.getHeats();
			int maxHeat = heats.size();
			int maxJam = heats.get(jamControl.getHeatIndex()).getJams().size();
			nextButton.setEnabled(heatNumber < maxHeat || jamNumber < maxJam);

			// make sure heat/jam spinners have appropriate maximums
			((SpinnerNumberModel) (heatSpinner.getModel())).setMaximum(maxHeat);
			heatCountLabel.setText("of " + maxHeat + " / Jam");
			((SpinnerNumberModel) (jamSpinner.getModel())).setMaximum(maxJam);
			jamCountLabel.setText("of " + maxJam);

			// update the timer
			int timeRemaining = jam.getTimeRemaining();
			if (!timeSpinner.getValue().equals(timeRemaining)) {
				timeSpinner.setValue(timeRemaining);
			}
			timeLabel.setText(String.format("(%d:%02d)", timeRemaining / 60,
					timeRemaining % 60));

			// en/disable timer buttons as appropriate
			boolean timerRunning = jamControl.isTimerRunning();
			startTimer.setEnabled(!timerRunning && timeRemaining > 0);
			stopTimer.setEnabled(timerRunning);
			int maxTime = match.getJamDuration();
			resetTimer.setEnabled(timeRemaining != maxTime);
			((SpinnerNumberModel) (timeSpinner.getModel())).setMaximum(maxTime);

			// update the labels for the teams
			Team team1 = jam.getTeam1();
			Team team2 = jam.getTeam2();
			team1Label.showTeam(team1, true, true);
			team2Label.showTeam(team2, true, true);

			// update LJ status
			boolean isTeam1LJ = team1 != null && jam.getLeadJammer() == team1;
			if (isTeam1LJ != team1LJ.isSelected()) {
				team1LJ.setSelected(isTeam1LJ);
			}
			boolean isTeam2LJ = team2 != null && jam.getLeadJammer() == team2;
			if (isTeam2LJ != team2LJ.isSelected()) {
				team2LJ.setSelected(isTeam2LJ);
			}

			// disable/enable LJ buttons depending on if team is set
			team1LJ.setEnabled(team1 != null);
			team2LJ.setEnabled(team2 != null);

			// update scores
			int score1 = jam.getScore1();
			if (!team1Score.getValue().equals(score1)) {
				team1Score.setValue(score1);
			}
			int score2 = jam.getScore2();
			if (!team2Score.getValue().equals(score2)) {
				team2Score.setValue(score2);
			}

			// show warning if scores set but LJ not set
			if ((score1 > 0 || score2 > 0) && !(isTeam1LJ || isTeam2LJ)) {
				ljWarning.setText("Warning: scores set but Lead Jammer status "
						+ "not set!");
				ljWarning.setIcon(Icons.WARNING);
			} else {
				ljWarning.setText("");
				ljWarning.setIcon(null);
			}
		}
	}
}
