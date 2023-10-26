package uk.co.lamsey.score5al.ui;

import uk.co.lamsey.score5al.model.Team;

/**
 * Defines options for defining how the team name appears in the results during
 * an export.
 */
public enum ExportTeamFormat {

	/**
	 * Formats teams by their number and name.
	 */
	NAME_NUMBER("[# - name]",
			"Identifies teams in the results using their number and name") {
		public String format(Team team) {
			return team == null ? "No team selected" : team.getNumber() + " - "
					+ team.getName();
		}
	},

	/**
	 * Formats teams by their number and abbreviated name.
	 */
	ABBR_NUMBER("[# - abbr. name]",
			"Identifies teams in the results using their number and abbreviated name") {
		public String format(Team team) {
			if (team == null) {
				return "No team";
			} else {
				String abbr = team.getAbbreviation();
				String name = abbr != null && !abbr.isEmpty() ? abbr : team
						.getName();
				return team.getNumber() + " - " + name;
			}
		}
	},

	/**
	 * Formats teams by their number only.
	 */
	NUMBER("[#]", "Identifies teams in the results using their number only") {
		public String format(Team team) {
			return team == null ? "?" : String.valueOf(team.getNumber());
		}
	},

	/**
	 * Formats teams by their name only.
	 */
	NAME("[name]", "Identifies teams in the results using their name only") {
		public String format(Team team) {
			return team == null ? "No team selected" : team.getName();
		}
	},

	/**
	 * Formats teams by their abbreviated name only.
	 */
	ABBR("[abbr. name]",
			"Identifies teams in the results using their abbreviated name only") {
		public String format(Team team) {
			if (team == null) {
				return "No team";
			} else {
				String abbr = team.getAbbreviation();
				return abbr != null && !abbr.isEmpty() ? abbr : team.getName();
			}
		}
	};

	/**
	 * The name of this mode, to be displayed in the UI.
	 */
	private String name;

	/**
	 * The help text to be shown when selecting this mode.
	 */
	private String helpText;

	/**
	 * @param name
	 *            The name of this mode, to be displayed in the UI.
	 * @param helpText
	 *            The help text to be shown when selecting this mode.
	 */
	private ExportTeamFormat(String name, String helpText) {
		this.name = name;
		this.helpText = helpText;
	}

	/**
	 * Returns the name of this mode, to be displayed in the UI.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the help text to be shown when selecting this mode.
	 */
	public String getHelpText() {
		return helpText;
	}

	/**
	 * Formats the passed team as appropriate for this mode.
	 */
	public abstract String format(Team team);
}
