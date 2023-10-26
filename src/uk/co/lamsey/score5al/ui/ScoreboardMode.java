package uk.co.lamsey.score5al.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;

/**
 * A list of the different modes which the scoreboard can be placed into, which
 * each correspond to a particular GUI panel.
 * 
 * @see ScoreboardWindow
 */
public enum ScoreboardMode {

	/**
	 * The mode for display during jams.
	 */
	JAM(JamModePanel.getInstance().getPanel(), "Jam", Icons.JAM, KeyEvent.VK_J,
			"Shows the status of the in-progress jam in the scoreboard window."),

	/**
	 * The mode for displaying the schedule table.
	 */
	SCHEDULE(SchedulePanel.getInstance().getPanel(), "Schedule", Icons.HEAT,
			KeyEvent.VK_H,
			"Shows the heat schedule table in the scoreboard window."),

	/**
	 * The mode for displaying the rankings table.
	 */
	TABLE(RankingsPanel.getInstance().getPanel(), "Table", Icons.TABLE,
			KeyEvent.VK_T,
			"Shows the match rankings table in the scoreboard window.");

	/**
	 * The panel to be displayed in the scoreboard window in this mode.
	 */
	private JPanel panel;

	/**
	 * The toolbar button for this mode.
	 */
	private JCheckBoxMenuItem menuItem;

	/**
	 * The menubar item for this mode.
	 */
	private JToggleButton toolbarButton;

	/**
	 * Creates a new ScoreboardMode.
	 * 
	 * @param panel
	 *            The panel to be displayed in the scoreboard window while in
	 *            this mode.
	 * @param name
	 *            The name of this scoreboard mode.
	 * @param icon
	 *            An icon representing this mode.
	 * @param mnemonic
	 *            The keyboard mnemonic to be used for selecting this mode.
	 * @param helpText
	 *            Help text for buttons which activate this mode.
	 */
	private ScoreboardMode(JPanel panel, String name, Icon icon, int mnemonic,
			String helpText) {
		this.panel = panel;

		// create buttons
		toolbarButton = new JToggleButton(name, icon);
		menuItem = new JCheckBoxMenuItem(name + " mode", icon);
		menuItem.setMnemonic(mnemonic);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(mnemonic,
				KeyEvent.CTRL_DOWN_MASK));
		HoverStatus hoverStatus = new HoverStatus(helpText);
		toolbarButton.addMouseListener(hoverStatus);
		menuItem.addMouseListener(hoverStatus);
		ScoreboardSwitcher switcher = new ScoreboardSwitcher();
		toolbarButton.addActionListener(switcher);
		menuItem.addActionListener(switcher);
	}

	/**
	 * Ensures that the buttons for this mode are in the selected state.
	 */
	public void setSelected() {
		if (!toolbarButton.isSelected()) {
			toolbarButton.setSelected(true);
		}
		if (!menuItem.isSelected()) {
			menuItem.setSelected(true);
		}
	}

	/**
	 * Returns the panel to be displayed in the scoreboard window in this mode.
	 */
	public JPanel getPanel() {
		return panel;
	}

	/**
	 * Returns the toolbar button for this mode.
	 */
	public JCheckBoxMenuItem getMenuItem() {
		return menuItem;
	}

	/**
	 * Returns the menubar item for this mode.
	 */
	public JToggleButton getToolbarButton() {
		return toolbarButton;
	}

	/**
	 * Shows the scoreboard window in a particular mode.
	 */
	private class ScoreboardSwitcher implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			ScoreboardWindow.getInstance().show(ScoreboardMode.this);
		}
	}
}
