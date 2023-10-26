package uk.co.lamsey.score5al.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import uk.co.lamsey.score5al.control.LogicException;
import uk.co.lamsey.score5al.control.MatchControl;

/**
 * Represents the menu bar for the main application window, which contains
 * various commands and options.
 */
public class MenuBar {

	/**
	 * The menu bar object which should be added to the main window.
	 */
	private JMenuBar menuBar;

	/**
	 * The button group which controls the scoreboard mode selection.
	 */
	private ButtonGroup scoreModeGroup;

	/**
	 * The file chooser used to select a filename for exporting.
	 */
	private JFileChooser fileChooser;

	/**
	 * The team formatting to be used when exporting match data.
	 */
	private ExportTeamFormat exportTeamFormat;

	/**
	 * Creates the menu bar and all of its options so it is ready to be added to
	 * the UI.
	 */
	private MenuBar() {

		// create the menubar and the export filechooser
		menuBar = new JMenuBar();
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter(
				"Comma-Separated Value (CSV) files", "csv"));

		// match menu - like the standard File menu
		JMenu matchMenu = new JMenu("Match");
		matchMenu.setMnemonic(KeyEvent.VK_M);
		menuBar.add(matchMenu);

		// score menu - for controlling the scoreboard window
		JMenu scoreMenu = new JMenu("Scoreboard");
		scoreMenu.setMnemonic(KeyEvent.VK_S);
		menuBar.add(scoreMenu);

		// help menu - self explanatory
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(helpMenu);

		// ---------------- //
		// match menu items //
		// ---------------- //

