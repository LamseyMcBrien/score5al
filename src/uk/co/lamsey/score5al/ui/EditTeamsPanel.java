package uk.co.lamsey.score5al.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;

import uk.co.lamsey.score5al.control.LogicException;
import uk.co.lamsey.score5al.control.MatchControl;
import uk.co.lamsey.score5al.model.Team;

/**
 * Contains controls for editing the teams in the match.
 */
public class EditTeamsPanel {

	/**
	 * The GUI panel represented by this class.
	 */
	private JPanel panel;

	/**
	 * The table used to display/select teams.
	 */
	private JTable teamTable;

	/**
	 * The input field used to change the team name.
	 */
	private JTextField nameField;

	/**
	 * The input field used to change the team abbreviation.
	 */
	private JTextField abbreviationField;

	/**
	 * The label used to show the team's colours.
	 */
	private JLabel colourLabel;

	/**
	 * The button used to choose a new foreground colour.
	 */
	private JButton fgButton;

	/**
	 * The button used to choose a new background colour.
	 */
	private JButton bgButton;

	/**
	 * The input field used to change the team's points adjustment.
	 */
	private JSpinner adjustmentField;

	/**
	 * The button used to update the selected team.
	 */
	private JButton updateButton;

	/**
	 * Initialises the GUI panel so it is ready to be added to the UI.
	 */
	private EditTeamsPanel() {

		// listener to handle update events
		TeamUpdater updater = new TeamUpdater();

		// create the panel and layout manager
		panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		panel.setLayout(layout);
		HoverStatus hoverInfo;

		// table to display/select teams
		teamTable = new JTable(new EditTeamTableModel());
		teamTable.getColumnModel().getColumn(0).setPreferredWidth(30);
		teamTable.getColumnModel().getColumn(1).setPreferredWidth(185);
		teamTable.getColumnModel().getColumn(2).setPreferredWidth(185);
		teamTable.getColumnModel().getColumn(3).setPreferredWidth(30);
		teamTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		teamTable.setFillsViewportHeight(true);
		teamTable.setDefaultRenderer(Team.class, new TeamNumberRenderer());
		teamTable.addMouseListener(new HoverStatus(
				"Select a team to edit its details."));
		teamTable.getSelectionModel().addListSelectionListener(
				new SelectionListener());

		// first row: scrollpane containing teams table
		JScrollPane scrollPane = new JScrollPane(teamTable);
		scrollPane.setPreferredSize(new Dimension(430, 320));

		// next row: team name
		JLabel nameInfo = new JLabel("Name:", Icons.NAME, SwingUtilities.LEFT);
		nameField = new JTextField();
		hoverInfo = new HoverStatus("Changes the name of the selected team");
		nameInfo.addMouseListener(hoverInfo);
		nameField.addMouseListener(hoverInfo);
		nameField.addActionListener(updater);
		nameField.setEnabled(false);

		// next row: team name abbreviation
		JLabel abbreviationInfo = new JLabel("Abbreviation:",
				Icons.ABBREVIATION, SwingUtilities.LEFT);
		abbreviationField = new JTextField();
		hoverInfo = new HoverStatus(
				"Changes the abbreviated version of the selected team's name");
		abbreviationInfo.addMouseListener(hoverInfo);
		abbreviationField.addMouseListener(hoverInfo);
		abbreviationField.addActionListener(updater);
		abbreviationField.setEnabled(false);

		// next row: colours
		JLabel colourInfo = new JLabel("Colours:", Icons.PALETTE,
				SwingUtilities.LEFT);
		colourLabel = new TeamNumberRenderer();
		colourLabel.setText("AaBbCc123");
		colourLabel.setBorder(new EmptyBorder(3, 5, 3, 5));
		colourLabel.setOpaque(false);
		colourLabel.setEnabled(false);
		colourInfo.addMouseListener(new HoverStatus(
				"Changes the colours used to represent this team"));
		colourLabel.addMouseListener(new HoverStatus(
				"Preview of the colours to be used to represent this team"));
		fgButton = new JButton("Choose foreground...", Icons.FOREGROUND);
		fgButton.addActionListener(new FgChooser());
		fgButton.addMouseListener(new HoverStatus(
				"Selects a new foreground (highlight) colour for this team."));
		fgButton.setEnabled(false);
		bgButton = new JButton("Choose background...", Icons.BACKGROUND);
		bgButton.addActionListener(new BgChooser());
		bgButton.addMouseListener(new HoverStatus(
				"Selects a new background (main) colour for this team."));
		bgButton.setEnabled(false);

		// next row: points adjustment
		JLabel adjustmentInfo = new JLabel("Points Adjustment:",
				Icons.ADJUSTMENT, SwingUtilities.LEFT);
		adjustmentField = new JSpinner(new SpinnerNumberModel(0,
				Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
		hoverInfo = new HoverStatus("Alters the number of points to "
				+ "add or substract from the team's match points total");
		adjustmentInfo.addMouseListener(hoverInfo);
        HoverStatus.addToContainer(adjustmentField, hoverInfo);
		adjustmentField.setEnabled(false);

		// last row: 'update' button
		updateButton = new JButton("Commit changes", Icons.EDIT);
		updateButton.addMouseListener(new HoverStatus(
				"Updates the selected team with any changes"));
		updateButton.addActionListener(updater);
		updateButton.setEnabled(false);

		// lay the panel out
		layout.setHorizontalGroup(layout
				.createParallelGroup()
				.addComponent(scrollPane)
				.addGroup(
						layout.createSequentialGroup()
								.addGroup(
										layout.createParallelGroup()
												.addComponent(nameInfo)
												.addComponent(abbreviationInfo)
												.addComponent(colourInfo)
												.addComponent(adjustmentInfo))
								.addGroup(
										layout.createParallelGroup(
												Alignment.TRAILING)
												.addComponent(nameField)
												.addComponent(abbreviationField)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		colourLabel,
																		GroupLayout.PREFERRED_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)
																.addComponent(
																		bgButton)
																.addComponent(
																		fgButton))
												.addComponent(adjustmentField)
												.addComponent(updateButton))));
		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addComponent(scrollPane)
				.addGroup(
						layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(nameInfo).addComponent(nameField))
				.addGroup(
						layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(abbreviationInfo)
								.addComponent(abbreviationField))
				.addGroup(
						layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(colourInfo)
								.addComponent(colourLabel)
								.addComponent(fgButton).addComponent(bgButton))
				.addGroup(
						layout.createParallelGroup(Alignment.BASELINE)
								.addComponent(adjustmentInfo)
								.addComponent(adjustmentField))
				.addComponent(updateButton));
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
	public static EditTeamsPanel getInstance() {
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
		private static final EditTeamsPanel INSTANCE = new EditTeamsPanel();
	}

