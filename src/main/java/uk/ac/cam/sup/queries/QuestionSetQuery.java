package uk.ac.cam.sup.queries;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToEntityMapResultTransformer;

import uk.ac.cam.cl.dtg.teaching.hibernate.HibernateUtil;
import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.models.User;

public class QuestionSetQuery extends Query<QuestionSet> {
	//private static Logger log = LoggerFactory.getLogger(QuestionSetQuery.class);
	
	private QuestionSetQuery(Criteria criteria) {
		super(QuestionSet.class);
		this.criteria = criteria;
	}
	
	public static QuestionSetQuery all() {
		ProjectionList projectionList = Projections.projectionList()
				.add(Projections.groupProperty("id"))
				.add(Projections.count("id").as("count"))
				.add(Projections.property("id").as("id"))
				.add(Projections.groupProperty("isStarred"))
				.add(Projections.groupProperty("timeStamp"));
			
		Criteria c = HibernateUtil.getInstance().getSession()
				.createCriteria(QuestionSet.class)
				.setProjection(projectionList)
				.addOrder(Order.desc("isStarred"))
				.addOrder(Order.desc("count"))
				.addOrder(Order.desc("timeStamp"))
				.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
			
		QuestionSetQuery qsq = new QuestionSetQuery(c);
		
		return qsq;
	}
	
	public static QuestionSet get(int id) {
		Session session = HibernateUtil.getInstance().getSession();

		QuestionSet qs = (QuestionSet) session
				.createQuery("from QuestionSet where id = :id")
				.setParameter("id", id)
				.uniqueResult();
		
		return qs;
	}
	
	public QuestionSetQuery withTags(List<Tag> taglist) {
		Disjunction d = Restrictions.disjunction();
		for (Tag t: taglist) {
			d.add(Restrictions.eq("t.name", t.getName()).ignoreCase());
		}
		criteria.createAlias("tags", "t").add(d);
		
		modified = true;
		
		return this;
	}
	
	public QuestionSetQuery withOwners(List<User> userlist) {
		Disjunction d = Restrictions.disjunction();
		for (User u: userlist) {
			d.add(Restrictions.eq("owner.id", u.getId()).ignoreCase());
		}
		criteria.add(d);
		
		modified = true;
		
		return this;
	}
	
	public QuestionSetQuery withUser(User user){
		criteria.add(Restrictions.eq("owner.id", user.getId()).ignoreCase());
		modified = true;
		return this;
	}
	
	public QuestionSetQuery withStar() {
		criteria.add(Restrictions.eq("isStarred", true));
		modified = true;
		return this;
	}
	public QuestionSetQuery withoutStar() {
		criteria.add(Restrictions.eq("isStarred", false));
		modified = true;
		return this;
	}
	
	public QuestionSetQuery bySupervisor() {
		criteria.add(Restrictions.eq("o.supervisor", true));
		modified = true;
		return this;
	}
	
	public QuestionSetQuery byStudent() {
		criteria.add(Restrictions.eq("o.supervisor", false));
		modified = true;
		return this;
	}
	
	public QuestionSetQuery after(Date date) {
		criteria.add(Restrictions.gt("timeStamp", date));
		modified = true;
		return this;
	}
	
	public QuestionSetQuery before(Date date) {
		criteria.add(Restrictions.lt("timeStamp", date));
		modified = true;
		return this;
	}
	
	public QuestionSetQuery maxDuration(int mins) {
		criteria.add(Restrictions.le("expectedDuration", mins));
		modified = true;
		return this;
	}
	
	public QuestionSetQuery minDuration(int mins) {
		criteria.add(Restrictions.ge("expectedDuration", mins));
		modified = true;
		return this;
	}
	
	public QuestionSetQuery have(Question q) {
		this.have(q.getId());
		modified = true;
		return this;
	}
	public QuestionSetQuery have(int qID) {
		criteria
			.createAlias("questions", "qp")
			.createAlias("qp.question", "q")
			.add(Restrictions.eq("q.id", qID));
		modified = true;
		return this;
	}
}
