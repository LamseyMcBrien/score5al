package uk.co.lamsey.score5al.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import uk.co.lamsey.score5al.control.MatchControl;
import uk.co.lamsey.score5al.control.Observer;
import uk.co.lamsey.score5al.model.DistributionStats;
import uk.co.lamsey.score5al.model.Match;

/**
 * A dialog which visualises the distribution of teams to jams.
 */
public class DistributionDialog {

	/**
	 * The dialog encapsulated by this class.
	 */
	private JDialog dialog;

	/**
	 * The text area currently being used to display the data.
	 */
	private JTextArea textArea;

	/**
	 * Creates and lays out the dialog.
	 */
	private DistributionDialog() {

		// set up the dialog
		dialog = new JDialog(MainWindow.getInstance().getWindow(),
				"Team/Jam Distribution");
		dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        dialog.setIconImage(Icons.DISTRIBUTION.getImage());

		// create the text area to show the stats in a scrollpane
		textArea = new JTextArea(new DistributionStats(MatchControl
				.getInstance().getMatch()).getDistributionGraph());
		textArea.setEditable(false);
		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, textArea
				.getFont().getSize()));
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(600, 420));

		// create a label to show some help text
		JLabel infoLabel = new JLabel("Numbers in the graph represent the "
				+ "number of jams played by each team at the end of each "
				+ "heat they take part in.", Icons.INFO, SwingConstants.LEFT);
		infoLabel.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));

		// lay the dialog out
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(infoLabel, BorderLayout.NORTH);
		dialog.getContentPane().add(scrollPane, BorderLayout.CENTER);
		dialog.pack();

		// add an observer to update the distribution stats
		MatchControl.getInstance().addObserver(new TextAreaUpdater());
	}

	/**
	 * Shows the dialog.
	 */
	public void show() {
		dialog.setLocationByPlatform(true);
		dialog.setVisible(true);
	}

	/**
	 * Updates the placeholder text area.
	 */
	private class TextAreaUpdater implements Observer<Match> {
		@Override
		public void update(final Match match) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					textArea.setText(new DistributionStats(match)
							.getDistributionGraph());
				}
			});
		}
	}

	/**
	 * Returns the singleton instance of this class.
	 */
	public static DistributionDialog getInstance() {
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
		private static final DistributionDialog INSTANCE = new DistributionDialog();
	}
}
