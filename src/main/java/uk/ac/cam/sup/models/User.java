package uk.ac.cam.sup.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name = "users" )
public class User extends Model {
	private boolean supervisor;
	@Id private String id;
	
	public User() {}
	
	public User(String i) {
		supervisor = false;
		id = i;
	}
	
	public User(String i, boolean s) {
		id = i;
		supervisor = s;
	}
	
	public String getId() {return id;}
	
	public boolean getSupervisor() {return supervisor;}

	public void setId(String i) {id = i;}
	public void setSupervisor(boolean s) {supervisor = s;}
	
	@Override
	public boolean equals(Object user){
		if (user == null || !(user instanceof User)) {
			return false;
		}
		return getId().equals(((User)user).getId());
	}
	
	@Override
	public int hashCode(){
		return 0;
	}
}