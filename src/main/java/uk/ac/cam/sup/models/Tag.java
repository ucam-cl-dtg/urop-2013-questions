package uk.ac.cam.sup.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Tags")
public class Tag {
	@Id private String name;
	
	public String getName() {
		return this.name;
	}
	
	@SuppressWarnings("unused")
	private Tag(){}
	public Tag(String name) {
		this.name = name;
	}
	
	public boolean equals (Object t) {
		if (!(t instanceof Tag)) {
			return false;
		} else { 
			return ((Tag)t).name.toLowerCase().equals(this.name.toLowerCase());
		}
	}
	
	public int hashCode () {
		return name.toLowerCase().hashCode();
	}
}
