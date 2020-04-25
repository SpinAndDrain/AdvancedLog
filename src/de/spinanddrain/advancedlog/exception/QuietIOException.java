package de.spinanddrain.advancedlog.exception;

public class QuietIOException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance with the specified <b>message</b>.
	 * 
	 * @param message
	 */
	public QuietIOException(String message) {
		super(message);
	}
	
	/**
	 * Creates a new instance with no message.
	 * 
	 */
	public QuietIOException() {
	}
	
}
