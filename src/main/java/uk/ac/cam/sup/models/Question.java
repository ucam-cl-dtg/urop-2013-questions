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

import org.hibernate.annotations.GenericGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

@Entity
@Table(name="Questions")
public class Question implements Cloneable {
	@Transient
	private static Logger log = LoggerFactory.getLogger(Question.class);
	
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	private int id;
	
	private boolean isStarred = false;
	private int usageCount = 0;
	private Date timeStamp;
	
	@ManyToOne
	private Question parent;
	
	@ManyToMany
	private Set<Tag> tags = new HashSet<Tag>();
	
	@ManyToOne
	private User owner;
	
	private int expectedDuration = 0;
	
	@Embedded @Column(nullable=false)
	private Data content = new Data();
	
	@Embedded @Column(nullable=false)
	private Data notes = new Data();
	
	@SuppressWarnings("unused")
	private Question() {}
	public Question(User owner){
		this.owner = owner;
	}
	
	public int getId(){return id;}
	
	public boolean isStarred(){return isStarred;}
	public void star(boolean s){isStarred = s;}
	
	public int getUsageCount(){return usageCount;}
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
	//private void setContent(Data c){content = c;}
	
	public Data getNotes(){return notes;}
	//private void setNotes(Data n){notes = n;}
	
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
		}
		return ((Question)x).getId() == getId();
	}
	
	@Override
	public int hashCode(){
		return getId() % 13;
	}
	
	private Question fork(){
		Question result = new Question(this.owner);
		result.isStarred = isStarred;
		result.timeStamp = new Date();
		result.parent = this;
		result.tags = new HashSet<Tag>(tags);
		result.expectedDuration = expectedDuration;
		result.content = new Data(content);
		result.notes = new Data(notes);
		return result;
	}
	private Question forkOnDemand(User editor){
		Question result;
		if(usageCount > 1){
			result = this.fork();
			this.usageCount--;
			result.usageCount = 1;
			result.owner = editor;
		} else {
			result = this;
		}
		return result;
	}
	public Question editContent(Data content, User editor) {
		if(content == null) {
			log.error("The content passed as argument was null. Creating dummy content.");
			content = new Data(true, "Error: no content was passed to method while editing");
		}
		Question result = forkOnDemand(editor);
		result.content = content;
		return result;
	}
	public Question editNotes(Data notes, User editor) {
		if(notes == null) {
			log.error("The notes passed as argument were null. Creating dummy set of notes.");
			notes = new Data(true, "Error: no notes were passed to the method while editing.");
		}
		Question result = forkOnDemand(editor);
		result.notes = notes;
		return result;
	}
	
	public Question use() {
		usageCount++;
		return this;
	}
	public Question disuse() {
		usageCount--;
		return this;
	}
	
	public Object clone() throws CloneNotSupportedException {
		Question q = (Question) super.clone();
		q.content = (Data) this.content.clone();
		if (this.notes != null) {
			q.notes = (Data) this.notes.clone();
		}
		
		return q;
	}
	
	public void shadow() {
		this.notes = null;
	}
	
	public Map<String,Object> toMap(boolean shadowed) {
		Map<String,Object> r =	new HashMap<String,Object>();
		r.put("id", this.id);
		r.put("parentid", (parent == null ? null : this.parent.getId()));
		r.put("timeStamp", this.timeStamp);
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
	
}
