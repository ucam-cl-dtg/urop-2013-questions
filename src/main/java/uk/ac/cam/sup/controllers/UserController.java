package uk.ac.cam.sup.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import uk.ac.cam.sup.exceptions.QueryAlreadyOrderedException;
import uk.ac.cam.sup.models.User;
import uk.ac.cam.sup.queries.QuestionQuery;
import uk.ac.cam.sup.queries.QuestionSetQuery;

import com.google.common.collect.ImmutableMap;

@Path("/users")
public class UserController extends GeneralController {
	
	//private static Logger log = LoggerFactory.getLogger(UserController.class);
	
	@GET
	@Path("/{crsid}")
	@Produces("application/json")
	public Map<String,?> getUserID(@PathParam("crsid") String crsid){
		return ImmutableMap.of("success", true, "crsid", crsid); 
	}
	
	@GET
	@Path("/{crsid}/sets")
	@Produces("application/json")
	public Map<String,?> produceUserSets(@PathParam("crsid") String crsid,
			@QueryParam("page") Integer page, @QueryParam("amount") Integer amount) {
		
		if(page == null || page < 1){
			page = 1;
		}
		if(amount == null || amount < 1){
			amount = 1;
		}
		
		
		QuestionSetQuery qsq = QuestionSetQuery.all().withUser(new User(crsid));
		int totalSets;
		try {
			totalSets = qsq.size();
		} catch (QueryAlreadyOrderedException e) {
			return ImmutableMap.of("success", false, "error", e.getMessage());
		}
		
		List<Map<String,?>> sets = qsq.maxResults(amount).offset(amount * (page-1)).maplist();
		
		return ImmutableMap.of(
				"success", true,
				"crsid", crsid,
				"sets", sets,
				"totalSets", totalSets);
	}
	
	@GET
	@Path("/{crsid}/questions")
	@Produces("application/json")
	public Map<String,?> produceUserQuestions(
			@PathParam("crsid") String crsid,
			@QueryParam("page") Integer page, @QueryParam("amount") Integer amount
			){
		if(page == null || page < 1){
			page = 1;
		}
		if(amount == null || amount < 1){
			amount = 1;
		}
		
		List<String> users = new ArrayList<String>();
		users.add(crsid);
		
		QuestionQuery qq = QuestionQuery.all().withUserIDs(users);
		int totalQuestions;
		try {
			totalQuestions = qq.size();
		} catch (QueryAlreadyOrderedException e) {
			return ImmutableMap.of("success", false, "error", e.getMessage());
		}
		
		List<Map<String,?>> questions = qq.maxResults(amount).offset(amount * (page-1)).maplist();
		
		return ImmutableMap.of(
				"success", true,
				"crsid", crsid,
				"questions", questions,
				"totalQuestions", totalQuestions);
	}
	
	@GET
	@Path("/me")
	@Produces("application/json")
	public Map<String,?> produceMyContent() {
		return getUserID(getCurrentUserID());
	}
	
}
