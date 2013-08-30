package uk.ac.cam.sup.exceptions;

public class QueryAlreadyOrderedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8395771330622998116L;

	public QueryAlreadyOrderedException(String message){
		super(message);
	}

	public QueryAlreadyOrderedException() {
		super();
	}

	public QueryAlreadyOrderedException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public QueryAlreadyOrderedException(String message, Throwable cause) {
		super(message, cause);
	}

	public QueryAlreadyOrderedException(Throwable cause) {
		super(cause);
	}
		
}
