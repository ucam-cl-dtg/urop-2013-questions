package uk.ac.cam.sup.models;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.Session;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;

import uk.ac.cam.sup.HibernateUtil;

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
	
	@SuppressWarnings("unused")
	private Integer expectedDuration;
	
	@ManyToOne
	private User owner;
	
	@Embedded
	@Column(nullable=false)
	private Data plan = new Data();
	
	@ManyToMany
	@Cascade(CascadeType.MERGE)
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
	
	//public void setQuestions(Set<Question> questions){this.questions = questions;}
	public Set<Question> getQuestions(){return questions;}
	public Set<Map<String,Object>> getQuestionsAsMaps(boolean shadow) {
		Set<Map<String,Object>> r = new HashSet<Map<String,Object>>();
		
		for (Question q: questions) {
			r.add(q.toMap(shadow));
		}
		
		return r;
	}
	public Set<Map<String,Object>> getQuestionsAsMaps() {
		return getQuestionsAsMaps(true);
	}
	public void addQuestion(Question question) {
		question.use();
		questions.add(question);
	}
	public void removeQuestion(Question question) {
		question.unuse();
		questions.remove(question);
	}
	
	public Date getTimeStamp() { return this.timeStamp; }
	public void setTimeStamp(Date timeStamp) { this.timeStamp = timeStamp; }
	
	public int getExpectedDuration() {
		int r = 0;
		
		for (Question q: questions) {
			r += q.getExpectedDuration();
		}
		this.expectedDuration = r;
		
		return r;
	}
	
	@Override
	public boolean equals(Object x){
		if (x == null || !(x instanceof QuestionSet)) {
			return false;
		}
		return ((QuestionSet)x).getId() == getId();
	}
	
	@Override
	public int hashCode(){
		return id;
	}
	
	public Map<String,Object> toMap(boolean shadow) {
		Map<String,Object> r = new HashMap<String,Object>();
		
		r.put("id", this.id);
		r.put("name", this.name);
		r.put("owner", this.owner);
		//r.put("timeStamp", this.timeStamp); // for direct soy access use soyTimeStamp
		r.put("soyTimeStamp", this.timeStamp.toString());
		r.put("expectedDuration", this.getExpectedDuration());
		r.put("parentid", null); // TODO: implement parent
		r.put("starred", this.isStarred);
		r.put("tags", this.tags);
		r.put("questions", this.getQuestionsAsMaps());
		r.put("plan", (shadow ? null : this.plan));
		
		return r;
	}
	
	public Map<String,Object> toMap() {
		return toMap(true);
	}
	
	public void save() {
		Session session = HibernateUtil.getTransactionSession();
		session.save(this);
	}
	
	public void update() {
		Session session = HibernateUtil.getTransactionSession();
		session.update(this);
	}
}
