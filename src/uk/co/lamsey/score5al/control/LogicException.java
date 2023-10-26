package uk.co.lamsey.score5al.control;

/**
 * A standard exception thrown by all logic methods to indicate that a problem
 * occurred and the user needs to be notified.
 */
public class LogicException extends Exception {

	/**
	 * Creates a new LogicException with the specified message and root cause.
	 */
	public LogicException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates a new LogicException with the specified message.
	 */
	public LogicException(String message) {
		super(message);
	}

	/**
	 * Creates a new LogicException with the specified root cause.
	 */
	public LogicException(Throwable cause) {
		super(cause);
	}
}
