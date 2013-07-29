package uk.ac.cam.sup.controllers;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jboss.resteasy.annotations.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.sup.form.QuestionSetAdd;
import uk.ac.cam.sup.form.QuestionSetEdit;
import uk.ac.cam.sup.form.QuestionSetFork;
import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.models.User;
import uk.ac.cam.sup.queries.QuestionQuery;
import uk.ac.cam.sup.queries.QuestionSetQuery;

import com.google.common.collect.ImmutableMap;

@Path("/sets")
public class QuestionSetEditController extends GeneralController {
	
	private static Logger log = LoggerFactory.getLogger(QuestionSetEditController.class);
	
	@GET
	@Path("/remove")
	@Produces("application/json")
	@Deprecated
	public boolean removeQuestionFromSet(@QueryParam("qid") int qid, @QueryParam("sid") int sid) {
		log.debug("Trying to remove question " + qid + " from set " + sid + ". (Initiated by user " + getCurrentUserID() + ")");
		try{
			 QuestionSet qs = QuestionSetQuery.get(sid);
			 qs.removeQuestion(QuestionQuery.get(qid));
			 qs.update();
		 } catch(Exception e) {
			 log.warn("Error when trying to remove question!\n" + e.getStackTrace());
			 return false;
		 }
		 return true;
	}
	
	@GET
	@Path("/add")
	@Produces("application/json")
	@Deprecated
	public boolean addQuestionToSet(@QueryParam("qid") int qid, @QueryParam("sid") int sid) {
		log.debug("Trying to add question " + qid + " to set " + sid + ". (Initiated by user " + getCurrentUserID() + ")");
		try{
			 QuestionSet qs = QuestionSetQuery.get(sid);
			 qs.addQuestion(QuestionQuery.get(qid));
			 qs.update();
		 } catch(Exception e) {
			 log.warn("Error when trying to add question!\n" + e.getStackTrace());
			 return false;
		 }
		return true;
	}
	
	@POST
	@Path("/fork")
	@Produces("application/json")
	public Map<String,?> forkSet(@Form QuestionSetFork form) throws Exception {
		QuestionSet qs;
		
		try {
			form.validate().parse();
			qs = form.getTarget();
			for (Question q: form.getQuestions()) {
				qs.addQuestion(q);
			}
			qs.update();
		} catch (Exception e) {
			return ImmutableMap.of("success", false, "error", e.getMessage());
		}
		
		return ImmutableMap.of("success", true, "set", qs);
	}
	
	@POST
	@Path("/save")
	@Produces("application/json")
	public Map<String,?> saveSet(@Form QuestionSetAdd form) throws Exception {
		QuestionSet qs;
		
		try {
			form.validate().parse();
			User author = getCurrentUser();
			qs = new QuestionSet(author);
			qs.setName(form.getName());
			qs.setPlan(form.getPlan());
			qs.save();
		} catch (Exception e) {
			return ImmutableMap.of("success", false, "error", e.getMessage());
		}
		
		return ImmutableMap.of("success", true, "set", qs);
	}
	
	@POST
	@Path("/update")
	@Produces("application/json")
	public Map<String,?> updateSet(@Form QuestionSetEdit form) throws Exception {
		QuestionSet qs;
		
		try {
			form.validate().parse();
			qs = QuestionSetQuery.get(form.getSetId());
			qs.edit(form);
		} catch (Exception e) {
			return ImmutableMap.of("success", false, "error", e.getMessage());
		}
		
		return ImmutableMap.of("success", true, "set", qs.toMap(false));
	}
	
	@GET
	@Path("/{id}/togglestar")
	@Produces("application/json")
	public Map<String,?> toggleStar(@PathParam("id") int id) {
		QuestionSet qs = QuestionSetQuery.get(id);
		qs.toggleStarred();
		qs.update();
		
		return ImmutableMap.of("setid", id, "starred", qs.isStarred());
	}
	
}
