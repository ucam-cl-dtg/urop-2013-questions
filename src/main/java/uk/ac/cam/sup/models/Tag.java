package uk.ac.cam.sup.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Tags")
public class Tag extends Model {
	@Id private String name;
	
	public String getName() {
		return this.name;
	}
	
	@SuppressWarnings("unused")
	private Tag(){}
	public Tag(String name) {
		this.name = name;
	}
	
	public static Set<Tag> parseTagString(String taglist) {
		Set<Tag> r = new HashSet<Tag>();
		String[] tagarray = taglist.split(",");
		
		for (String s: tagarray) {
			r.add(new Tag(s.trim()));
		}
		
		return r;
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
