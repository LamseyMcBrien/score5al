package uk.co.lamsey.score5al.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellRenderer;

import uk.co.lamsey.score5al.control.LogicException;
import uk.co.lamsey.score5al.control.MatchControl;
import uk.co.lamsey.score5al.control.Observer;
import uk.co.lamsey.score5al.model.Match;
import uk.co.lamsey.score5al.model.Team;

/**
 * Contains controls for assigning teams to the jams in each heat.
 */
public class EditHeatsPanel {

    /**
     * The GUI panel represented by this class.
     */
    private JPanel panel;

    /**
     * The spinner used to select the heat to be displayed.
     */
    private JSpinner heatSpinner;

    /**
     * The label showing the total number of heats in the match.
     */
    private JLabel heatCountLabel;

    /**
     * The button used to choose the previous heat.
     */
    private JButton prevButton;

    /**
     * The button used to choose the next heat.
     */
    private JButton nextButton;

    /**
     * The table used to display/select teams in the currently-selected heat.
     */
    private JTable heatTable;

    /**
     * The model used to display/edit the heats.
     */
    private EditHeatTableModel heatModel;

    /**
     * The spinner used to choose the number of teams per heat.
     */
    private JSpinner autoSpinner;

    /**
     * Initialises the GUI panel so it is ready to be added to the UI.
     */
    private EditHeatsPanel() {

        // create the panel and layout manager
        panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
        panel.setLayout(layout);

        // controls to change the current heat
        prevButton = new JButton(Icons.PREVIOUS);
        prevButton.setEnabled(false);
        prevButton.addMouseListener(new HoverStatus(
                "View the jams in the previous heat."));
        JLabel heatLabel = new JLabel("Heat");
        heatSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 15, 1));
        HoverStatus.addToContainer(heatSpinner, new HoverStatus(
                "Changes the heat being displayed."));
        heatCountLabel = new JLabel("of 15");
        nextButton = new JButton(Icons.NEXT);
        nextButton.addMouseListener(new HoverStatus(
                "View the jams in the next heat."));

        // listeners for the next/prev buttons
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Object value = heatSpinner.getNextValue();
                if (value != null) {
                    heatSpinner.setValue(value);
                }
            }
        });
        prevButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Object value = heatSpinner.getPreviousValue();
                if (value != null) {
                    heatSpinner.setValue(value);
                }
            }
        });

        // table to display/edit heats
        heatModel = new EditHeatTableModel();
        heatTable = new JTable(heatModel);
        heatTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        heatTable.getColumnModel().getColumn(1).setPreferredWidth(30);
        heatTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        heatTable.setRowSelectionAllowed(false);
        heatTable.setFillsViewportHeight(true);
        heatTable.setDefaultRenderer(JLabel.class, new JLabelRenderer());
        heatTable.setDefaultRenderer(Team.class, new TeamComboRenderer());
        heatTable.addMouseListener(new HoverStatus(
                "Click a team to change it to another team."));

        // editor for selecting teams
        TeamComboBox teamCombo = new TeamComboBox();
        heatTable
                .setDefaultEditor(Team.class, new DefaultCellEditor(teamCombo));

        // listener for the spinner
        heatSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {

                // update the table model
                Integer value = (Integer) heatSpinner.getValue();
                if (value != null) {
                    heatModel.showHeat(value - 1);
                }

                // en/disable the next/prev buttons if necessary
                if (heatSpinner.getNextValue() == null) {
                    nextButton.setEnabled(false);
                } else {
                    nextButton.setEnabled(true);
                }
                if (heatSpinner.getPreviousValue() == null) {
                    prevButton.setEnabled(false);
                } else {
                    prevButton.setEnabled(true);
                }
            }
        });

        // scrollpane containing table
        JScrollPane scrollPane = new JScrollPane(heatTable);
        scrollPane.setPreferredSize(new Dimension(430, 300));

        // button for updating the heat
        JButton updateButton = new JButton("Commit changes", Icons.EDIT);
        updateButton.addActionListener(new HeatUpdater());
        updateButton.addMouseListener(new HoverStatus(
                "Updates the selected heat with any changes."));

        // controls for auto-assigning teams
        JSeparator separator = new JSeparator();
        JLabel autoLabelPre = new JLabel("Auto-assign teams to all heats with");
        autoSpinner = new JSpinner(new SpinnerNumberModel(6, 2, 15, 1));
        HoverStatus.addToContainer(autoSpinner, new HoverStatus(
                "Changes the number of teams auto-allocated to each heat."));
        JLabel autoLabelPost = new JLabel("teams per heat:");
        JButton autoButton = new JButton("Auto-assign", Icons.WAND);
        autoButton.addActionListener(new AutoAssigner());
        autoButton.addMouseListener(new HoverStatus("Attempts to evenly "
                + "distribute the allocation of teams to heats/jams."));

        // lay the panel out
        layout.setHorizontalGroup(layout
                .createParallelGroup(Alignment.TRAILING)
                .addGroup(
                        layout.createSequentialGroup()
                                // left padding
                                .addContainerGap(GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                .addComponent(prevButton)
                                .addComponent(heatLabel)
                                // suppress spinner expansion
                                .addComponent(heatSpinner,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addComponent(heatCountLabel)
                                .addComponent(nextButton)
                                // right padding
                                .addContainerGap(GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE))
                .addComponent(scrollPane)
                .addComponent(updateButton)
                .addComponent(separator)
                .addGroup(
                        layout.createSequentialGroup()
                                .addComponent(autoLabelPre)
                                // suppress spinner expansion
                                .addComponent(autoSpinner,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addComponent(autoLabelPost)
                                // gap between label and button
                                .addPreferredGap(ComponentPlacement.UNRELATED,
                                        GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                .addComponent(autoButton)));
        layout.setVerticalGroup(layout
                .createSequentialGroup()
                .addGroup(
                        layout.createParallelGroup(Alignment.CENTER)
                                .addComponent(prevButton)
                                .addComponent(heatLabel)
                                // suppress spinner expansion
                                .addComponent(heatSpinner,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addComponent(heatCountLabel)
                                .addComponent(nextButton))
                .addComponent(scrollPane)
                .addComponent(updateButton)
                .addComponent(separator)
                .addGroup(
                        layout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(autoLabelPre)
                                .addComponent(autoSpinner)
                                .addComponent(autoLabelPost)
                                .addComponent(autoButton)));
        layout.linkSize(SwingConstants.HORIZONTAL, updateButton, autoButton);

        // make sure the match controls get updated to reflect match changes
        MatchControl.getInstance().addObserver(new HeatControlUpdater());
    }

    /**
     * Returns the GUI panel represented by this class.
     */
    public JPanel getPanel() {
        return panel;
    }

    /**
     * Returns the singleton instance of this class.
     */
    public static EditHeatsPanel getInstance() {
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
        private static final EditHeatsPanel INSTANCE = new EditHeatsPanel();
    }

    /**
     * Custom renderer for JLabels which just displays the JLabel.
     */
    private static class JLabelRenderer implements TableCellRenderer {

        /**
         * Simply returns the JLabel.
         */
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            return (JLabel) value;
        }
    }

    /**
     * Updates the panel when the match is updated.
     */
    private class HeatControlUpdater implements Observer<Match> {

        /**
         * Updates the heat selection controls (table model updates are handled
         * within the model).
         */
        @Override
        public void update(Match match) {

            // update the "selected heat" spinner and label
            int totalHeats = match.getTotalHeats();
            SpinnerNumberModel heatSpinnerModel = (SpinnerNumberModel) heatSpinner
                    .getModel();
            if ((Integer) heatSpinnerModel.getMaximum() != totalHeats) {
                heatCountLabel.setText("of " + totalHeats);
                if ((Integer) heatSpinner.getValue() > totalHeats) {
                    heatSpinner.setValue(totalHeats);
                }
                heatSpinnerModel.setMaximum(totalHeats);
            }

            // also update the "teams per heat" spinner
            int totalTeams = match.getTotalTeams();
            SpinnerNumberModel teamModel = (SpinnerNumberModel) autoSpinner
                    .getModel();
            if ((Integer) teamModel.getMaximum() != totalTeams) {
                if ((Integer) autoSpinner.getValue() > totalTeams) {
                    autoSpinner.setValue(totalTeams);
                }
                teamModel.setMaximum(totalTeams);
            }
        }
    }

    /**
     * Updates the heat with the current user-selected teams.
     */
    private class HeatUpdater implements ActionListener {

        /**
         * Attempts to update all the jams in the heat with the data from the
         * table and displays an appropriate message.
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            try {
                // try the update and see if anything changed
                MatchControl mc = MatchControl.getInstance();
                if (mc.updateHeat(heatModel.getHeatNo(),
                        heatModel.getTeam1List(), heatModel.getTeam2List())) {
                    MainWindow.getInstance().updateStatusBar(Icons.SUCCESS,
                            "Update successful", true);
                } else {
                    MainWindow.getInstance().updateStatusBar(Icons.INFO,
                            "No changes detected", true);
                }
            } catch (LogicException error) {
                MainWindow.getInstance().showError(error.getMessage());
            }
        }
    }

    /**
     * Auto-assigns teams to all heats in the match.
     */
    private class AutoAssigner implements ActionListener {

        /**
         * Confirms with the user, then attempts to auto-assign teams to all
         * heats, then displays an appropriate message.
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            // confirm with the user
            int response = JOptionPane.showConfirmDialog(
                    panel.getTopLevelAncestor(), new String[] {
                            "This function will (re)allocate the teams "
                                    + "in all heats.",
                            "Are you sure you want to continue?" }, "Confirm",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (response == JOptionPane.OK_OPTION) {

                // block user input
                final DisabledGlassPane glassPane = new DisabledGlassPane();
                SwingUtilities.getRootPane(panel).setGlassPane(glassPane);
                glassPane.activate("Please wait...");

                // run on background thread
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    public void run() {
                        try {
                            Integer teamsPerHeat = (Integer) autoSpinner
                                    .getValue();
                            if (teamsPerHeat != null) {
                                // try the update and see if anything changed
                                MatchControl mc = MatchControl.getInstance();
                                mc.autoAssign(teamsPerHeat);
                                MainWindow.getInstance().updateStatusBar(
                                        Icons.SUCCESS, "Update successful",
                                        true);
                            } else {
                                MainWindow.getInstance().showError(
                                        "Please select the number of teams "
                                                + "per heat.");
                            }
                        } catch (LogicException error) {
                            MainWindow.getInstance().showError(
                                    error.getMessage());
                        }

                        // unblock main window
                        glassPane.deactivate();
                    }
                });

            } else { // cancelled
                MainWindow.getInstance().updateStatusBar(Icons.INFO,
                        "Cancelled - no changes made", true);
            }
        }
    }

    /**
     * A combo box to be used as a table editor for selecting teams.
     */
    private static class TeamComboBox extends JComboBox<Team> implements
            Observer<Match> {

        /**
         * Initialises the combo box with the current list of teams.
         */
        public TeamComboBox() {
            super();
            setRenderer(new TeamComboRenderer());
            MatchControl mc = MatchControl.getInstance();
            mc.addObserver(this);
            update(mc.getMatch());
        }

        /**
         * Updates the combo box with the teams from the match.
         */
        @Override
        public void update(Match match) {
            setModel(new DefaultComboBoxModel<Team>(match.getTeams().toArray(
                    new Team[] {})));
        }
    }

    /**
     * A renderer for teams in a table or combobox.
     */
    private static class TeamComboRenderer extends GradientLabel implements
            ListCellRenderer<Team>, TableCellRenderer {

        /**
         * Creates the label with a bold font and centre alignment.
         */
        public TeamComboRenderer() {
            super(SwingConstants.CENTER, Font.BOLD);
        }

        /**
         * Updates the label based on the selected team.
         */
        @Override
        public Component getListCellRendererComponent(
                JList<? extends Team> list, Team team, int index,
                boolean isSelected, boolean cellHasFocus) {

            // need to reset font for combo boxes for some reason
            setFont(getFont().deriveFont(Font.BOLD));

            // show team, but correct for selection background if necessary
            showTeam(team, true, true);
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            return this;
        }

        /**
         * Updates the label based on the selected team.
         */
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object object, boolean isSelected, boolean hasFocus, int row,
                int column) {
            Team team = (Team) object;
            showTeam(team, true, true);
            table.setRowHeight(row, getPreferredSize().height);
            return this;
        }
    }
}
