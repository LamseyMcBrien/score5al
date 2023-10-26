package uk.co.lamsey.score5al.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import uk.co.lamsey.score5al.control.LogicException;
import uk.co.lamsey.score5al.control.MatchControl;
import uk.co.lamsey.score5al.control.Observer;
import uk.co.lamsey.score5al.model.Match;

/**
 * The panel used to edit the match's basic details (name, team count, heat
 * count, jam count).
 */
public class EditMatchPanel {

	/**
	 * The GUI panel represented by this class.
	 */
	private JPanel panel;

	/**
	 * The textfield used to edit the match name.
	 */
	private JTextField nameField;

	/**
	 * The spinner used to edit the number of teams.
	 */
	private JSpinner teamsField;

	/**
	 * The spinner used to edit the number of heats.
	 */
	private JSpinner heatsField;

	/**
	 * The radio button used to choose the 'teams vs' method of calculating the
	 * number of jams.
	 */
	private JRadioButton vsRadio;

	/**
	 * The spinner used to edit the number of times each time plays one another.
	 */
	private JSpinner vsField;

	/**
	 * The radio button used to choose the 'custom' method of of calculating the
	 * number of jams.
	 */
	private JRadioButton customRadio;

	/**
	 * The spinner used to edit the custom number of jams.
	 */
	private JSpinner customField;

	/**
	 * The spinner used to edit the jam duration.
	 */
	private JSpinner durationField;

