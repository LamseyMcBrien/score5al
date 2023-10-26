package uk.co.lamsey.score5al.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;

/**
 * Contains the controls at the top of the main window.
 */
public class ToolBar {

	/**
	 * The toolbar represented by this class.
	 */
	private JToolBar toolbar;

	/**
	 * The button group which controls the scoreboard mode selection.
	 */
	private ButtonGroup scoreModeGroup;

	/**
	 * Initialises the toolbar so it is ready to be added to the UI.
	 */
	private ToolBar() {

		// create the container panel
		toolbar = new JToolBar();

		// 'new' button to create a new match
		JButton newButton = new JButton("New", Icons.NEW);
		newButton.addMouseListener(new HoverStatus("Creates a new match"));
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				MainWindow.getInstance().newMatch();
			}
		});
		toolbar.add(newButton);

		// 'open' button to open an existing match
		JButton openButton = new JButton("Open...", Icons.OPEN);
		openButton.addMouseListener(new HoverStatus("Opens a saved match"));
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				MainWindow.getInstance().openMatch();
			}
		});
		toolbar.add(openButton);

		// 'save' button to save the current match to its current file
		JButton saveButton = new JButton("Save", Icons.SAVE);
		saveButton.addMouseListener(new HoverStatus("Saves the current match"));
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				MainWindow.getInstance().saveMatch();
			}
		});
		toolbar.add(saveButton);

		// 'save' button to save the current match to a new file
		JButton saveAsButton = new JButton("Save as...", Icons.SAVE_AS);
		saveAsButton.addMouseListener(new HoverStatus(
				"Saves the current match to a new file"));
		saveAsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				MainWindow.getInstance().saveMatchAs(null);
			}
		});
		toolbar.add(saveAsButton);

		// spacer
		toolbar.add(Box.createHorizontalStrut(10));
		toolbar.add(Box.createGlue());

		// label for score controls
		toolbar.add(new JLabel("Scoreboard mode: "));

		// button group to control scoreboard mode selection
		scoreModeGroup = new ButtonGroup();
		for (ScoreboardMode mode : ScoreboardMode.values()) {
			scoreModeGroup.add(mode.getToolbarButton());
			toolbar.add(mode.getToolbarButton());
		}
	}

	/**
	 * Clears the selection of the scoreboard mode.
	 */
	public void clearScoreMode() {
		scoreModeGroup.clearSelection();
	}

	/**
	 * Returns the toolbar represented by this class.
	 */
	public JToolBar getToolbar() {
		return toolbar;
	}

	/**
	 * Returns the singleton instance of this class.
	 */
	public static ToolBar getInstance() {
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
		private static final ToolBar INSTANCE = new ToolBar();
	}
}