	/**
	 * The listener which updates the input fields when a team is selected.
	 */
	private class SelectionListener implements ListSelectionListener {

		/**
		 * Updates the input fields based on the table selection
		 * (clears/disables the inputs if nothing is selected).
		 */
		@Override
		public void valueChanged(ListSelectionEvent event) {
			if (event == null || !event.getValueIsAdjusting()) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						int selectedRow = teamTable.getSelectedRow();
						if (selectedRow == -1) {
							// no selection, clear and disable the input fields
							nameField.setText("");
							nameField.setEnabled(false);
							abbreviationField.setText("");
							abbreviationField.setEnabled(false);
							colourLabel.setOpaque(false);
							colourLabel.setForeground(Color.BLACK);
							colourLabel.setEnabled(false);
							fgButton.setEnabled(false);
							bgButton.setEnabled(false);
							adjustmentField.setValue(0);
							adjustmentField.setEnabled(false);
							updateButton.setEnabled(false);
						} else {
							// team selected, get its details
							Team team = MatchControl.getInstance().getMatch()
									.getTeams().get(selectedRow);
							nameField.setText(team.getName());
							nameField.setEnabled(true);
							nameField.selectAll();
							nameField.requestFocusInWindow();
							abbreviationField.setText(team.getAbbreviation());
							abbreviationField.setEnabled(true);
							colourLabel.setOpaque(true);
							colourLabel.setForeground(team.getFgColour());
							colourLabel.setBackground(team.getBgColour());
							colourLabel.setEnabled(true);
							fgButton.setEnabled(true);
							bgButton.setEnabled(true);
							adjustmentField.setValue(team.getPointsAdjustment());
							adjustmentField.setEnabled(true);
							updateButton.setEnabled(true);
						}
					}
				});
			}
		}
	}

	/**
	 * The listener which handles choosing the foreground colour.
	 */
	private class FgChooser implements ActionListener {

		/**
		 * The colour chooser used to select the new colour.
		 */
		private JColorChooser chooser;

		/**
		 * The label used for the preview panel in the chooser.
		 */
		private GradientLabel previewLabel;

		/**
		 * Sets up the colour chooser so it is ready to be shown.
		 */
		public FgChooser() {
			chooser = new JColorChooser();
			JPanel previewPanel = new JPanel(new BorderLayout());
			previewLabel = new GradientLabel(SwingConstants.CENTER, Font.BOLD);
			previewLabel.setText("AaBbCc123");
			previewPanel.add(previewLabel);
			previewLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
			chooser.setPreviewPanel(previewPanel);
			chooser.getSelectionModel().addChangeListener(new PreviewUpdater());
		}

		/**
		 * Shows a colour chooser dialogue, and updates the colour label's
		 * foreground colour if the user so chooses.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			chooser.setColor(colourLabel.getForeground());
			previewLabel.setForeground(colourLabel.getForeground());
			previewLabel.setBackground(colourLabel.getBackground());
			JColorChooser.createDialog(panel.getTopLevelAncestor(),
					"Select foreground colour", true, chooser, new FgUpdater(),
					null).setVisible(true);
		}

		/**
		 * Updates the selected foreground colour when the OK button is pressed.
		 */
		private class FgUpdater implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				colourLabel.setForeground(chooser.getColor());
			}
		}

		/**
		 * Handles updates to the preview panel in the chooser.
		 */
		private class PreviewUpdater implements ChangeListener {
			public void stateChanged(ChangeEvent event) {
				previewLabel.setForeground(chooser.getColor());
			}
		}
	}

	/**
	 * The listener which handles choosing the background colour.
	 */
	private class BgChooser implements ActionListener {

		/**
		 * The colour chooser used to select the new colour.
		 */
		private JColorChooser chooser;

		/**
		 * The label used for the preview panel in the chooser.
		 */
		private GradientLabel previewLabel;

		/**
		 * Sets up the colour chooser so it is ready to be shown.
		 */
		public BgChooser() {
			chooser = new JColorChooser();
			JPanel previewPanel = new JPanel(new BorderLayout());
			previewLabel = new GradientLabel(SwingConstants.CENTER, Font.BOLD);
			previewLabel.setText("AaBbCc123");
			previewPanel.add(previewLabel);
			previewLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
			chooser.setPreviewPanel(previewPanel);
			chooser.getSelectionModel().addChangeListener(new PreviewUpdater());
		}

		/**
		 * Shows a colour chooser dialogue, and updates the colour label's
		 * background colour if the user so chooses.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			chooser.setColor(colourLabel.getBackground());
			previewLabel.setForeground(colourLabel.getForeground());
			previewLabel.setBackground(colourLabel.getBackground());
			JColorChooser.createDialog(panel.getTopLevelAncestor(),
					"Select background colour", true, chooser, new BgUpdater(),
					null).setVisible(true);
		}

		/**
		 * Updates the selected background colour when the OK button is pressed.
		 */
		private class BgUpdater implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				colourLabel.setBackground(chooser.getColor());
			}
		}

		/**
		 * Handles updates to the preview panel in the chooser.
		 */
		private class PreviewUpdater implements ChangeListener {
			public void stateChanged(ChangeEvent event) {
				previewLabel.setBackground(chooser.getColor());
			}
		}
	}

	/**
	 * The listener which executes the 'update team' logic.
	 */
	private class TeamUpdater implements ActionListener {

		/**
		 * Attempts to update the currently-selected team with the details
		 * specified.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					int selectedTeam = teamTable.getSelectedRow();
					if (selectedTeam == -1) {
						// no team selected
						MainWindow.getInstance().showError(
								"Please select a team to modify!");
					} else {
						try {
							// try the update and see if anything changed
							if (MatchControl.getInstance().updateTeam(
									selectedTeam, nameField.getText(),
									abbreviationField.getText(),
									colourLabel.getForeground(),
									colourLabel.getBackground(),
									(int) adjustmentField.getValue())) {
								MainWindow.getInstance().updateStatusBar(
										Icons.SUCCESS, "Update successful",
										true);
							} else {
								MainWindow.getInstance()
										.updateStatusBar(Icons.INFO,
												"No changes detected", true);
							}
						} catch (LogicException error) {
							MainWindow.getInstance().showError(
									error.getMessage());
						}
					}
				}
			});
		}
	}

	/**
	 * Custom renderer for team numbers which uses the team's foreground and
	 * background colours.
	 */
	private static class TeamNumberRenderer extends GradientLabel implements
			TableCellRenderer {

		/**
		 * Creates the label with a bold font and centre alignment.
		 */
		public TeamNumberRenderer() {
			super(SwingConstants.CENTER, Font.BOLD);
		}

		/**
		 * Returns a JLabel which displays the team's number using the team's
		 * foreground and background colours.
		 */
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			// column class is Team
			Team team = (Team) value;
			showTeam(team, true, true);
			setText(String.valueOf(team.getNumber()));
			table.setRowHeight(row, getPreferredSize().height);
			return this;
		}
	}
}
