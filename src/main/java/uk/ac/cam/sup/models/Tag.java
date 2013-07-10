package uk.ac.cam.sup.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Tags")
public class Tag {
	@Id String name;
	
	public Tag(){}
}
