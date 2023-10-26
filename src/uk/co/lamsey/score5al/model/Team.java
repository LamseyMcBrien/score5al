package uk.co.lamsey.score5al.model;

import java.awt.Color;

/**
 * A team is a group of players participating in a Sur5al event. Each team has a
 * a number, a name, two colours, and an optional abbreviation.
 */
public class Team {

	/**
	 * The team's number.
	 */
	private int number;

	/**
	 * The team's name.
	 */
	private String name;

	/**
	 * An optional abbreviation for the team's name to be displayed when the
	 * team's name is too long.
	 */
	private String abbreviation;

	/**
	 * The foreground colour to be used for this team, which should correspond
	 * with the highlight colour of their team dress.
	 */
	private Color fgColour;

	/**
	 * The background colour to be used for this team, which should correspond
	 * with the main colour of their team dress.
	 */
	private Color bgColour;

	/**
	 * The number of points by which a team's match score should be altered. May
	 * be positive or negative.
	 */
	private int pointsAdjustment;

	/**
	 * This team's match ranking data.
	 */
	private Ranking ranking;

	/**
	 * Creates a new Team with default values.
	 * 
	 * @param teamNum
	 *            The team's number.
	 */
	public Team(int teamNum) {
		this.number = teamNum;
		this.name = "Team " + number;
		this.abbreviation = "";
		this.fgColour = Color.BLACK;
		this.bgColour = Color.WHITE;
		this.pointsAdjustment = 0;
		this.ranking = new Ranking(this);
	}

	/**
	 * Returns the team's number.
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Returns the team's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Updates the team's name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the optional abbreviation for the team's name to be displayed
	 * when the team's name is too long.
	 */
	public String getAbbreviation() {
		return abbreviation;
	}

	/**
	 * Updates the optional abbreviation for the team's name to be displayed
	 * when the team's name is too long.
	 */
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	/**
	 * Returns the foreground colour to be used for this team, which should
	 * correspond with the highlight colour of their team dress.
	 */
	public Color getFgColour() {
		return fgColour;
	}

	/**
	 * Updates the foreground colour to be used for this team, which should
	 * correspond with the highlight colour of their team dress.
	 */
	public void setFgColour(Color fgColour) {
		this.fgColour = fgColour;
	}

	/**
	 * Returns the background colour to be used for this team, which should
	 * correspond with the main colour of their team dress.
	 */
	public Color getBgColour() {
		return bgColour;
	}

	/**
	 * Updates the background colour to be used for this team, which should
	 * correspond with the main colour of their team dress.
	 */
	public void setBgColour(Color bgColour) {
		this.bgColour = bgColour;
	}

	/**
	 * Returns the number of points by which a team's match score should be
	 * altered. May be positive or negative.
	 */
	public int getPointsAdjustment() {
		return pointsAdjustment;
	}

	/**
	 * Updates the number of points by which a team's match score should be
	 * altered. May be positive or negative.
	 */
	public void setPointsAdjustment(int pointsAdjustment) {
		this.pointsAdjustment = pointsAdjustment;
	}

	/**
	 * Returns the team's ranking data.
	 */
	public Ranking getRanking() {
		return ranking;
	}
}
