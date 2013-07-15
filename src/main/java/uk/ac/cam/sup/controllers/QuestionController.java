package uk.ac.cam.sup.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.sup.HibernateUtil;
import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.queries.QuestionQuery;
import uk.ac.cam.sup.util.SearchTerm;
import uk.ac.cam.sup.util.WorldStrings;

import com.google.common.collect.ImmutableMap;
import com.googlecode.htmleasy.ViewWith;

@Path(WorldStrings.URL_PREFIX + "/q")
public class QuestionController {
	private static Logger log = LoggerFactory.getLogger(QuestionController.class);
	
	@GET
	@Path("/search")
	@ViewWith("/soy/search.main")
	public Map<String, ?> searchQuestionsView(
			@QueryParam("tags") String tags,
			@QueryParam("owners") String owners,
			@QueryParam("star") Boolean star,
			@QueryParam("supervisor") Boolean supervisor,
			@QueryParam("after") Long after,
			@QueryParam("before") Long before,
			@QueryParam("usagemin") Integer usageMin,
			@QueryParam("usagemax") Integer usageMax,
			@QueryParam("parent") Integer parentId,
			@QueryParam("durmax") Integer durMax,
			@QueryParam("durmin") Integer durMin){
		
		SearchTerm st = new SearchTerm(tags, owners, star, supervisor, after, 
				before, usageMin, usageMax, parentId, durMax, durMin);
		List<Question> filteredQuestions = getFilteredQuestions(st);
		
		return ImmutableMap.of("questions", filteredQuestions, "searchTerms", st);
		//return ImmutableMap.of("st", st);
	}
	
	@GET
	@Path("/json")
	@Produces("application/json")
	public List<Question> produceFilteredJSON(
			@QueryParam("tags") String tags,
			@QueryParam("owners") String owners,
			@QueryParam("star") Boolean star,
			@QueryParam("supervisor") Boolean supervisor,
			@QueryParam("after") Long after,
			@QueryParam("before") Long before,
			@QueryParam("usagemin") Integer usageMin,
			@QueryParam("usagemax") Integer usageMax,
			@QueryParam("parent") Integer parentId,
			@QueryParam("durmax") Integer durMax,
			@QueryParam("durmin") Integer durMin
			){
		
		SearchTerm st = new SearchTerm(tags, owners, star, supervisor, after, 
				before, usageMin, usageMax, parentId, durMax, durMin);
		
		return getFilteredQuestions(st);
	}
	
	private List<Question> getFilteredQuestions(SearchTerm st){
		log.debug("Getting new QuestionQuery");
		QuestionQuery qq = QuestionQuery.all();
		
		log.debug("Filtering for tags");
		if(st.getTags() != null){
			List<String> lTags = Arrays.asList(st.getTags().split(","));
			qq.withTagNames(lTags);
		}
		
		log.debug("Filtering for owners");
		if(st.getOwners() != null){
			List<String> lUsers = Arrays.asList(st.getOwners().split(","));
			qq.withUserIDs(lUsers);
		}
		
		log.debug("Filtering for star, role...");
		if(st.getStar() != null && st.getStar()) { qq.withStar(); }
		if(st.getStar() != null && !st.getStar()) { qq.withoutStar(); }
		if(st.getSupervisor() != null && st.getSupervisor()) { qq.bySupervisor(); }
		if(st.getSupervisor() != null && !st.getSupervisor()) { qq.byStudent(); }
		
		log.debug("Filtering for date...");
		if(st.getAfter() != null) { qq.after(new Date(st.getAfter())); }
		if(st.getBefore() != null) { qq.before(new Date(st.getBefore())); }
		
		log.debug("Filtering for usages");
		if(st.getUsageMin() != null) { qq.minUsages(st.getUsageMin()); }
		if(st.getUsageMax() != null) { qq.maxUsages(st.getUsageMax()); }
		
		log.debug("Fileting for parentID");
		if(st.getParentId() != null) { qq.withParent(st.getParentId()); }
		
		log.debug("Filtering for duration");
		if(st.getDurMax() != null) { qq.maxDuration(st.getDurMax()); }
		if(st.getDurMin() != null) { qq.minDuration(st.getDurMin()); }
			
		return qq.list();
	}

	private Question getQuestion(int id) {
		Session session = HibernateUtil.getTransaction();
		
		Question r = (Question) session
				.createQuery("from Question where id = :id")
				.setParameter("id", id)
				.uniqueResult();
		
		session.getTransaction().commit();
		return r;
	}
	
	@GET
	@Path("/{id}/json")
	@Produces("application/json")
	public Question produceSingleQuestionJSON(@PathParam("id") int id) {
		return getQuestion(id);
	}
	
	@GET
	@Path("/{id}/history/json")
	@Produces("application/json")
	public Map<String,?> produceHistoryJSON(@PathParam("id") int id) {
		List<Question> history = new ArrayList<Question>();
		
		for (Question q = getQuestion(id); q != null; q = q.getParent()) {
			history.add(q);
		}
		
		return ImmutableMap.of("history",history);
	}
	
	@GET
	@Path("/{id}/forks/json")
	@Produces("application/json")
	public List<?> produceForksJSON(@PathParam("id") int id) {
		return QuestionQuery.all().withParent(id).list();
	}
	
	
}
