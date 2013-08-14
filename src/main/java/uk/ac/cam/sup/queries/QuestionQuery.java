package uk.ac.cam.sup.queries;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.sup.exceptions.QueryAlreadyOrderedException;
import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.models.User;
import uk.ac.cam.sup.util.HibernateUtil;

public class QuestionQuery extends Query<Question> {
	private static Logger log = LoggerFactory.getLogger(QuestionQuery.class);
	
	private QuestionQuery(Criteria c){
		log.debug("Constructing new criteria");
		criteria = c; 
	}
	
	public static Question get(int id) {
		Session session = HibernateUtil.getTransactionSession();
		log.debug("Trying to get question with id " + id);
		Question q = (Question) session
				.createQuery("from Question where id = :id")
				.setParameter("id", id)
				.uniqueResult();
		log.debug("Returning question with id " + id);
		return q;
	}
	
	public static QuestionQuery all(){
		// The .setResultTransformer(...) part is responsible for putting Questions back together,
		// which, for ex., contain 2 or more of the tags searched for. (Otherwise you would get
		// back 1 instance of the same object with every of the different tags specified).
		log.debug("New QuestionQuery required. Constructing & returning");
		QuestionQuery qq = new QuestionQuery(HibernateUtil.getTransactionSession()
				.createCriteria(Question.class)	
				.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY));
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
		//criteria.setFetchMode("t", FetchMode.JOIN);
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
		if(ownerIDs == null || !(ownerIDs.size() > 0)) return this;
		
		Disjunction oredUserList = Restrictions.disjunction();
		for(String owner : ownerIDs){
			oredUserList.add(Restrictions.like("owner.id", owner).ignoreCase());
		}
		criteria.add(oredUserList);
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
	
	public QuestionQuery after(Date begin){
		log.debug("Filtering after a date...");
		criteria.add(Restrictions.ge("timeStamp", begin));
		return this;
	}
	public QuestionQuery before(Date date){
		try{
			Question q = get(1);
			System.out.println("Date provided: " + date + "      Question date: " + q.getTimeStamp());
		}catch(Exception e){
			System.out.println("Exception " + e.getMessage());
		}
		
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
	
	public QuestionQuery withParents(List<Integer> parents){
		log.debug("Filtering with parents...");
		if(parents == null || !(parents.size() > 0)) return this;
		Disjunction oredParentList = Restrictions.disjunction();
		for(int pid: parents){
			oredParentList.add(Restrictions.eq("parent.id", pid));
		}
		criteria.add(oredParentList);
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
	
	public QuestionQuery maxResults(int max){
		criteria.setMaxResults(max);
		return this;
	}
	
	public QuestionQuery offset(int offset){
		criteria.setFirstResult(offset);
		return this;
	}
	
	public int size() throws QueryAlreadyOrderedException {
		try{
			int result = ((Long)criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
			criteria.setProjection(null);
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			return result;
		} catch(Exception e) {
			throw new QueryAlreadyOrderedException("Order was already applied to this QuestionQuery!");
		}

	}

}
