package uk.co.lamsey.score5al.control;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import uk.co.lamsey.score5al.model.Jam;
import uk.co.lamsey.score5al.model.Match;
import uk.co.lamsey.score5al.model.Team;
import uk.co.lamsey.score5al.ui.Icons;
import uk.co.lamsey.score5al.ui.MainWindow;

/**
 * Implements the logic related to updating jams, including jam selection and
 * the score and timer functions.
 */
public class JamControl extends Observable<Jam> {

	/**
	 * The heat index of the jam which is currently selected for
	 * editing/display.
	 */
	private int heatIndex;

	/**
	 * The jam index of the jam which is currently selected for editing/display.
	 */
	private int jamIndex;

	/**
	 * The service used to implement the jam timer.
	 */
	private ScheduledExecutorService timer;

	/**
	 * The Future object representing the currently-running timer (null if not
	 * running).
	 */
	private ScheduledFuture<?> runningTimer;

	/**
	 * An object used for synchronisation locking when manipulating the time
	 * remaining.
	 */
	private Object timerSync;

	/**
	 * Initialises the jam controls with the details of the first jam in the
	 * match.
	 */
	private JamControl() {
		heatIndex = 0;
		jamIndex = 0;
		MatchControl.getInstance().addObserver(new MatchObserver());
		timer = Executors.newSingleThreadScheduledExecutor();
		runningTimer = null;
		timerSync = new Object();
	}

	/**
	 * Selects the next jam in the match (does nothing if no more jams).
	 */
	public void nextJam() {

		// try getting the next jam in the heat
		Match match = MatchControl.getInstance().getMatch();
		Jam jam = match.getJam(heatIndex, jamIndex + 1);
		if (jam != null) { // jam found
			stopTimer(); // make sure timer isn't still running
			jamIndex++;
			notifyObservers(jam);
		} else {
			// try getting the first jam of the next heat
			jam = match.getJam(heatIndex + 1, 0);
			if (jam != null) { // jam found
				stopTimer(); // make sure timer isn't still running
				heatIndex++;
				jamIndex = 0;
				notifyObservers(jam);
			}
		}
	}

	/**
	 * Selects the previous jam in the match (does nothing if no more jams).
	 */
	public void prevJam() {

		// try getting the previous jam in the heat
		Match match = MatchControl.getInstance().getMatch();
		if (jamIndex > 0) { // there must be a previous jam in this heat
			stopTimer(); // make sure timer isn't still running
			jamIndex--;
			notifyObservers(match.getJam(heatIndex, jamIndex));
		} else if (heatIndex > 0) {
			stopTimer(); // make sure timer isn't still running
			// get the last jam of the previous heat
			heatIndex--;
			List<Jam> jams = match.getHeats().get(heatIndex).getJams();
			jamIndex = jams.size() - 1;
			notifyObservers(jams.get(jamIndex));
		}
	}

	/**
	 * Returns the heat index of the jam which is currently selected for
	 * editing/display.
	 */
	public int getHeatIndex() {
		return heatIndex;
	}

	/**
	 * Updates the heat index of the jam which is currently selected for
	 * editing/display.
	 */
	public void setHeatIndex(int newHeatIndex) {
		if (heatIndex != newHeatIndex) {
			stopTimer(); // make sure timer isn't still running
			Match match = MatchControl.getInstance().getMatch();
			// try getting the same jam for the new heat; if not, then work back
			for (int newJamIndex = jamIndex; newJamIndex >= 0; newJamIndex--) {
				Jam jam = match.getJam(newHeatIndex, newJamIndex);
				if (jam != null) {
					heatIndex = newHeatIndex;
					jamIndex = newJamIndex;
					notifyObservers(jam);
					return;
				}
			}
		}
	}

	/**
	 * Returns the jam index of the jam which is currently selected for
	 * editing/display.
	 */
	public int getJamIndex() {
		return jamIndex;
	}

	/**
	 * Updates the jam index of the jam which is currently selected for
	 * editing/display.
	 */
	public void setJamIndex(int newJamIndex) {
		stopTimer(); // make sure timer isn't still running
		if (jamIndex != newJamIndex) {
			Jam jam = MatchControl.getInstance().getMatch()
					.getJam(heatIndex, newJamIndex);
			if (jam != null) {
				jamIndex = newJamIndex;
				notifyObservers(jam);
			}
		}
	}

	/**
	 * Sets the time remaining for the current jam to the specified value.
	 */
	public void setTimeRemaining(int time) {
		if (time >= 0) {
			synchronized (timerSync) {
				Jam jam = getJam();
				if (time != jam.getTimeRemaining()) {
					jam.setTimeRemaining(time);

					// stop the timer if it's running and we hit 0
					if (time == 0 && isTimerRunning()) {
						runningTimer.cancel(false);
						runningTimer = null;
						MainWindow.getInstance().updateStatusBar(
								Icons.TIME_STOP, "Jam timer stopped.", true);
					}

					notifyObservers(jam);
					MatchControl.getInstance().setUnsavedChanges(true);
				}
			}
		}
	}

