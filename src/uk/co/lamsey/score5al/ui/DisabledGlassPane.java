package uk.co.lamsey.score5al.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * Simple implementation of a Glass Pane that will capture and ignore all events
 * as well paint the glass pane to give the frame a "disabled" look.
 * 
 * http://tips4java.wordpress.com/2008/11/07/disabled-glass-pane/
 */
public class DisabledGlassPane extends JComponent implements KeyListener {

    /**
     * The label used to display a message to the user.
     */
    private JLabel message = new JLabel();

    /**
     * Creates a new DisabledGlassPane, ready to be added to a rootPane.
     */
    public DisabledGlassPane() {

        // Set glass pane properties
        setOpaque(false);
        setBackground(new Color(240, 240, 240, 128));
        setLayout(new GridBagLayout());

        // Add a message label to the glass pane
        add(message, new GridBagConstraints());
        message.setOpaque(true);
        message.setBackground(new Color(224, 224, 224));
        message.setForeground(Color.BLACK);
        message.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Disable Mouse, Key and Focus events for the glass pane
        addMouseListener(new MouseAdapter() {
            // do nothing
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            // do nothing
        });

        addKeyListener(this);
        setFocusTraversalKeysEnabled(false);
    }

    /**
     * The component is transparent but we want to paint the background to give
     * it the disabled look.
     */
    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getSize().width, getSize().height);
    }

    /**
     * Consumes the event.
     */
    public void keyPressed(KeyEvent e) {
        e.consume();
    }

    /**
     * Does nothing.
     */
    public void keyTyped(KeyEvent e) {
        // do nothing
    }

    /**
     * Consumes the event.
     */
    public void keyReleased(KeyEvent e) {
        e.consume();
    }

    /**
     * Make the glass pane visible and change the cursor to the wait cursor
     * 
     * A message can be displayed and it will be centered on the frame.
     */
    public void activate(String text) {
        if (text != null && text.length() > 0) {
            message.setVisible(true);
            message.setText(text);
        } else {
            message.setVisible(false);
        }

        setVisible(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        requestFocusInWindow();
    }

    /**
     * Hide the glass pane and restore the cursor
     */
    public void deactivate() {
        setCursor(null);
        setVisible(false);
    }
}