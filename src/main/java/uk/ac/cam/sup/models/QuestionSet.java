package uk.ac.cam.sup.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;

import com.google.inject.OutOfScopeException;

@Entity
@Table(name="QuestionSets")
public class QuestionSet extends Model {
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	private int id;
	
	private String name;
	private boolean isStarred = false;
	
	@Column(nullable=false, columnDefinition = "date default sysdate")
	private Date timeStamp = new Date();
	
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
	
	@OneToMany
	private Set<QuestionPlacement> questions = new TreeSet<QuestionPlacement>();
	
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
	
	public List<Question> getQuestions(){
		List<Question> result = new ArrayList<Question>();
		while (result.size() < questions.size()) { result.add(null); }
		for(QuestionPlacement q: questions) {
			result.set(q.getPlace()-1, q.getQuestion());
		}
		return result;
	}
	public List<Map<String,Object>> getQuestionsAsMaps(boolean shadow) {
		List<Question> questions = getQuestions();
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		for (Question q: questions) {
			result.add(q.toMap());
		}
		return result;
	}
	public List<Map<String,Object>> getQuestionsAsMaps() {
		return getQuestionsAsMaps(true);
	}
	
	public Question getQuestion(int place) {
		for (QuestionPlacement qp: questions) {
			if (qp.getPlace() == place) {
				return qp.getQuestion();
			}
		}
		throw new OutOfScopeException("index: " + place + " size: " + questions.size());
	}
	
	public void addQuestion(Question question) {
		question.use();
		QuestionPlacement qp = new QuestionPlacement(question, questions.size()+1);
		question.save();
		qp.save();
		questions.add(qp);
	}
	
	public void removeQuestion(Question question) {
		for (QuestionPlacement qp: questions) {
			if (qp.getQuestion().equals(question)) {
				this.removeQuestion(qp.getPlace());
				break;
			}
		}
	}
	
	public void removeQuestion(int place) {
		QuestionPlacement[] qarray = questions.toArray(new QuestionPlacement[0]);
		qarray[place-1].getQuestion().unuse();
		qarray[place-1].delete();
		questions.remove(qarray[place-1]);
		
		for (int i = place; i < qarray.length; i++) {
			qarray[i].setPlace(i-1);
			qarray[i].update();
		}
	}
	
	public void addBefore(int place, Question q) {
		q.use();
		QuestionPlacement[] qarray = questions.toArray(new QuestionPlacement[0]);
		for (int i = place; i < qarray.length; i++) {
			qarray[place].setPlace(qarray[place].getPlace()+1);
			qarray[place].update();
		}
	}
	
	public void addAfter(int place, Question q) {
		if (place < questions.size()-1) {
			addBefore(place+1, q);
		} else {
			addQuestion(q);
		}
	}
	
	public Date getTimeStamp() { return (this.timeStamp == null ? new Date(0) : this.timeStamp); }
	public void setTimeStamp(Date timeStamp) { this.timeStamp = timeStamp; }
	
	public int getExpectedDuration() {
		int r = 0;
		
		for (QuestionPlacement q: questions) {
			r += q.getQuestion().getExpectedDuration();
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
}
