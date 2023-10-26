package uk.co.lamsey.score5al.ui;

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * Represents the scoreboard window, which is designed to be shown on another
 * monitor and controlled via the main window. The scoreboard window has various
 * modes which can be switched between by the user.
 */
public class ScoreboardWindow {

    /**
     * The secondary window used to display the scoreboard (intended to be
     * displayed on the projector).
     */
    private JFrame window;

    /**
     * The layout manager used to switch between the panels which can be shown
     * in the window.
     */
    private CardLayout panelSwitcher;

    /**
     * Creates the frame and sets it up so it is ready to be shown.
     */
    private ScoreboardWindow() {

        // set up the window and handle close events properly
        window = new JFrame("Scoreboard");
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                hide();
            }
        });
        window.setIconImage(Icons.WINDOW.getImage());

        // leave full screen mode when ESC is pressed
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(new KeyEventDispatcher() {
                    public boolean dispatchKeyEvent(KeyEvent event) {
                        if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
                            exitFullScreen();
                        }
                        return false;
                    }
                });

        // set up the container panel
        panelSwitcher = new CardLayout();
        Container panel = window.getContentPane();
        panel.setLayout(panelSwitcher);

        // add the panels
        for (ScoreboardMode mode : ScoreboardMode.values()) {
            panel.add(mode.getPanel(), mode.name());
        }

        // pack the window to its preferred size
        window.pack();
        // set the window's minimum size to be 600x450
        window.setMinimumSize(new Dimension(600, 450));
        // set default location by OS defaults
        window.setLocationByPlatform(true);
    }

    /**
     * Shows the scoreboard window (which is made visible if it's not already)
     * in the specified mode (null to leave mode alone).
     */
    public void show(ScoreboardMode mode) {
        if (mode != null) {
            panelSwitcher.show(window.getContentPane(), mode.name());
            mode.setSelected();
            MainWindow.getInstance().updateStatusBar(Icons.SUCCESS,
                    "Scoreboard set to " + mode.getMenuItem().getText() + ".",
                    true);
        }
        if (!window.isVisible()) {
            window.setVisible(true);
        }
    }

    /**
     * Hides the scoreboard window (does nothing if not already visible).
     */
    public void hide() {
        ToolBar.getInstance().clearScoreMode();
        MenuBar.getInstance().clearScoreMode();
        if (window.isVisible()) {
            exitFullScreen();
            window.setVisible(false);
        }
    }

    /**
     * Destroys the scoreboard window in preparation for app shutdown.
     */
    public void close() {
        hide();
        window.dispose();
    }

    /**
     * Puts the scoreboard into full-screen mode.
     */
    public void enterFullScreen() {
        if (!window.isUndecorated()) {
            if (window.isVisible()) {
                window.setVisible(false);
            }
            window.dispose();
            window.setUndecorated(true);
            window.pack();
            window.setVisible(true);
            window.getGraphicsConfiguration().getDevice()
                    .setFullScreenWindow(window);
            MainWindow.getInstance().updateStatusBar(Icons.FULL_SCREEN,
                    "Scoreboard set to full-screen mode", true);
        }
    }

    /**
     * Removes the scoreboard from full-screen mode.
     */
    public void exitFullScreen() {
        if (window.isVisible() && window.isUndecorated()) {
            window.getGraphicsConfiguration().getDevice()
                    .setFullScreenWindow(null);
            window.setVisible(false);
            window.dispose();
            window.setUndecorated(false);
            window.pack();
            window.setVisible(true);
            MainWindow.getInstance().updateStatusBar(Icons.WINDOWED,
                    "Scoreboard set to windowed mode", true);
        }
    }

    /**
     * Returns the singleton instance of this class.
     */
    public static ScoreboardWindow getInstance() {
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
        private static final ScoreboardWindow INSTANCE = new ScoreboardWindow();
    }
}
