package uk.ac.cam.sup.queries;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import uk.ac.cam.sup.HibernateUtil;
import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.models.User;

public class QuestionQuery {
	private Criteria criteria;
	
	private QuestionQuery(Criteria c){
		criteria = c; 
	}
	
	@SuppressWarnings("unchecked")
	public List<Question> list(){
		return criteria.list();
	}
	
	public static QuestionQuery all(){
		// The .setResultTransformer(...) part is responsible for putting Questions back together,
		// which, for ex., contain 2 or more of the tags searched for. (Otherwise you would get
		// back 1 instance of the same object with every of the different tags specified).
		QuestionQuery qq = new QuestionQuery(HibernateUtil.getSession()
				.createCriteria(Question.class)	
				.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY));
		return qq;
	}
	
	public QuestionQuery withTags(List<Tag> tags) {
		if (tags == null || !(tags.size() > 0) ) return this;
		
		Disjunction oredTagList = Restrictions.disjunction();
		for(Tag tag : tags){
			oredTagList.add(Restrictions.like("t.name", tag.getName()));
		}
		criteria.createAlias("tags", "t");
		criteria.add(oredTagList);
		
		return this;
	}
	
	public QuestionQuery withUsers(List<User> users) {
		Disjunction oredUserList = Restrictions.disjunction();
		for(User user : users){
			oredUserList.add(Restrictions.like("owner.id", user.getId()));
		}
		if (users.size() > 0) criteria.add(oredUserList);
		return this;
	}
	
	public QuestionQuery withStar(){
		criteria.add(Restrictions.eq("isStarred", true));
		return this;
	}
	public QuestionQuery withoutStar(){
		criteria.add(Restrictions.eqOrIsNull("isStarred", false));
		return this;
	}
	
	public QuestionQuery bySupervisor(){
		criteria.createAlias("owner", "o_sup").add(Restrictions.eq("o_sup.supervisor", true));
		return this;
	}
	public QuestionQuery byStudent(){
		criteria.createAlias("owner", "o_stu").add(Restrictions.eq("o_stu.supervisor", false));
		return this;
	}
	
	public QuestionQuery after(Date date){
		criteria.add(Restrictions.ge("timeStamp", date));
		return this;
	}
	public QuestionQuery before(Date date){
		criteria.add(Restrictions.le("timeStamp", date));
		return this;
	}
	
	public QuestionQuery moreUsersThan(int uCount){
		criteria.add(Restrictions.ge("usageCount", uCount));
		return this;
	}
	public QuestionQuery lessUsersThan(int uCount){
		criteria.add(Restrictions.le("usageCount", uCount));
		return this;
	}
	
	public QuestionQuery withParent(Question p) {
		criteria.add(Restrictions.eq("parent.id", p.getId()));
		return this;
	}
	
	public QuestionQuery longerDurationThan(int minutes){
		criteria.add(Restrictions.ge("expectedDuration", minutes));
		return this;
	}
	public QuestionQuery shorterDurationThan(int minutes){
		criteria.add(Restrictions.le("expectedDuration", minutes));
		return this;
	}
}
