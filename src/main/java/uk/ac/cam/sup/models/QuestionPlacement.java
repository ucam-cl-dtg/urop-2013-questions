package uk.ac.cam.sup.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="Placement")
public class QuestionPlacement extends Model 
		implements Comparable<QuestionPlacement>, Cloneable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="placementIdGen")
	@SequenceGenerator(name="placementIdGen", sequenceName="PLACEMENT_SEQ", allocationSize=1)
	private int id;

	@OneToOne
	private Question question;
	
	private int place;
	
	@SuppressWarnings("unused")
	private QuestionPlacement() {}
	
	public QuestionPlacement(Question q, int place) {
		this.question = q;
		this.place = place;
	}
	
	public Question getQuestion() {
		return this.question;
	}

	public void setQuestion(Question q) {
		this.question = q;
	}
	
	public int getPlace() {
		return this.place;
	}
	@SuppressWarnings("unused")
	private void setPlace(int p) {
		this.place = p;
	}
	
	public int compareTo(QuestionPlacement q) {
		return this.place - q.place;
	}
	
	public void moveUp() {
		this.place --;
	}
	
	public void moveDown() {
		this.place ++;
	}
	
	public Object clone() throws CloneNotSupportedException {
		QuestionPlacement newQP = (QuestionPlacement) super.clone();
		newQP.id = 0;

		return newQP;
	}
	
}
