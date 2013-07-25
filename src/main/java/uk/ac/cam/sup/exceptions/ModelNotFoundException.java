package uk.ac.cam.sup.exceptions;

public class ModelNotFoundException extends Exception {
	private static final long serialVersionUID = -924676843640098459L;
	
	public ModelNotFoundException(){
		super();
	}
	public ModelNotFoundException(String message){
		super(message);
	}
}
