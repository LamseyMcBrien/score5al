package uk.co.lamsey.score5al.ui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * A dialog which displays the program's "about" information.
 */
public class AboutDialog {

	/**
	 * The current software version.
	 */
    public static final String VERSION = "1.0 (2013-08-14)";

	/**
	 * The dialog encapsulated by this class.
	 */
	private JDialog dialog;

	/**
	 * Creates and lays out the dialog.
	 */
	private AboutDialog() {

		// set up the dialog
		dialog = new JDialog(MainWindow.getInstance().getWindow(),
				"About Score5al");
		dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		dialog.setIconImage(Icons.INFO.getImage());

		// create a box to hold labels with the program information
		Box labelBox = new Box(BoxLayout.Y_AXIS);
		labelBox.add(new JLabel("Score5al - a scoreboard program for Sur5al"));
		labelBox.add(Box.createVerticalStrut(5));
		labelBox.add(new JLabel("Version " + VERSION));
		labelBox.add(new JLabel(
				"(c) Copyright Liam McBrien 2013. All Rights Reserved."));
		labelBox.add(new Hyperlink("http://www.lamsey.co.uk"));
		labelBox.add(Box.createVerticalStrut(5));
		labelBox.add(new JLabel("Score5al uses the OpenCSV library"));
		labelBox.add(new Hyperlink("http://opencsv.sourceforge.net/"));
		labelBox.add(Box.createVerticalStrut(5));
		labelBox.add(new JLabel("Silk icon set by Mark James"));
		labelBox.add(new Hyperlink("http://www.famfamfam.com/lab/icons/silk/"));
		labelBox.add(Box.createVerticalStrut(5));
		labelBox.add(new JLabel(
				"Sur5al logo and concept (c) Royal Windsor Rollergirls"));
		labelBox.add(new Hyperlink("http://www.windsorrollergirls.com"));

		// lay out the panel with the program icon on the left
		JComponent panel = (JComponent) dialog.getContentPane();
		panel.setLayout(new BorderLayout());
		panel.add(new JLabel(Icons.ICON), BorderLayout.WEST);
		panel.add(labelBox, BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		labelBox.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

		// lay the dialog out
		dialog.pack();
	}

	/**
	 * Shows the dialog.
	 */
	public void show() {
		dialog.setLocationRelativeTo(MainWindow.getInstance().getWindow());
		dialog.setVisible(true);
	}

	/**
	 * Returns the singleton instance of this class.
	 */
	public static AboutDialog getInstance() {
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
		private static final AboutDialog INSTANCE = new AboutDialog();
	}
}
