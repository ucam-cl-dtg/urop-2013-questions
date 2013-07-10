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
	
	
	public boolean isString() {return isString;}
	
	public String getData() {
		return data;
	}
	
	public void setData(boolean isString, String data) {
		this.isString = isString;
		this.data = data;
	}
	
}
