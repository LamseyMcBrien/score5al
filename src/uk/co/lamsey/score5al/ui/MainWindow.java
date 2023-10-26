package uk.co.lamsey.score5al.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import uk.co.lamsey.score5al.control.JamControl;
import uk.co.lamsey.score5al.control.LogicException;
import uk.co.lamsey.score5al.control.MatchControl;

/**
 * Represents the main window of the Score5al UI, which contains the controls
 * for editing the match. Also handles functionality involving dialogue boxes
 * (like quitting, saving, etc).
 * 
 * @see ScoreboardWindow
 */
public class MainWindow {

	/**
	 * The application's main window (intended to be displayed on the local
	 * screen).
	 */
	private JFrame window;

	/**
	 * The label in the status panel at the bottom of the main window.
	 */
	private JLabel statusLabel;

	/**
	 * The file chooser used to select a filename for saving/loading.
	 */
	private JFileChooser fileChooser;

	/**
	 * Whether or not the last status was sticky, meaning it shouldn't be
	 * cleared until the next message is displayed.
	 */
	private boolean stickyStatus;

	/**
	 * Creates and displays the application UI.
	 */
	private MainWindow() {

		// set up the application window
		window = new JFrame("Score5al");
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				close();
			}
		});
		window.setIconImage(Icons.WINDOW.getImage());

		// add the menu bar
		window.setJMenuBar(MenuBar.getInstance().getMenuBar());

		// create the tab pane which contains the edit controls
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.addTab("Match", Icons.MATCH, EditMatchPanel.getInstance()
				.getPanel(), "Set up the match details");
		tabPane.addTab("Teams", Icons.TEAM, EditTeamsPanel.getInstance()
				.getPanel(), "Set up the teams taking part");
		tabPane.addTab("Heats", Icons.HEAT, EditHeatsPanel.getInstance()
				.getPanel(), "Assign teams to heats and jams");
		tabPane.addTab("Jams", Icons.JAM, EditJamsPanel.getInstance()
				.getPanel(), "Update the jam scores");

		// create the bottom status panel
		Box statusPanel = Box.createHorizontalBox();
		statusPanel.setMinimumSize(new Dimension(400, 20));
		statusPanel.setPreferredSize(new Dimension(640, 20));
		statusPanel.setMaximumSize(new Dimension(10000, 20));
		statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
				Color.GRAY));
		statusPanel.setOpaque(true);

		// add the status label to the status panel
		stickyStatus = false;
		statusLabel = new JLabel();
		statusPanel.add(Box.createHorizontalStrut(2));
		statusPanel.add(statusLabel);
		statusPanel.add(Box.createGlue());

		// set up the window's content pane (toolbar goes into the top)
		Container contentPane = window.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(ToolBar.getInstance().getToolbar(), BorderLayout.NORTH);
		contentPane.add(tabPane, BorderLayout.CENTER);
		contentPane.add(statusPanel, BorderLayout.SOUTH);

		// set up the file chooser
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter(
				"Score5al saved match files", "s5l"));
	}

	/**
	 * Makes the main UI window visible.
	 */
	public void show() {

		// pack the window to its preferred size
		window.pack();
		// set the window's minimum size to be 620x450
		window.setMinimumSize(new Dimension(620, 450));
		// set position according to OS defaults
		window.setLocationByPlatform(true);
		// prevent the toolbar button from getting default focus
		EditMatchPanel.getInstance().getPanel().requestFocusInWindow();
		// all done, show the GUI
		window.setVisible(true);
	}

	/**
	 * Prompts the user to confirm that they really want to quit and, if so,
	 * does so.
	 */
	public void close() {

		// check to see if there are unsaved changes
		boolean exitConfirmed = checkSaveNeeded("exiting");

		// if it's OK to quit, close down the UI
		if (exitConfirmed) {
			JamControl.getInstance().stopTimer();
			ScoreboardWindow.getInstance().close();
			window.setVisible(false);
			window.dispose();
			System.exit(0);
		}
	}

	/**
	 * Shows an error dialog with the passed message.
	 */
	public void showError(final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(window, message, "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	/**
	 * Shows an warning dialog with the passed messages.
	 */
	public void showWarnings(final List<String> messages) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(window, messages.toArray(),
						"Warning", JOptionPane.WARNING_MESSAGE);
			}
		});
	}

	/**
	 * Checks if the current match needs to be saved, then creates a new match.
	 */
	public void newMatch() {

		// check if we need to save
		if (checkSaveNeeded("creating a new match")) {
			MatchControl.getInstance().newMatch();
			updateStatusBar(Icons.SUCCESS, "New match created", true);
		}
	}

	/**
	 * Checks if the current match needs to be saved, then lets the user pick a
	 * new match to load.
	 */
	public void openMatch() {

		// check if we need to save
		if (checkSaveNeeded("opening a new match")) {

			// get the file to open from the user
			if (fileChooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
				String filename = fileChooser.getSelectedFile().getPath();
				try {
					List<String> warnings = MatchControl.getInstance()
							.loadMatch(filename);
					if (warnings.isEmpty()) {
						updateStatusBar(Icons.SUCCESS,
								"Loaded match successfully.", true);
					} else {
						showWarnings(warnings);
						updateStatusBar(Icons.WARNING,
								"Loaded match (with warnings).", true);
					}
				} catch (LogicException error) {
					showError(error.getMessage());
				}
			}
		}
	}

	/**
	 * Saves the current match and returns whether or not it was saved
	 * successfully.
	 */
	public boolean saveMatch() {
		return saveMatchAs(MatchControl.getInstance().getMatch()
				.getSaveFilePath());
	}

	/**
	 * Saves the match to the specified file (null to have the user specify a
	 * file), and returns whether or not the save succeeded.
	 */
	public boolean saveMatchAs(String filename) {

		// check if we know where to save to
		if (filename == null) {
			// not saved, ask for the filename
			if (fileChooser.showSaveDialog(window) == JFileChooser.APPROVE_OPTION) {
				filename = fileChooser.getSelectedFile().getPath();
				if (!filename.endsWith(".s5l")) {
					filename += ".s5l";
				}
			}
		}

		// if there's a file to save to, save the file
		if (filename != null) {
			try {
				MatchControl.getInstance().saveMatch(filename);
				updateStatusBar(Icons.SUCCESS, "File saved successfully", true);
				return true;
			} catch (LogicException error) {
				showError(filename);
			}
		}
		return false;
	}

	/**
	 * Checks if the user wants to save the current match before performing some
	 * other operation. Returns whether or not it's OK to proceed.
	 * 
	 * @param action
	 *            The missing text in the sentence
	 *            "Do you want to save before ...?"
	 */
	public boolean checkSaveNeeded(String action) {
		JamControl jamControl = JamControl.getInstance();
		if (MatchControl.getInstance().isUnsavedChanges()
				|| jamControl.isTimerRunning()) {
			int result = JOptionPane.showConfirmDialog(window,
					"There are unsaved changes. Do you want to save before "
							+ action + "?", "Warning: unsaved changes",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (result == JOptionPane.YES_OPTION) {
				// user wants to save - make sure timer is stopped, then save
				jamControl.stopTimer();
				return saveMatch();
			} else if (result == JOptionPane.NO_OPTION) {
				// user doesn't want to save - ensure timer is stopped
				jamControl.stopTimer();
				return true;
			} else {
				// user cancelled
				return false;
			}
		} else {
			// no unsaved changes and timer not running
			return true;
		}
	}

	/**
	 * Updates the contents of the status bar.
	 * 
	 * @param icon
	 *            The icon to be displayed in the status bar (null for no icon).
	 * @param text
	 *            The text to be displayed in the status bar (null for no text).
	 * @param sticky
	 *            If true, the text won't be cleared until the next text is set.
	 */
	public void updateStatusBar(final ImageIcon icon, final String text,
			boolean sticky) {
		stickyStatus = sticky;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				statusLabel.setIcon(icon);
				statusLabel.setText(text);
			}
		});
	}

	/**
	 * Clears the contents of the status bar, unless the previous message was
	 * sticky.
	 */
	public void clearStatusBar() {
		if (!stickyStatus) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					statusLabel.setIcon(null);
					statusLabel.setText(null);
				}
			});
		}
	}

	/**
	 * Returns the top-level frame (useful for parenting components).
	 */
	public JFrame getWindow() {
		return window;
	}

	/**
	 * Returns the singleton instance of this class.
	 */
	public static MainWindow getInstance() {
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
		private static final MainWindow INSTANCE = new MainWindow();
	}
}
