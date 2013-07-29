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
import com.googlecode.htmleasy.RedirectException;

@Path("/sets")
public class QuestionSetEditController extends GeneralController {
	
	private static Logger log = LoggerFactory.getLogger(QuestionSetEditController.class);
	
	@GET
	@Path("/remove")
	@Produces("application/json")
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
	public boolean addQuestionFromSet(@QueryParam("qid") int qid, @QueryParam("sid") int sid) {
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
	public void forkSet(@Form QuestionSetFork form) throws Exception {
		form.validate().parse();
		for (Question q: form.getQuestions()) {
			form.getTarget().addQuestion(q);
		}
		form.getTarget().update();
		throw new RedirectException("/app/#sets/"+form.getTarget().getId());
	}
	
	@POST
	@Path("/save")
	public void saveSet(@Form QuestionSetAdd form) throws Exception {
		form.validate().parse();
		User author = getCurrentUser();
		QuestionSet qs = new QuestionSet(author);
		qs.setName(form.getName());
		qs.setPlan(form.getPlan());
		qs.save();
		
		throw new RedirectException("/app/#sets/"+qs.getId());
	}
	
	@POST
	@Path("/update")
	public void updateSet(@Form QuestionSetEdit form) throws Exception {
		form.validate().parse();
		
		QuestionSetQuery.get(form.getSetId()).edit(form);
		
		throw new RedirectException("/app/#sets/"+form.getSetId());
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
