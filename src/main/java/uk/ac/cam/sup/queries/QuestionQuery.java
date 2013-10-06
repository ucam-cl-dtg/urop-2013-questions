package uk.ac.cam.sup.queries;

import java.util.ArrayList;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.cl.dtg.teaching.hibernate.HibernateUtil;
import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.models.User;

public class QuestionQuery extends Query<Question> {
	private static Logger log = LoggerFactory.getLogger(QuestionQuery.class);
	
	private QuestionQuery(Criteria criteria){
		super(Question.class);
		
		log.debug("Constructing new criteria");;
		this.criteria = criteria;
	}

	public static Question get(int id) {
		Session session = HibernateUtil.getInstance().getSession();
		log.debug("Trying to get question with id " + id);
		Question q = (Question) session
				.createQuery("from Question where id = :id")
				.setParameter("id", id)
				.uniqueResult();
				
		log.debug("Returning question with id " + id);
		return q;
	}
	
	public static QuestionQuery all(){
		log.debug("New QuestionQuery required. Constructing & returning");
		
		ProjectionList projectionList = Projections.projectionList()
			.add(Projections.groupProperty("id"))
			.add(Projections.count("id").as("count"))
			.add(Projections.property("id").as("id"))
			.add(Projections.groupProperty("isStarred"))
			.add(Projections.groupProperty("timeStamp"));
		
		Criteria c = HibernateUtil.getInstance().getSession()
			.createCriteria(Question.class)
			.setProjection(projectionList)
			.addOrder(Order.desc("isStarred"))
			.addOrder(Order.desc("count"))
			.addOrder(Order.desc("timeStamp"))
			.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		
		QuestionQuery qq = new QuestionQuery(c);
		
		log.debug("Successfully created, now returning");
		return qq;
	}
	
	
	public QuestionQuery withTags(List<Tag> tags){
		log.debug("Filtering withTags...");
		List<String> tagNames = new ArrayList<String>();
		for(Tag tag : tags) {
			tagNames.add(tag.getName());
		}
		modified = true;
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
		//criteria.setFetchMode("t", FetchMode.JOIN);
		modified = true;
		return this;
	}
	
	public QuestionQuery withOwners(List<User> owners){
		log.debug("Filtering withUsers...");
		List<String> ownerIDs = new ArrayList<String>();
		for(User owner : owners) {
			ownerIDs.add(owner.getId());
		}
		modified = true;
		return withUserIDs(ownerIDs);
	}
	public QuestionQuery withUserIDs(List<String> ownerIDs) {
		log.debug("Filtering witUserIDs...");
		if(ownerIDs == null || !(ownerIDs.size() > 0)) return this;
		
		Disjunction oredUserList = Restrictions.disjunction();
		for(String owner : ownerIDs){
			oredUserList.add(Restrictions.like("owner.id", owner).ignoreCase());
		}
		criteria.add(oredUserList);
		modified = true;
		return this;
	}
	
	public QuestionQuery withStar(){
		log.debug("Filtering withStar...");
		criteria.add(Restrictions.eq("isStarred", true));
		modified = true;
		return this;
	}
	public QuestionQuery withoutStar(){
		log.debug("Filtering withoutStar...");
		criteria.add(Restrictions.eqOrIsNull("isStarred", false));
		modified = true;
		return this;
	}
	
	public QuestionQuery bySupervisor(){
		log.debug("Filtering bySupervisor (no student questions)...");
		criteria.createAlias("owner", "o_sup").add(Restrictions.eq("o_sup.supervisor", true));
		modified = true;
		return this;
	}
	public QuestionQuery byStudent(){
		log.debug("Filtering byStudent (no supervisor questions)...");
		criteria.createAlias("owner", "o_stu").add(Restrictions.eq("o_stu.supervisor", false));
		modified = true;
		return this;
	}
	
	public QuestionQuery after(Date begin){
		log.debug("Filtering after a date...");
		criteria.add(Restrictions.ge("timeStamp", begin));
		modified = true;
		return this;
	}
	public QuestionQuery before(Date date){
		log.debug("Filtering before a date...");
		criteria.add(Restrictions.le("timeStamp", date));
		modified = true;
		return this;
	}
	
	public QuestionQuery minUsages(int uCount){
		log.debug("Filtering by minUsages...");
		criteria.add(Restrictions.ge("usageCount", uCount));
		modified = true;
		return this;
	}
	public QuestionQuery maxUsages(int uCount){
		log.debug("Filtering by maxUsages...");
		criteria.add(Restrictions.le("usageCount", uCount));
		modified = true;
		return this;
	}
	
	
	public QuestionQuery withParent(int parentID) {
		log.debug("Filtering by parentID...");
		criteria.add(Restrictions.eq("parent.id", parentID));
		modified = true;
		return this;
	}
	
	public QuestionQuery withParents(List<Integer> parents){
		log.debug("Filtering with parents...");
		if(parents == null || !(parents.size() > 0)) return this;
		Disjunction oredParentList = Restrictions.disjunction();
		for(int pid: parents){
			oredParentList.add(Restrictions.eq("parent.id", pid));
		}
		criteria.add(oredParentList);
		modified = true;
		return this;
	}
	
	public QuestionQuery minDuration(int minutes){
		log.debug("Filtering by minDuration...");
		criteria.add(Restrictions.ge("expectedDuration", minutes));
		modified = true;
		return this;
	}
	public QuestionQuery maxDuration(int minutes){
		log.debug("Filtering by maxDuration...");
		criteria.add(Restrictions.le("expectedDuration", minutes));
		modified = true;
		return this;
	}
}
