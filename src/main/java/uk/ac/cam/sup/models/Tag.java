package uk.ac.cam.sup.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Tags")
public class Tag {
	@Id String name;
	
	public Tag(){}
	
	public Tag(String name) {
		this.name = name;
	}
	
	public String getName(){return name;}
}
