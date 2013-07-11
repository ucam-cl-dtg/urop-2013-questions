package uk.ac.cam.sup.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="QuestionSets")
public class QuestionSet {
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	private int id;
	
	private String name;
	private boolean isStarred = false;
	
	@ManyToOne
	private User owner;
	
	@Embedded
	@Column(nullable=false)
	private Data plan = new Data();
	
	@ManyToMany
	private Set<Tag> tags = new HashSet<Tag>();
	
	@ManyToMany
	private Set<Question> questions = new HashSet<Question>();
	
	public QuestionSet() {}
	public QuestionSet(User owner){
		this.owner = owner;
	}
	
}
