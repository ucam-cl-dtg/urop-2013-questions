package uk.ac.cam.sup.queries;

import org.hibernate.Criteria;
import org.hibernate.Session;

import uk.ac.cam.sup.HibernateUtil;
import uk.ac.cam.sup.models.Question;

public class QuestionQuery {
	private Criteria criteria;
	
	private QuestionQuery(Criteria c){
		criteria = c; 
	}
	
	public static QuestionQuery all(){
		Session session = HibernateUtil.getSession();
		QuestionQuery qq = new QuestionQuery(session.createCriteria(Question.class));
		return qq;
	}
}
