package uk.ac.cam.sup.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.Session;
import org.hibernate.annotations.GenericGenerator;

import uk.ac.cam.sup.HibernateUtil;

@Entity
@Table(name="Placement")
public class QuestionPlacement implements Comparable<QuestionPlacement> {
	
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
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
	@SuppressWarnings("unused")
	private void setQuestion(Question q) {
		this.question = q;
	}
	
	public int getPlace() {
		return this.place;
	}
	public void setPlace(int p) {
		this.place = p;
	}
	
	public int compareTo(QuestionPlacement q) {
		return this.place - q.place;
	}
	
	public void update() {
		Session session = HibernateUtil.getTransactionSession();
		session.update(this);
	}
	
	public void save() {
		Session session = HibernateUtil.getTransactionSession();
		session.save(this);
	}
	
	public void delete() {
		Session session = HibernateUtil.getTransactionSession();
		session.delete(this);
	}
	
}
