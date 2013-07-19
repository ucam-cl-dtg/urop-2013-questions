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
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.sup.form.QuestionEdit;
import uk.ac.cam.sup.queries.QuestionSetQuery;

@Entity()
@Table(name="Questions")
public class Question extends Model implements Cloneable {
	@Transient
	private static Logger log = LoggerFactory.getLogger(Question.class);
	
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	private Integer id;
	
	private boolean isStarred = false;
	private int usageCount = 0;
	private Date timeStamp;
	
	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	private Question parent;
	
	@ManyToMany
	@Cascade(CascadeType.MERGE)
	private Set<Tag> tags = new HashSet<Tag>();
	
	@ManyToOne
	private User owner;
	
	private int expectedDuration = 0;
	
	@Embedded @Column(nullable=false)
	private Data content = new Data(false, null);
	
	@Embedded @Column(nullable=false)
	private Data notes = new Data(false, null);
	
	@SuppressWarnings("unused")
	private Question() {}
	public Question(User owner){
		this.owner = owner;
	}
	
	public Integer getId(){
		return this.id;
	}
	
	public boolean isStarred(){return isStarred;}
	public void star(boolean s){isStarred = s;}
	
	public int getUsageCount(){	return this.usageCount; }
	public void setUsageCount(int c){usageCount = c;}
	
	public Date getTimeStamp(){return timeStamp;}
	public void setTimeStamp(Date d){timeStamp = d;}
	
	public Question getParent(){return parent;}
	public void setParent(Question p){parent = p;}
	
	public User getOwner(){return owner;}
	
	// Duration in minutes
	public int getExpectedDuration(){return expectedDuration;}
	public void setExpectedDuration(int expDuration){expectedDuration = expDuration;}
	
	public Data getContent(){return content;}
	public void setContent(Data c){content = c;}
	
	public Data getNotes(){return notes;}
	public void setNotes(Data n){notes = n;}
	
	public void setTags(Set<Tag> tags){this.tags = tags;}
	public Set<Tag> getTags(){return tags;}
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
	
	@Override
	public boolean equals(Object x){
		if (x == null || !(x instanceof Question)) {
			return false;
		} else if ((this.id == null) ^ (((Question)x).id == null)) {
			return false;
		} else if ((this.id == null) && (((Question)x).id == null)) {
			return ((Question)x) == this;
		}
		return ((Question)x).id == this.id;
	}
	
	@Override
	public int hashCode(){
		return (this.id == null ? 0 : this.id);
	}
	
	private Question fork(QuestionSet qs){
		Question result = new Question(this.owner);
		
		result.isStarred = isStarred;
		result.timeStamp = new Date();
		result.parent = this;
		result.tags = new HashSet<Tag>(tags);
		result.expectedDuration = expectedDuration;
		result.content = new Data(content);
		
		if (this.getOwner().getSupervisor() && !qs.getOwner().getSupervisor()) {
			result.notes = new Data(false, null);
		} else {
			result.notes = new Data(notes);
		}
		
		qs.swapFor(this, result);
		
		result.save();
		qs.update();
		
		return result;
	}
	
	private Question inPlaceEdit(QuestionEdit qe) {
		qe.store(this);
		this.update();
		
		return this;
	}
	
	private Question forkAndEdit(User editor, QuestionEdit qe) {
		Question q = this.fork(QuestionSetQuery.get(qe.getSetId()));
		q.owner = editor;
		
		qe.store(q);
		q.update();
		
		return q;
	}
	
	public Question edit(User editor, QuestionEdit qe) {
		boolean inPlace = (editor.equals(owner))
				&& (qe.isMinor() || (this.usageCount <= 1));
		
		if (inPlace) {
			return this.inPlaceEdit(qe);
		} else {
			return this.forkAndEdit(editor, qe);
		}
	}
	
	public Object clone() throws CloneNotSupportedException {
		Question q = (Question) super.clone();
		q.content = (Data) this.content.clone();
		if (this.notes != null) {
			q.notes = (Data) this.notes.clone();
		}
		
		return q;
	}
	
	public Map<String,Object> toMap(boolean shadowed) {
		Map<String,Object> r =	new HashMap<String,Object>();
		r.put("id", this.id);
		r.put("parentid", (parent == null ? null : this.parent.getId()));
		//r.put("timeStamp", this.timeStamp);
		r.put("soyTimeStamp", timeStamp.toString());
		r.put("owner", this.owner);
		r.put("content", this.content);
		r.put("starred", this.isStarred);
		r.put("expectedDuration", this.expectedDuration);
		r.put("tags", this.tags);
		r.put("notes", (shadowed ? null : this.notes));
		
		return r;
	}
	
	public Map<String,Object> toMap() {
		return toMap(true);
	}
	
	void use() {
		this.usageCount++;
	}
	
	void unuse() {
		this.usageCount--;
	}
	
}
