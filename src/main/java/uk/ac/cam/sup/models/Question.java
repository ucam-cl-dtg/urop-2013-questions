package uk.ac.cam.sup.models;

import java.util.Date;
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
@Table(name="Questions")
public class Question {
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	private int id;
	
	private boolean isStarred = false;
	private int usageCount = 0;
	private Date timeStamp;
	@ManyToOne private Question parent;
	@ManyToMany private Set<Tag> tags = new HashSet<Tag>();
	
	@ManyToOne private User owner;
	private int expectedDuration = 0;
	
	@Embedded @Column(nullable=false)
	private Data content = new Data();
	@Embedded @Column(nullable=false)
	private Data notes = new Data();
	
	public Question() {}
	public Question(User owner){
		//this.owner = owner;
	}
	
	/*public int getId(){return id;}
	
	public boolean getStarred(){return isStarred;}
	public void setStarred(boolean s){isStarred = s;}
	
	public int getUsageCount(){return usageCount;}
	public void setUsageCount(int c){usageCount = c;}
	
	public Date getTimeStamp(){return timeStamp;}
	public void setTimeStamp(Date d){timeStamp = d;}
	
	public Question getParent(){return parent;}
	public void setParent(Question p){parent = p;}
	
	public Set<Question> getForks(){return forks;}
	public void setForks(Set<Question> f){forks = f;}
		
	public Data getContent(){return content;}
	public void setContent(Data c){content = c;}
	
	public Data getNotes(){return notes;}
	public void setNotes(Data n){notes = n;}
	*/
}
