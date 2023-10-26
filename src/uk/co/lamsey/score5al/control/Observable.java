package uk.co.lamsey.score5al.control;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple thread-safe generic Observable class, with the ability to pass the
 * updated object to interested observers.
 * 
 * @see "http://en.wikipedia.org/wiki/Observer_pattern"
 */
public abstract class Observable<T> {

	/**
	 * The list of observers which should be notified of updates to this
	 * Observable object.
	 */
	private List<Observer<T>> observers;

	/**
	 * Initialises the list of observers for this object (initially empty).
	 */
	protected Observable() {
		observers = new ArrayList<Observer<T>>();
	}

	/**
	 * Adds the specified observer to the list of observers of this object.
	 */
	public void addObserver(Observer<T> observer) {
		synchronized (observers) {
			observers.add(observer);
		}
	}

	/**
	 * Removes the specified observer from the list of observers of this object.
	 */
	public void removeObserver(Observer<T> observer) {
		synchronized (observers) {
			observers.remove(observer);
		}
	}

	/**
	 * Notifies all observers that the passed object has been updated.
	 */
	protected void notifyObservers(T updatedObject) {
		synchronized (observers) {
			for (Observer<T> observer : observers) {
				observer.update(updatedObject);
			}
		}
	}
}