	/**
	 * Initialises the GUI panel so it is ready to be added to the UI.
	 */
	private EditMatchPanel() {

		// listener to handle update events
		MatchUpdater updater = new MatchUpdater();

		// create the panel and layout manager
		panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		panel.setLayout(layout);
		HoverStatus hoverInfo;

		// first row: match name
		JLabel nameInfo = new JLabel("Match name:", Icons.NAME,
				SwingUtilities.LEFT);
		nameField = new JTextField();
		hoverInfo = new HoverStatus("Changes the name of the match");
		nameInfo.addMouseListener(hoverInfo);
		nameField.addMouseListener(hoverInfo);
		nameField.addActionListener(updater);

		// next row: number of teams
		JLabel teamsInfo = new JLabel("Number of teams:", Icons.TEAM,
				SwingUtilities.LEFT);
		teamsField = new JSpinner(new SpinnerNumberModel(15, 2,
				Integer.MAX_VALUE, 1));
		hoverInfo = new HoverStatus("Changes the number of teams in the match."
				+ " Teams are added/removed to/from the end of the team list.");
        HoverStatus.addToContainer(teamsField, hoverInfo);
        teamsInfo.addMouseListener(hoverInfo);

		// next row: number of heats
		JLabel heatsInfo = new JLabel("Number of heats:", Icons.HEAT,
				SwingUtilities.LEFT);
		heatsField = new JSpinner(new SpinnerNumberModel(15, 1,
				Integer.MAX_VALUE, 1));
		hoverInfo = new HoverStatus("Changes the number of heats in the match."
				+ " Jams are distributed evenly between the heats.");
        HoverStatus.addToContainer(heatsField, hoverInfo);
		heatsInfo.addMouseListener(hoverInfo);

		// next row: jams
		JLabel jamsInfo = new JLabel("Number of jams:", Icons.JAM,
				SwingUtilities.LEFT);
		vsRadio = new JRadioButton("Teams play one another this many times: ",
				true);
		vsField = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE,
				1));
		customRadio = new JRadioButton("Custom number of jams: ");
		customField = new JSpinner(new SpinnerNumberModel(105, 1,
				Integer.MAX_VALUE, 1));
		hoverInfo = new HoverStatus("Changes the number of jams in the match. "
				+ "Jams are added/removed to/from the end of the jam schedule.");
		jamsInfo.addMouseListener(hoverInfo);
		vsRadio.addMouseListener(hoverInfo);
        HoverStatus.addToContainer(vsField, hoverInfo);
		customRadio.addMouseListener(hoverInfo);
        HoverStatus.addToContainer(customField, hoverInfo);

		// set up radiobutton selection behaviour
		ButtonGroup jamRadios = new ButtonGroup();
		jamRadios.add(vsRadio);
		jamRadios.add(customRadio);
		vsRadio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				vsField.setEnabled(true);
				customField.setEnabled(false);
			}
		});
		customRadio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				vsField.setEnabled(false);
				customField.setEnabled(true);
			}
		});

		// next row: jam duration
		JLabel durationInfo = new JLabel("Jam duration:", Icons.TIMER,
				SwingUtilities.LEFT);
		durationField = new JSpinner(new SpinnerNumberModel(120, 5,
				Integer.MAX_VALUE, 5));
		hoverInfo = new HoverStatus("Changes the duration of each jam in "
				+ "seconds.");
		durationInfo.addMouseListener(hoverInfo);
        HoverStatus.addToContainer(durationField, hoverInfo);

		// last row: 'update' button
		JButton updateButton = new JButton("Commit changes", Icons.EDIT);
		updateButton.addMouseListener(new HoverStatus(
				"Updates the match with any changes you have made"));
		updateButton.addActionListener(updater);

		// lay the panel out
		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup().addComponent(nameInfo)
								.addComponent(teamsInfo)
								.addComponent(heatsInfo).addComponent(jamsInfo)
								.addComponent(durationInfo))
				.addGroup(
						layout.createParallelGroup(Alignment.TRAILING)
								.addComponent(nameField)
								.addComponent(teamsField)
								.addComponent(heatsField)
								.addGroup(
										layout.createSequentialGroup()
												.addGroup(
														layout.createParallelGroup()
																.addComponent(
																		vsRadio)
																.addComponent(
																		customRadio))
												.addGroup(
														layout.createParallelGroup()
																.addComponent(
																		vsField)
																.addComponent(
																		customField)))
								.addComponent(durationField)
								.addComponent(updateButton)));
		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(nameInfo).addComponent(nameField))
				.addGroup(
						layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(teamsInfo)
								.addComponent(teamsField))
				.addGroup(
						layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(heatsInfo)
								.addComponent(heatsField))
				.addGroup(
						layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(jamsInfo).addComponent(vsRadio)
								.addComponent(vsField))
				.addGroup(
						layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(customRadio)
								.addComponent(customField))
				.addGroup(
						layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(durationInfo)
								.addComponent(durationField))
				.addComponent(updateButton));

		// add a listener for changes to the match and bootstrap the fields
		MatchControl mc = MatchControl.getInstance();
		mc.addObserver(new Observer<Match>() {
			public void update(Match match) {
				updateFields(match);
			}
		});
		updateFields(mc.getMatch());
	}

	/**
	 * Updates the values of the input fields based on the passed match's
	 * details.
	 */
	private void updateFields(final Match match) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// set the basic details from the match
				nameField.setText(match.getName());
				int totalTeams = match.getTotalTeams();
				teamsField.setValue(totalTeams);
				heatsField.setValue(match.getTotalHeats());
				int totalJams = match.getTotalJams();
				customField.setValue(totalJams);
				durationField.setValue(match.getJamDuration());

				// work out how many times each team plays each other
				int teamsVsOnce = ((totalTeams * (totalTeams - 1)) / 2);
				int teamsVs = totalJams / teamsVsOnce;
				vsField.setValue(teamsVs);

				// work out which radio button should be selected
				if (totalJams % teamsVsOnce == 0) {
					vsRadio.setSelected(true);
					vsField.setEnabled(true);
					customField.setEnabled(false);
				} else {
					customRadio.setSelected(true);
					customField.setEnabled(true);
					vsField.setEnabled(false);
				}
			}
		});
	}

	/**
	 * The listener which attempts to commit any changes made in the input
	 * fields.
	 */
	private class MatchUpdater implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			// get values
			String name = nameField.getText();
			int numTeams = (int) (teamsField.getValue());
			int numHeats = (int) (heatsField.getValue());
			int duration = (int) (durationField.getValue());

			// work out the number of jams
			int numJams;
			if (vsRadio.isSelected()) {
				numJams = ((numTeams * (numTeams - 1)) / 2)
						* (int) (vsField.getValue());
			} else {
				numJams = (int) (customField.getValue());
			}

			// try to update the match
			try {
				if (MatchControl.getInstance().updateMatch(name, numTeams,
						numHeats, numJams, duration)) {
					MainWindow.getInstance().updateStatusBar(Icons.SUCCESS,
							"Update successful", true);
				} else {
					MainWindow.getInstance().updateStatusBar(Icons.INFO,
							"No changes detected", true);
				}
			} catch (LogicException error) {
				MainWindow.getInstance().showError(error.getMessage());
			}
		}
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
	public static EditMatchPanel getInstance() {
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
		private static final EditMatchPanel INSTANCE = new EditMatchPanel();
	}
}
