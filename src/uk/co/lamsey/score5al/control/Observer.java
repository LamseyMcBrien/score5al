package uk.co.lamsey.score5al.control;

/**
 * A simple generic Observer interface, with the ability to receive the updated
 * object on an update notification.
 * 
 * @see "http://en.wikipedia.org/wiki/Observer_pattern"
 */
public interface Observer<T> {

	/**
	 * Signals that the passed object has been updated.
	 */
	public void update(T updatedObject);
}
