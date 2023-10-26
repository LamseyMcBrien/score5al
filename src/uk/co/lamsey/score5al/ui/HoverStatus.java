package uk.co.lamsey.score5al.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A MouseListener which displays a string in the program's status bar whenever
 * the mouse is hovered over the component to which the HoverStatus is attached.
 */
public class HoverStatus extends MouseAdapter {

    /**
     * The text to be displayed in the status bar.
     */
    private String statusText;

    /**
     * Creates a new HoverStatus which will display the specified text in the
     * status bar.
     */
    public HoverStatus(String statusText) {
        this.statusText = statusText;
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        MainWindow.getInstance().updateStatusBar(Icons.INFO, statusText, false);
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        MainWindow.getInstance().clearStatusBar();
    }

    /**
     * Adds the passed HoverStatus object to all of the components in the passed
     * container or composite component (such as a JSpinner).
     */
    public static void addToContainer(Container container,
            HoverStatus hoverStatus) {
        container.addMouseListener(hoverStatus);
        for (Component component : container.getComponents()) {
            component.addMouseListener(hoverStatus);
            if (component instanceof Container) {
                addToContainer((Container) component, hoverStatus);
            }
        }
    }
}
