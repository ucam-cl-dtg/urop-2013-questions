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
@Table(name="QuestionSets")
public class QuestionSet {
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	private int id;
	
	private String name;
	private boolean isStarred = false;
	private Date timeStamp;
	
	@ManyToOne
	private User owner;
	
	@Embedded
	@Column(nullable=false)
	private Data plan = new Data();
	
	@ManyToMany
	private Set<Tag> tags = new HashSet<Tag>();
	
	@ManyToMany
	private Set<Question> questions = new HashSet<Question>();
	
	
	@SuppressWarnings("unused")
	private QuestionSet() {}
	public QuestionSet(User owner){
		this.owner = owner;
	}
	
	public int getId(){return id;}
	
	public String getName(){return name;}
	public void setName(String name){this.name = name;}
	
	public boolean isStarred(){return isStarred;}
	public void setStarred(boolean s){isStarred = s;}
	
	public User getOwner(){return owner;}
	
	public Data getPlan(){return plan;}
	public void setPlan(Data plan){this.plan = plan;}
	
	public void setTags(Set<Tag> tags){this.tags = tags;}
	public Set<Tag> getTags() {
		return tags;
	}
	public void addTag(Tag tag){tags.add(tag);}
	public void removeTag(Tag tag){tags.remove(tag);}
	public void removeTagByString(String tag){tags.remove(new Tag(tag));}
	public Set<String> getTagsAsString() {
		Set<String> result = new HashSet<String>();
		for(Tag t : tags){
			result.add(t.getName());
		}
		return result;
	}
	
	public void setQuestions(Set<Question> questions){this.questions = questions;}
	public Set<Question> getQuestions(){return questions;}
	public void addQuestion(Question question){questions.add(question);}
	public void removeQuestion(Question question){questions.remove(question);}
	
	public Date getTimeStamp() { return this.timeStamp; }
	public void setTimeStamp(Date timeStamp) { this.timeStamp = timeStamp; }
	
	@Override
	public boolean equals(Object x){
		if (x == null || !(x instanceof QuestionSet)) {
			return false;
		}
		return ((QuestionSet)x).getId() == getId();
	}
	
	@Override
	public int hashCode(){
		return getId() % 13;
	}
}
