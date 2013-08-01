package uk.ac.cam.sup.models;

import java.util.UUID;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import uk.ac.cam.sup.util.DataType;

@Embeddable
public class Data implements Cloneable {
	
	@Enumerated(EnumType.STRING)
	private DataType type = DataType.PLAIN_TEXT;
	private String data = "";
	private String description = "";
	
	public Data(){}
	
	@Deprecated
	public Data(boolean isString, String data) {
		if (isString) {
			this.type = DataType.PLAIN_TEXT;
		} else {
			this.type = DataType.EMPTY;
		}
		this.data = data;
	}
	
	public Data(DataType type, String data) {
		this.type = type;
		this.data = data;
	}
	
	public Data(DataType type, String data, String description) {
		this.type = type;
		this.data = data;
		this.description = description;
	}
	
	public Data(String type, String data, byte[] file, String description, boolean forceLoad) {
		this.type = DataType.valueOf(type);
		if (this.type == DataType.FILE) {
			String[] name = this.description.split(".");
			String extension = name[name.length-1];
			String filename = "data-"+UUID.randomUUID().toString()+extension;
			
			try {
				// TODO: load
				this.data = filename;
			} catch (Exception e) {
				if (forceLoad) {
					throw e;
				} else {
					this.data = null;
				}
			}

			this.description = description;
		} else {
			this.data = data;
			this.description = "";
		}
	}
	
	public Data(Data old) {
		this.type = old.type;
		this.data = old.data;
		this.description = old.description;
	}
	
	public String getType() {
		return this.type.toString();
	}
	
	@Deprecated
	public boolean isString() {
		return this.type == DataType.PLAIN_TEXT;
	}
	
	public void setType(DataType dt) {
		this.type = dt;
	}
	
	public String getData() {
		return data;
	}
	
	public void setDescription(String desc) {
		this.description = desc;
	}
	
	public String getDescription() {
		return description;
	}
	
	@Deprecated
	public void setData(boolean isString, String data) {
		if (isString) {
			this.type = DataType.PLAIN_TEXT;
		} else {
			this.type = DataType.EMPTY;
		}
		
		this.data = (data == null ? null : data.trim());
	}
	
	public void setData(DataType type, String data) {
		this.type = type;
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
