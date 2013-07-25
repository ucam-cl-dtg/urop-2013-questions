package uk.ac.cam.sup.exceptions;

public class ModelNotFoundException extends Exception {
	public ModelNotFoundException(){
		super();
	}
	public ModelNotFoundException(String message){
		super(message);
	}
}
