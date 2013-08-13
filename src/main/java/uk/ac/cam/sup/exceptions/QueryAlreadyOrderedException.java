package uk.ac.cam.sup.exceptions;

public class QueryAlreadyOrderedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8395771330622998116L;

	public QueryAlreadyOrderedException(String message){
		super(message);
	}
}
