package uk.ac.cam.sup.queries;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.sup.HibernateUtil;
import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.models.User;

public class QuestionQuery {
	private Criteria criteria;
	private static Logger log = LoggerFactory.getLogger(QuestionQuery.class);
	
	private QuestionQuery(Criteria c){
		log.debug("Constructing new criteria");
		criteria = c; 
	}
	
	@SuppressWarnings("unchecked")
	public List<Question> list(){
		log.debug("Returning list of results");
		return criteria.list();
	}
	
	public List<Map<String,Object>> maplist(boolean shadowed) {
		List<Question> ql = this.list();
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		
		for (Question q: ql) {
			result.add(q.toMap(shadowed));
		}
		
		return result;
	}
	
	public List<Map<String,Object>> maplist() {
		return this.maplist(true);
	}
	
	public static Question get(int id) {
		Session session = HibernateUtil.getTransaction();
		
		Question q = (Question) session
				.createQuery("from Question where id = :id")
				.setParameter("id", id)
				.uniqueResult();
		
		return q;
	}
	
	public static QuestionQuery all(){
		// The .setResultTransformer(...) part is responsible for putting Questions back together,
		// which, for ex., contain 2 or more of the tags searched for. (Otherwise you would get
		// back 1 instance of the same object with every of the different tags specified).
		log.debug("New QuestionQuery required. Constructing & returning");
		QuestionQuery qq = new QuestionQuery(HibernateUtil.getSession()
				.createCriteria(Question.class)	
				.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY));
		qq.criteria.addOrder(Order.desc("timeStamp"));
		log.debug("Successfully created, now returning");
		return qq;
	}
	
	public QuestionQuery withTags(List<Tag> tags){
		log.debug("Filtering withTags...");
		List<String> tagNames = new ArrayList<String>();
		for(Tag tag : tags) {
			tagNames.add(tag.getName());
		}
		return withTagNames(tagNames);
	}
	public QuestionQuery withTagNames(List<String> tagNames) {
		log.debug("Filtering withTagNames...");
		if (tagNames == null || !(tagNames.size() > 0)) return this;
		
		Disjunction oredTagList = Restrictions.disjunction();
		for(String tag : tagNames){
			oredTagList.add(Restrictions.like("t.name", tag).ignoreCase());
		}
		criteria.createAlias("tags", "t");
		criteria.add(oredTagList);
		
		return this;
	}
	
	public QuestionQuery withOwners(List<User> owners){
		log.debug("Filtering withUsers...");
		List<String> ownerIDs = new ArrayList<String>();
		for(User owner : owners) {
			ownerIDs.add(owner.getId());
		}
		return withUserIDs(ownerIDs);
	}
	public QuestionQuery withUserIDs(List<String> ownerIDs) {
		log.debug("Filtering witUserIDs...");
		Disjunction oredUserList = Restrictions.disjunction();
		for(String owner : ownerIDs){
			oredUserList.add(Restrictions.like("owner.id", owner).ignoreCase());
		}
		if (ownerIDs.size() > 0) criteria.add(oredUserList);
		return this;
	}
	
	public QuestionQuery withStar(){
		log.debug("Filtering withStar...");
		criteria.add(Restrictions.eq("isStarred", true));
		return this;
	}
	public QuestionQuery withoutStar(){
		log.debug("Filtering withoutStar...");
		criteria.add(Restrictions.eqOrIsNull("isStarred", false));
		return this;
	}
	
	public QuestionQuery bySupervisor(){
		log.debug("Filtering bySupervisor (no student questions)...");
		criteria.createAlias("owner", "o_sup").add(Restrictions.eq("o_sup.supervisor", true));
		return this;
	}
	public QuestionQuery byStudent(){
		log.debug("Filtering byStudent (no supervisor questions)...");
		criteria.createAlias("owner", "o_stu").add(Restrictions.eq("o_stu.supervisor", false));
		return this;
	}
	
	public QuestionQuery after(Date date){
		log.debug("Filtering after a date...");
		criteria.add(Restrictions.ge("timeStamp", date));
		return this;
	}
	public QuestionQuery before(Date date){
		log.debug("Filtering before a date...");
		criteria.add(Restrictions.le("timeStamp", date));
		return this;
	}
	
	public QuestionQuery minUsages(int uCount){
		log.debug("Filtering by minUsages...");
		criteria.add(Restrictions.ge("usageCount", uCount));
		return this;
	}
	public QuestionQuery maxUsages(int uCount){
		log.debug("Filtering by maxUsages...");
		criteria.add(Restrictions.le("usageCount", uCount));
		return this;
	}
	
	
	public QuestionQuery withParent(int parentID) {
		log.debug("Filtering by parentID...");
		criteria.add(Restrictions.eq("parent.id", parentID));
		return this;
	}
	
	public QuestionQuery minDuration(int minutes){
		log.debug("Filtering by minDuration...");
		criteria.add(Restrictions.ge("expectedDuration", minutes));
		return this;
	}
	public QuestionQuery maxDuration(int minutes){
		log.debug("Filtering by maxDuration...");
		criteria.add(Restrictions.le("expectedDuration", minutes));
		return this;
	}
}
