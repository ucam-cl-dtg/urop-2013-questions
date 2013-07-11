package uk.ac.cam.sup.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.Session;

import uk.ac.cam.sup.HibernateUtil;

@Entity
@Table(name="Tags")
public class Tag {
	@Id private String name;
	
	public String getName() {
		return this.name;
	}
	
	private Tag(){}
	
	public Tag(String name) {
		this.name = name;
	}

	public static Tag fromString(String name) {
		Session session = HibernateUtil.getTransaction();
		Tag t = (Tag) session.createQuery("from Tag where name = ?")
			.setString(0, name)
			.uniqueResult();
		
		if (t == null) {
			t = new Tag(name);
			session.save(t);
			session.getTransaction().commit();
		}
		
		return t;
	}
	
	public boolean equals (Object t) {
		if (!(t instanceof Tag)) {
			return false;
		} else { 
			return ((Tag)t).name.equals(this.name);
		}
	}
	
	public int hashCode () {
		return name.hashCode();
	}
}
