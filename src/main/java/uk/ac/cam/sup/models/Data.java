package uk.ac.cam.sup.models;

import javax.persistence.Embeddable;

@Embeddable
public class Data {
	private Boolean isString = true;
	private String data = "";
	
	public Data(){}
	public Data(boolean isString, String data) {
		this.isString = isString;
		this.data = data;
	}
	// Copy constructor 
	public Data(Data old) {
		this.isString = old.isString;
		this.data = old.data;
	}
	
	
	public boolean isString() {return isString;}
	
	public String getData() {
		return data;
	}
	
	public void setData(boolean isString, String data) {
		this.isString = isString;
		this.data = data;
	}
	
}
