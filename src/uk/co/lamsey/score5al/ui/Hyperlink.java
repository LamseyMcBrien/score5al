package uk.co.lamsey.score5al.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JLabel;

/**
 * A JLabel which acts as a hyperlink.
 */
public class Hyperlink extends JLabel {

	/**
	 * Creates a new hyperlink which links to the specified URL.
	 * 
	 * @param url
	 *            The full url, including protocol, to be visited.
	 */
	public Hyperlink(final String url) {

		// style the label like a link
		super(url);
		setForeground(Color.blue);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		// add hyperlink behaviour using a mouse listener
		addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent event) {
				setText("<html><u>" + url + "</u></html>");
			}

			public void mouseExited(MouseEvent event) {
				setText(url);
			}

			public void mouseClicked(MouseEvent event) {
				try {
					Desktop.getDesktop().browse(new URI(url));
				} catch (IOException error) {
					MainWindow.getInstance().showError(error.getMessage());
				} catch (URISyntaxException error) {
					MainWindow.getInstance().showError(error.getMessage());
				}
			}
		});
	}

}
