package uk.ac.cam.sup.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jboss.resteasy.annotations.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.sup.form.QuestionAdd;
import uk.ac.cam.sup.form.QuestionEdit;
import uk.ac.cam.sup.form.TagAdd;
import uk.ac.cam.sup.form.TagDel;
import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.models.User;
import uk.ac.cam.sup.ppdloader.PPDLoader;
import uk.ac.cam.sup.queries.QuestionQuery;
import uk.ac.cam.sup.queries.QuestionSetQuery;

import com.google.common.collect.ImmutableMap;
import com.googlecode.htmleasy.RedirectException;


@Path("/q")
public class QuestionEditController extends GeneralController{
	
	Logger log = LoggerFactory.getLogger(GeneralController.class);
	
	@POST
	@Path("/update")
	public void editQuestion(@Form QuestionEdit qe) {
		User editor = getCurrentUser();

		try {
			qe.validate();
		} catch (Exception e) {
			throw new RedirectException("/q/error/?msg=" + e.getMessage());
		}

		Question q = QuestionQuery.get(qe.getId());
		q = q.edit(editor, qe);

		throw new RedirectException("/app/#sets/" + qe.getSetId());
	}

	@POST
	@Path("/save")
	public void addQuestion(@Form QuestionAdd qa) {
		User author = getCurrentUser();

		try {
			qa.validate();
		} catch (Exception e) {
			throw new RedirectException("/q/error/?msg=" + e.getMessage());
		}

		Question q = new Question(author);
		q.setContent(qa.getContent());
		q.setNotes(qa.getNotes());
		q.setExpectedDuration(qa.getExpectedDuration());
		q.save();
		QuestionSet qs = QuestionSetQuery.get(qa.getSetId());
		qs.addQuestion(q);
		qs.update();

		throw new RedirectException("/app/#sets/" + qa.getSetId());
	}

	@GET
	@Path("/pastpapers")
	@Produces("application/json")
	public Set<Question> producePastPapers() throws Exception {
		return PPDLoader.loadAllQuestions();
	}
	
	@POST
	@Path("/addtags")
	@Produces("application/json")
	public Map<String,List<Tag>> addTags(@Form TagAdd addForm) {
		// returns the tags added
		
		int qid = addForm.getQid();
		String[] newTagsArray = addForm.getNewTags();
		
		if (newTagsArray != null) {
			
			Question question = QuestionQuery.get(qid);
			List<Tag> result = new ArrayList<Tag>();
			Set<Tag> existingTags = question.getTags();
			Tag tmp;
			
			for (int i = 0; i < newTagsArray.length; i++) {
				tmp = new Tag(newTagsArray[i]);
				
				if(!existingTags.contains(tmp) && tmp.getName() != null && tmp.getName() != "") {
					result.add(tmp);
				}
				
				log.debug("Trying to add tag " + tmp.getName() + " to question " + qid + "...");
				question.addTag(tmp);
			}
			
			log.debug("Trying to update question in data base...");
			question.update();
			
			return ImmutableMap.of("tags", result);
		}

		return null;
	}
	
	@POST
	@Path("/deltag")
	@Produces("application/json")
	public boolean delTag(@Form TagDel delForm){
		String tag = delForm.getTag();
		int qid = delForm.getQid();
		
		try{
			log.debug("Deleting tag '" + tag + "' from question " + qid);
			Question question = QuestionQuery.get(qid);
			question.removeTagByString(tag);
			question.update();
			return true;
		} catch(Exception e){
			return false;
		}
		
	}
	
	@POST
	@Path("/{id}/togglestar")
	@Produces("application/json")
	public Map<String,?> toggleStar(@PathParam("id") int id) {
		Question q = QuestionQuery.get(id);
		q.toggleStarred();
		q.update();
		
		return ImmutableMap.of("id", id, "starred", q.isStarred());
	}
	
}
