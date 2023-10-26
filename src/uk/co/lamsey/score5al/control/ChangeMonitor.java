package uk.co.lamsey.score5al.control;

/**
 * An observer which should be added to any class which changes data that should
 * be saved to disk.
 */
public class ChangeMonitor<T> implements Observer<T> {

	@Override
	public void update(T updatedObject) {
		MatchControl.getInstance().setUnsavedChanges(true);
	}
}
