package uk.ac.cam.sup.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.models.User;
import uk.ac.cam.sup.queries.QuestionQuery;
import uk.ac.cam.sup.queries.QuestionSetQuery;
import uk.ac.cam.sup.util.WorldStrings;

import com.google.common.collect.ImmutableMap;

@Path(WorldStrings.URL_PREFIX + "/sets")
public class QuestionSetController {
	
	@Context
	private HttpServletRequest request;
	
	private static Logger log = LoggerFactory.getLogger(QuestionSetController.class);
	
	@GET
	@Path("/json")
	@Produces("application/json")
	public List<?> produceFilteredJSON (
			@QueryParam("tags") String tags,
			@QueryParam("owners") String users,
			@QueryParam("star") boolean star,
			@QueryParam("supervisor") Boolean supervisor,
			@QueryParam("after") Long after,
			@QueryParam("before") Long before,
			@QueryParam("durmin") Integer minduration,
			@QueryParam("durmax") Integer maxduration
	) {
		QuestionSetQuery query = QuestionSetQuery.all();
		
		if (tags != null) {
			String[] tagstrings = tags.split(",");
			List<Tag> tagset = new ArrayList<Tag>();
			for (String t: tagstrings) {
				tagset.add(new Tag(t));
			}
			query.withTags(tagset);
		}
		
		if (users != null) {
			String[] userstrings = users.split(",");
			List<User> userset = new ArrayList<User>();
			for (String u: userstrings) {
				userset.add(new User(u));
			}
			query.withUsers(userset);
		}
		
		if (star) {	query.withStar(); }
		if (supervisor != null) {
			if (supervisor) { query.bySupervisor(); }
			else { query.byStudent(); }
		}
		
		if (after != null) { query.after(new Date(after)); }
		if (before != null) { query.before(new Date(before)); }
		
		if (minduration != null) { query.minDuration(minduration); }
		if (maxduration != null) { query.maxDuration(maxduration); }
		
		return query.maplist(false);
	}
	
	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Map<String,Object> showSingleSet(@PathParam("id") int id) {
		return QuestionSetQuery.get(id).toMap(false);
	}
	
	@GET
	@Path("/{id}/json")
	@Produces("application/json")
	public Map<String,Object> produceSingleSetJSON(@PathParam("id") int id) {
		return QuestionSetQuery.get(id).toMap(false);
	}
	
	@GET
	@Path("/mysets")
	@Produces("application/json")
	public Map<?,?> produceMySets(@QueryParam("contains") Integer questionID){
		
		String userID = (String)request.getSession().getAttribute("RavenRemoteUser");
		List<User> userlist = new ArrayList<User>();
		userlist.add(new User(userID));
		
		QuestionSetQuery starredList = QuestionSetQuery.all().withUsers(userlist).withStar();
		QuestionSetQuery nostarList = QuestionSetQuery.all().withUsers(userlist).withoutStar();
		starredList.getCriteria().addOrder(Order.asc("name"));
		nostarList.getCriteria().addOrder(Order.asc("name"));
		
		List<QuestionSet> resultSets = new ArrayList<QuestionSet>();
		resultSets.addAll(starredList.list());
		resultSets.addAll(nostarList.list());
		
		if(questionID == null) {
			return ImmutableMap.of("sets", resultSets);
		} else {
			List<Map<String,?>> maplist = new ArrayList<Map<String,?>>();
			log.debug("Trying to get all questionSets with those specially marked containing question " + questionID);
			List<QuestionSet> haveQuestion = QuestionSetQuery.all().have(questionID).list();
			for(QuestionSet set : resultSets) {
				maplist.add(ImmutableMap.of("set", set, "containsQuestion", haveQuestion.contains(set)));
			}
			return ImmutableMap.of("maplist", maplist);
		}
	}
	
	@GET
	@Path("/remove")
	@Produces("application/json")
	public boolean removeQuestionFromSet(@QueryParam("qid") int qid, @QueryParam("sid") int sid) {
		 try{
			 QuestionSet qs = QuestionSetQuery.get(sid);
			 qs.removeQuestion(QuestionQuery.get(qid));
			 qs.update();
		 } catch(Exception e) {
			 return false;
		 }
		 return true;
	}
	
	@GET
	@Path("/add")
	@Produces("application/json")
	public boolean addQuestionFromSet(@QueryParam("qid") int qid, @QueryParam("sid") int sid) {
		try{
			 QuestionSet qs = QuestionSetQuery.get(sid);
			 qs.addQuestion(QuestionQuery.get(qid));
			 qs.update();
		 } catch(Exception e) {
			 return false;
		 }
		 return true;
	}
}
