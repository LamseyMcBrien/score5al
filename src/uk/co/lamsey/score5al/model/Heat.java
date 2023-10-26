package uk.co.lamsey.score5al.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A heat is a collection of jams between a group of teams, which are all played
 * consecutively in one time period.
 */
public class Heat {

	/**
	 * An ordered list of the jams in this heat.
	 */
	private List<Jam> jams;

	/**
	 * Creates a new Heat with no jams.
	 */
	public Heat() {
		jams = new ArrayList<Jam>();
	}

	/**
	 * Returns the ordered list of the jams in this heat.
	 */
	public List<Jam> getJams() {
		return jams;
	}

	/**
	 * Returns the total number of jams in this heat.
	 */
	public int getTotalJams() {
		return jams.size();
	}
}