	/**
	 * Starts the jam timer (does nothing if already running or time is up).
	 */
	public void startTimer() {
		synchronized (timerSync) {
			if (!isTimerRunning()) {
				final Jam jam = getJam();
				if (jam.getTimeRemaining() > 0) {
					runningTimer = timer.scheduleAtFixedRate(new Runnable() {
						public void run() {
							synchronized (timerSync) {
								setTimeRemaining(jam.getTimeRemaining() - 1);
							}
						}
					}, 1, 1, TimeUnit.SECONDS);
					MainWindow.getInstance().updateStatusBar(Icons.TIME_START,
							"Jam timer started.", true);
				}
			}
		}
	}

	/**
	 * Stops the jam timer (does nothing if not running).
	 */
	public void stopTimer() {
		synchronized (timerSync) {
			if (isTimerRunning()) {
				runningTimer.cancel(false);
				runningTimer = null;
				MainWindow.getInstance().updateStatusBar(Icons.TIME_STOP,
						"Jam timer stopped.", true);
				notifyObservers(getJam());
			}
		}
	}

	/**
	 * Resets the jam timer.
	 */
	public void resetTimer() {
		setTimeRemaining(MatchControl.getInstance().getMatch().getJamDuration());
		MainWindow.getInstance().updateStatusBar(Icons.TIME_RESET,
				"Jam timer reset.", true);
	}

	/**
	 * Returns whether or not the timer is currently running.
	 */
	public boolean isTimerRunning() {
		synchronized (timerSync) {
			return runningTimer != null;
		}
	}

	/**
	 * Adjusts team 1's score by the specified value.
	 */
	public void adjustTeam1Score(int amount) {
		Jam jam = getJam();
		jam.setScore1(Math.max(jam.getScore1() + amount, 0));
		notifyObservers(jam);
		MatchControl.getInstance().setUnsavedChanges(true);
	}

	/**
	 * Adjusts team 2's score by the specified value.
	 */
	public void adjustTeam2Score(int amount) {
		Jam jam = getJam();
		jam.setScore2(Math.max(jam.getScore2() + amount, 0));
		notifyObservers(jam);
		MatchControl.getInstance().setUnsavedChanges(true);
	}

	/**
	 * Sets team 1's score to the specified value.
	 */
	public void setTeam1Score(int score) {
		Jam jam = getJam();
		if (score != jam.getScore1()) {
			jam.setScore1(score);
			notifyObservers(jam);
			MatchControl.getInstance().setUnsavedChanges(true);
		}
	}

	/**
	 * Sets team 2's score to the specified value.
	 */
	public void setTeam2Score(int score) {
		Jam jam = getJam();
		if (score != jam.getScore2()) {
			jam.setScore2(score);
			notifyObservers(jam);
			MatchControl.getInstance().setUnsavedChanges(true);
		}
	}

	/**
	 * Sets or unsets team 1's lead jammer status (if true, also unsets team 2's
	 * LJ status).
	 */
	public void setTeam1LJ(boolean isLJ) {
		Jam jam = getJam();
		setLJ(isLJ, jam.getTeam1(), jam);
	}

	/**
	 * Sets or unsets team 2's lead jammer status (if true, also unsets team 1's
	 * LJ status).
	 */
	public void setTeam2LJ(boolean isLJ) {
		Jam jam = getJam();
		setLJ(isLJ, jam.getTeam2(), jam);
	}

	/**
	 * Sets the lead jammer status to the specified value for the specified
	 * team.
	 */
	private void setLJ(boolean isLJ, Team team, Jam jam) {
		if (isLJ && jam.getLeadJammer() != team) {
			jam.setLeadJammer(team);
			notifyObservers(jam);
			MatchControl.getInstance().setUnsavedChanges(true);
		} else if (!isLJ && jam.getLeadJammer() == team) {
			jam.setLeadJammer(null);
			notifyObservers(jam);
			MatchControl.getInstance().setUnsavedChanges(true);
		}
	}

	/**
	 * Returns the currently-selected jam.
	 */
	public Jam getJam() {
		return MatchControl.getInstance().getMatch()
				.getJam(heatIndex, jamIndex);
	}

	/**
	 * Returns the singleton instance of this class.
	 */
	public static JamControl getInstance() {
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
		private static final JamControl INSTANCE = new JamControl();
	}

	/**
	 * Listens for changes in the overall match and updates accordingly.
	 */
	private class MatchObserver implements Observer<Match> {
		public void update(Match match) {

			// make sure the indices are still valid
			Jam jam = getJam();
			if (jam == null) {
				heatIndex = 0;
				jamIndex = 0;
				jam = getJam();
			}

			// update the panel (data may have changed)
			notifyObservers(jam);
		}
	}
}
