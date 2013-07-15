package uk.ac.cam.sup.models;

import javax.persistence.Embeddable;

@Embeddable
public class Data implements Cloneable {
	private Boolean isString = true;
	private String data = "";
	
	public Data(){}
	public Data(boolean isString, String data) {
		this.isString = isString;
		this.data = data;
	}
	
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
	
	public Object clone() throws CloneNotSupportedException {
		Data d = (Data) super.clone();
		if (this.data != null) {
			d.data = new String(this.data);
		}
		
		return d;
	}
	
}
