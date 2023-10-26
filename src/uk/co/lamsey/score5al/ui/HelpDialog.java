package uk.co.lamsey.score5al.ui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Represents the window used to display the user guide.
 * 
 * @author Lamsey
 */
public class HelpDialog {

    /**
     * The dialog box which contains the help information.
     */
    private JDialog dialog;

    /**
     * The text pane which displays the help information.
     */
    private JEditorPane helpPane;

    /**
     * Creates the help dialog and makes it ready to be shown.
     */
    private HelpDialog() {

        // create the dialog
        dialog = new JDialog(MainWindow.getInstance().getWindow(), "User Guide");
        dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        dialog.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
        dialog.setIconImage(Icons.HELP.getImage());

        // create the pane used to display the help text
        helpPane = new JEditorPane();
        helpPane.setContentType("text/html");
        helpPane.setEditable(false);

        // add a listener to navigate using the HTML links in the help pages
        helpPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent event) {
                if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    loadPage(event.getURL());
                }
            }
        });

        // add the help pane in a scrollpane
        JScrollPane scrollPane = new JScrollPane(helpPane);
        scrollPane.setPreferredSize(new Dimension(550, 535));
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(scrollPane);

        // load the start page
        loadPage(HelpDialog.class.getResource("/help/start.html"));

        // make sure the dialog is appropriately sized and placed
        dialog.pack();
        dialog.setMinimumSize(new Dimension(450, 415));
        dialog.setLocationByPlatform(true);
    }

    /**
     * Displays the help dialog if it's not already visible.
     */
    public void show() {
        if (!dialog.isVisible()) {
            dialog.setVisible(true);
        }
    }

    /**
     * Loads the specified page internally or externally depending on the
     * protocol.
     */
    private void loadPage(URL url) {
        if (url.getProtocol().equals("http")
                || url.getProtocol().equals("mailto")) {
            // external link
            try {
                Desktop.getDesktop().browse(url.toURI());
            } catch (IOException error) {
                MainWindow.getInstance().showError(error.getMessage());
            } catch (URISyntaxException error) {
                MainWindow.getInstance().showError(error.getMessage());
            }
        } else {
            // internal link
            try {
                helpPane.setPage(url);
            } catch (IOException error) {
                helpPane.setText("<html><body><h1>I/O Error</h1><p>The following "
                        + "error was encountered while loading the help page:</p>"
                        + "<p>" + error.getMessage() + "</p></body></html>");
            }
        }
    }

    /**
     * Returns the singleton instance of this class.
     */
    public static HelpDialog getInstance() {
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
        private static final HelpDialog INSTANCE = new HelpDialog();
    }
}
