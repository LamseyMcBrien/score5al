package uk.co.lamsey.score5al.control;

import java.awt.Font;
import java.util.Enumeration;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import uk.co.lamsey.score5al.ui.MainWindow;

/**
 * Main executable class for the program. This class simply instantiates the
 * main program window and makes it visible.
 */
public class Main {

	/**
	 * Sets the default OS look and feel, then shows the application window.
	 * 
	 * @param args
	 *            Ignored.
	 */
	public static void main(String[] args) {

		// use the OS look and feel
		String lafError;
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			lafError = null;
		} catch (Exception error) {
			// show the error later
			lafError = error.getMessage();
		}

		// use a larger font - makes buttons etc easier to hit
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Font font = UIManager.getFont(key);
			if (font != null) {
				UIManager.put(key, font.deriveFont(font.getSize2D() + 1));
			}
		}

		// build and show the UI
		final String error = lafError;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainWindow.getInstance().show();
				// display any LaF errors
				if (error != null) {
					MainWindow.getInstance().showError(error);
				}
			}
		});
	}
}
