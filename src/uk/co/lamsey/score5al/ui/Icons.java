package uk.co.lamsey.score5al.ui;

import java.net.URL;

import javax.swing.ImageIcon;

/**
 * Static class with access to a standard set of icons used throughout the UI.
 */
public abstract class Icons {

	/**
	 * Used for incrementing scores.
	 */
	public static final ImageIcon ADD = icon("add.png");

	/**
	 * Used to represent a team's abbreviation.
	 */
	public static final ImageIcon ABBREVIATION = icon("cut.png");

	/**
	 * Used to represent a team's points adjustment.
	 */
	public static final ImageIcon ADJUSTMENT = icon("wrench.png");

	/**
	 * Used to represent a team's background colour.
	 */
	public static final ImageIcon BACKGROUND = icon("paintcan.png");

	/**
	 * Used to represent team distribution.
	 */
	public static final ImageIcon DISTRIBUTION = icon("chart_line.png");

	/**
	 * Used for edit/update operations.
	 */
	public static final ImageIcon EDIT = icon("pencil.png");

	/**
	 * Used for quitting the application.
	 */
	public static final ImageIcon EXIT = icon("door_out.png");

	/**
	 * Used for exporting match data.
	 */
	public static final ImageIcon EXPORT = icon("table_save.png");

	/**
	 * Used to represent a team's foreground colour.
	 */
	public static final ImageIcon FOREGROUND = icon("paintbrush.png");

	/**
	 * Used to represent full-screen mode.
	 */
	public static final ImageIcon FULL_SCREEN = icon("monitor.png");

	/**
	 * Used to represent a heat.
	 */
	public static final ImageIcon HEAT = icon("text_columns.png");

	/**
	 * A standard blue 'help' icon.
	 */
	public static final ImageIcon HELP = icon("help.png");

	/**
	 * Used as the program icon. See also WINDOW, LOGO.
	 */
	public static final ImageIcon ICON = icon("icon.png");

	/**
	 * A blue info icon.
	 */
	public static final ImageIcon INFO = icon("information.png");

	/**
	 * Used to represent a jam.
	 */
	public static final ImageIcon JAM = icon("clock.png");

	/**
	 * A white transparent version of the Sur5al logo. See also ICON, LOGO.
	 */
	public static final ImageIcon LOGO = icon("logo.png");

	/**
	 * Used to represent a match.
	 */
	public static final ImageIcon MATCH = icon("star.png");

	/**
	 * Used for decrementing scores.
	 */
	public static final ImageIcon MINUS = icon("delete.png");

	/**
	 * Used to represent a name.
	 */
	public static final ImageIcon NAME = icon("tag_blue.png");

	/**
	 * A 'next' icon (a blue arrow pointing right).
	 */
	public static final ImageIcon NEXT = icon("resultset_next.png");

	/**
	 * A 'new' icon (a blank page with an 'add' button).
	 */
	public static final ImageIcon NEW = icon("page_white_add.png");

	/**
	 * An 'open' icon (page being taken out of a folder).
	 */
	public static final ImageIcon OPEN = icon("folder_page.png");

	/**
	 * Used to represent team colours
	 */
	public static final ImageIcon PALETTE = icon("palette.png");

	/**
	 * A 'previous' icon (a blue arrow pointing left).
	 */
	public static final ImageIcon PREVIOUS = icon("resultset_previous.png");

	/**
	 * A 'save' icon (floppy disk).
	 */
	public static final ImageIcon SAVE = icon("disk.png");

	/**
	 * A 'save as' icon (floppy disks).
	 */
	public static final ImageIcon SAVE_AS = icon("disk_multiple.png");

	/**
	 * An icon used for showing more information (a magnifying glass).
	 */
	public static final ImageIcon STATS = icon("chart_line.png");

	/**
	 * A green success icon.
	 */
	public static final ImageIcon SUCCESS = icon("accept.png");

	/**
	 * Used to represent the rankings table.
	 */
	public static final ImageIcon TABLE = icon("chart_bar.png");

	/**
	 * Used to represent a team.
	 */
	public static final ImageIcon TEAM = icon("group.png");

	/**
	 * Used to represent jam time.
	 */
	public static final ImageIcon TIMER = icon("hourglass.png");

	/**
	 * Used when starting the jam timer.
	 */
	public static final ImageIcon TIME_START = icon("clock_play.png");

	/**
	 * Used when stopping the jam timer.
	 */
	public static final ImageIcon TIME_STOP = icon("clock_stop.png");

	/**
	 * Used when resetting the jam timer.
	 */
	public static final ImageIcon TIME_RESET = icon("clock_red.png");

	/**
	 * A warning symbol.
	 */
	public static final ImageIcon WARNING = icon("error.png");

	/**
	 * A magic wand, used for auto-assign operations.
	 */
	public static final ImageIcon WAND = icon("wand.png");

	/**
	 * Used for the window icon. See also ICON, LOGO.
	 */
	public static final ImageIcon WINDOW = icon("window.png");

	/**
	 * Used to represent windowed mode.
	 */
	public static final ImageIcon WINDOWED = icon("application.png");

	/**
	 * Creates an icon from the specified filename.
	 */
	private static ImageIcon icon(String filename) {
		URL resource = Icons.class.getResource("/icons/" + filename);
		if (resource == null) {
			throw new RuntimeException("Icon " + filename + " does not exist!");
		}
		return new ImageIcon(resource);
	}

}
