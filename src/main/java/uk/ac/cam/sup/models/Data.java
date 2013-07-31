package uk.ac.cam.sup.models;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import uk.ac.cam.sup.util.DataType;

@Embeddable
public class Data implements Cloneable {
	
	@Enumerated(EnumType.STRING)
	private DataType type = DataType.PLAIN_TEXT;
	private String data = "";
	
	public Data(){}
	public Data(boolean isString, String data) {
		if (isString) {
			this.type = DataType.PLAIN_TEXT;
		} else {
			this.type = DataType.EMPTY;
		}
		this.data = data;
	}
	
	public Data(Data old) {
		this.type = old.type;
		this.data = old.data;
	}
	
	@Deprecated
	public boolean isString() {
		return this.type == DataType.PLAIN_TEXT;
	}
	
	public boolean isPlainText() {
		return this.type == DataType.PLAIN_TEXT;
	}
	
	public void setType(DataType dt) {
		this.type = dt;
	}
	
	public String getData() {
		return data;
	}
	
	public void setData(boolean isString, String data) {
		if (isString) {
			this.type = DataType.PLAIN_TEXT;
		} else {
			this.type = DataType.EMPTY;
		}
		
		this.data = (data == null ? null : data.trim());
	}
	
	public Object clone() throws CloneNotSupportedException {
		Data d = (Data) super.clone();
		if (this.data != null) {
			d.data = new String(this.data);
		}
		
		return d;
	}
	
}
