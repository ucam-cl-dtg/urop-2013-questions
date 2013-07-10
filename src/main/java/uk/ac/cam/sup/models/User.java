package uk.ac.cam.sup.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name = "users" )
public class User {
	private boolean supervisor;
	private String id;
	
	public User() {}
	
	public User(String i) {
		supervisor = false;
		id = i;
	}
	
	public User(String i, boolean s) {
		id = i;
		supervisor = s;
	}
	
	@Id
	public String getId() {return id;}
	
	public boolean getSupervisor() {return supervisor;}

	public void setId(String i) {id = i;}
	public void setSupervisor(boolean s) {supervisor = s;}
}