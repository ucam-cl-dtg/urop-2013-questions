package uk.ac.cam.sup.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.google.common.collect.ImmutableMap;

import uk.ac.cam.sup.models.User;
import uk.ac.cam.sup.queries.QuestionQuery;
import uk.ac.cam.sup.queries.QuestionSetQuery;

@Path("/users")
public class UserController extends GeneralController {
	
	@GET
	@Path("/{crsid}")
	@Produces("application/json")
	public Map<String,?> produceSetsAndQuestions(@PathParam("crsid") String crsid) {
		User u = new User(crsid);
		List<User> userlist = new ArrayList<User>();
		userlist.add(u);
		
		List<Map<String,?>> questions = QuestionQuery.all().withOwners(userlist).maplist();
		List<Map<String,?>> sets = QuestionSetQuery.all().withUsers(userlist).maplist();
		
		return ImmutableMap.of(
				"crsid", crsid,
				"questions", questions,
				"sets", sets
		);
	}
	
	@GET
	@Path("/me")
	@Produces("application/json")
	public Map<String,?> produceMyContent() {
		return produceSetsAndQuestions(getCurrentUserID());
	}
	
}