		// new match
		JMenuItem newMatch = new JMenuItem("New", Icons.NEW);
		newMatch.setMnemonic(KeyEvent.VK_N);
		newMatch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				KeyEvent.CTRL_DOWN_MASK));
		newMatch.addMouseListener(new HoverStatus("Creates a new match"));
		newMatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				MainWindow.getInstance().newMatch();
			}
		});
		matchMenu.add(newMatch);

		// open match
		JMenuItem openMatch = new JMenuItem("Open..", Icons.OPEN);
		openMatch.setMnemonic(KeyEvent.VK_O);
		openMatch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				KeyEvent.CTRL_DOWN_MASK));
		openMatch.addMouseListener(new HoverStatus("Opens a saved match"));
		openMatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				MainWindow.getInstance().openMatch();
			}
		});
		matchMenu.add(openMatch);

		// save match
		JMenuItem saveMatch = new JMenuItem("Save", Icons.SAVE);
		saveMatch.setMnemonic(KeyEvent.VK_S);
		saveMatch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				KeyEvent.CTRL_DOWN_MASK));
		saveMatch.addMouseListener(new HoverStatus("Saves the current match"));
		saveMatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				MainWindow.getInstance().saveMatch();
			}
		});
		matchMenu.add(saveMatch);

		// save match as
		JMenuItem saveMatchAs = new JMenuItem("Save as...", Icons.SAVE_AS);
		saveMatchAs.setMnemonic(KeyEvent.VK_A);
		saveMatchAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				KeyEvent.CTRL_DOWN_MASK));
		saveMatchAs.addMouseListener(new HoverStatus(
				"Saves the current match to a new file"));
		saveMatchAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				MainWindow.getInstance().saveMatchAs(null);
			}
		});
		matchMenu.add(saveMatchAs);

		matchMenu.add(new JSeparator());

		// show distribution
		JMenuItem distribution = new JMenuItem("Show distribution...",
				Icons.STATS);
		distribution.setMnemonic(KeyEvent.VK_D);
		distribution.addMouseListener(new HoverStatus(
				"Visualises the distribution of teams to heats/jams"));
		distribution.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DistributionDialog.getInstance().show();
			}
		});
		matchMenu.add(distribution);

		// save match
		JMenuItem export = new JMenuItem("Export to CSV", Icons.EXPORT);
		export.setMnemonic(KeyEvent.VK_E);
		export.addMouseListener(new HoverStatus("Exports the match data to a "
				+ "CSV file for importing into a spreadsheet package"));
		export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				export();
			}
		});
		matchMenu.add(export);

		// export options submenu
		JMenu exportOptions = new JMenu("Export options");
		exportOptions.setMnemonic(KeyEvent.VK_P);
		matchMenu.add(exportOptions);

		// button group to control export format selection
		JMenuItem header = new JMenuItem("Team formatting:");
		header.setEnabled(false);
		exportOptions.add(header);
		ButtonGroup teamFormatGroup = new ButtonGroup();
		exportTeamFormat = ExportTeamFormat.NAME_NUMBER;
		boolean selected = true;
		for (final ExportTeamFormat format : ExportTeamFormat.values()) {
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(
					format.getName(), selected);
			item.addMouseListener(new HoverStatus(format.getHelpText()));
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					exportTeamFormat = format;
				}
			});
			exportOptions.add(item);
			teamFormatGroup.add(item);
			selected = false;
		}

		matchMenu.add(new JSeparator());

		// exit
		JMenuItem exit = new JMenuItem("Exit", Icons.EXIT);
		exit.setMnemonic(KeyEvent.VK_X);
		exit.addMouseListener(new HoverStatus("Quits the application"));
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				MainWindow.getInstance().close();
			}
		});
		matchMenu.add(exit);

		// --------------------- //
		// scoreboard menu items //
		// --------------------- //

		// button group to control scoreboard mode selection
		scoreModeGroup = new ButtonGroup();
		for (ScoreboardMode mode : ScoreboardMode.values()) {
			scoreModeGroup.add(mode.getMenuItem());
			scoreMenu.add(mode.getMenuItem());
		}

		scoreMenu.add(new JSeparator());

		// enter full screen
		JMenuItem enterFullScreen = new JMenuItem("Enter full screen",
				Icons.FULL_SCREEN);
		enterFullScreen.setMnemonic(KeyEvent.VK_F);
		enterFullScreen.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_ENTER, KeyEvent.ALT_DOWN_MASK));
		enterFullScreen.addMouseListener(new HoverStatus("Puts the scoreboard "
				+ "into full-screen mode on its current screen (ESC to exit "
				+ "full-screen)."));
		enterFullScreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ScoreboardWindow.getInstance().enterFullScreen();
			}
		});
		scoreMenu.add(enterFullScreen);

		// exit full screen
		JMenuItem exitFullScreen = new JMenuItem("Exit full screen",
				Icons.WINDOWED);
		exitFullScreen.setMnemonic(KeyEvent.VK_X);
		exitFullScreen.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_ESCAPE, 0));
		exitFullScreen.addMouseListener(new HoverStatus("Puts the scoreboard "
				+ "into windowed mode."));
		exitFullScreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ScoreboardWindow.getInstance().exitFullScreen();
			}
		});
		scoreMenu.add(exitFullScreen);

		// --------------- //
		// help menu items //
		// --------------- //

		JMenuItem help = new JMenuItem("Help...", Icons.HELP);
		help.setMnemonic(KeyEvent.VK_H);
		help.addMouseListener(new HoverStatus(
				"Shows the user documentation for Score5al."));
		help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				HelpDialog.getInstance().show();
			}
		});
		helpMenu.add(help);

		// about dialog
		JMenuItem about = new JMenuItem("About...", Icons.INFO);
		about.setMnemonic(KeyEvent.VK_A);
		about.addMouseListener(new HoverStatus(
				"Shows version and copyright information about Score5al."));
		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				AboutDialog.getInstance().show();
			}
		});
		helpMenu.add(about);
	}

	/**
	 * Clears the selection of the scoreboard mode.
	 */
	public void clearScoreMode() {
		scoreModeGroup.clearSelection();
	}

	/**
	 * Shows the filechooser for the 'export' function and performs the export
	 * if desired.
	 */
	private void export() {

		// always ask for the filename when exporting
		MainWindow mw = MainWindow.getInstance();
		if (fileChooser.showSaveDialog(mw.getWindow()) == JFileChooser.APPROVE_OPTION) {
			String filename = fileChooser.getSelectedFile().getPath();
			if (!filename.endsWith(".csv")) {
				filename += ".csv";
			}

			// try to export the file
			try {
				MatchControl.getInstance().exportMatch(filename,
						exportTeamFormat);
				mw.updateStatusBar(Icons.SUCCESS,
						"Match data exported successfully.", true);
			} catch (LogicException error) {
				mw.showError(filename);
			}
		}
	}

	/**
	 * Returns the menu bar object which should be added to the main window.
	 */
	public JMenuBar getMenuBar() {
		return menuBar;
	}

	/**
	 * Returns the singleton instance of this class.
	 */
	public static MenuBar getInstance() {
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
		private static final MenuBar INSTANCE = new MenuBar();
	}
}
