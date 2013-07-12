package uk.ac.cam.sup.queries;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import uk.ac.cam.sup.HibernateUtil;
import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.models.User;

public class QuestionSetQuery {
	private Criteria criteria;
	private QuestionSetQuery(Criteria criteria) {
		this.criteria = criteria;
	}
	
	public static QuestionSetQuery all() {
		return new QuestionSetQuery (
				HibernateUtil.getSession()
					.createCriteria(QuestionSet.class)
					.createAlias("owner", "o")
					.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
		);
	}
	
	public QuestionSetQuery withTags(List<Tag> taglist) {
		Disjunction d = Restrictions.disjunction();
		for (Tag t: taglist) {
			d.add(Restrictions.eq("t.name", t.getName()));
		}
		criteria.createAlias("tags", "t").add(d);
			
		return this;
	}
	
	public QuestionSetQuery withUsers(List<User> userlist) {
		Disjunction d = Restrictions.disjunction();
		for (User u: userlist) {
			d.add(Restrictions.eq("owner.id", u.getId()));
		}
		criteria.add(d);
		
		return this;
	}
	
	public QuestionSetQuery withStar() {
		criteria.add(Restrictions.eq("isStarred", true));
		return this;
	}
	
	public QuestionSetQuery bySupervisor() {
		criteria
			.add(Restrictions.eq("o.supervisor", true));
		return this;
	}
	
	public QuestionSetQuery byStudent() {
		criteria
		.add(Restrictions.eq("o.supervisor", false));
		return this;
	}
	
	public QuestionSetQuery after(Date date) {
		criteria.add(Restrictions.gt("timeStamp", date));
		return this;
	}
	
	public QuestionSetQuery before(Date date) {
		criteria.add(Restrictions.lt("timeStamp", date));
		return this;
	}
	
	public List list() {
		List l = criteria.list();
		return l;
	}
}
