package uk.ac.cam.sup.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.cl.dtg.ldap.LDAPObjectNotFoundException;
import uk.ac.cam.cl.dtg.ldap.LDAPQueryManager;
import uk.ac.cam.cl.dtg.ldap.LDAPUser;
import uk.ac.cam.cl.dtg.teaching.api.NotificationException;
import uk.ac.cam.cl.dtg.teaching.api.NotificationApi.NotificationApiWrapper;
import uk.ac.cam.sup.exceptions.FormValidationException;
import uk.ac.cam.sup.exceptions.InvalidInputException;
import uk.ac.cam.sup.form.QuestionAdd;
import uk.ac.cam.sup.form.QuestionEdit;
import uk.ac.cam.sup.form.QuestionTagAdd;
import uk.ac.cam.sup.form.TagDel;
import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.models.User;
import uk.ac.cam.sup.ppdloader.PPDLoader;
import uk.ac.cam.sup.queries.QuestionQuery;
import uk.ac.cam.sup.queries.QuestionSetQuery;
import uk.ac.cam.sup.queries.TagQuery;

import com.google.common.collect.ImmutableMap;

@Path("/q")
public class QuestionEditController extends GeneralController{
	
	Logger log = LoggerFactory.getLogger(QuestionEditController.class);
	
	/**
	 * Update an existing question
	 * 
	 * @param qe
	 * @return
	 */
	@POST
	@Path("/update")
	@Produces("application/json")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Map<String,?> updateQuestion(@MultipartForm QuestionEdit qe) {
		User editor = getCurrentUser();
		Question q;
		QuestionSet qs;
		
		try {
			qe.validate().parse();

			log.debug("Trying to update question " + qe.getId() + " in set " + qe.getSetId() + ".");
			
			q = QuestionQuery.get(qe.getId());
			if (q == null) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			}
			qs = QuestionSetQuery.get(qe.getSetId());
			if (qs != null) {
				if (!qs.getOwner().getId().equals(getCurrentUserID())) {
					throw new FormValidationException("You're not the owner of the target set: "+qs.getName());
				}
			}
			
			q = q.edit(editor, qe);
			log.info("Calling method to send notification...");
			sendQuestionEditedNotification(qe.getId(), q.getId(), editor.getId());
						
			if(qe.getSetId() == -1) {
				return ImmutableMap.of("success", true, "question", q);
			}else{
				return ImmutableMap.of("success", true, "question", q, "set", qs);
			}
		} catch (WebApplicationException e) {
			throw e;
		} catch (FormValidationException e) {
			log.debug("There was a FormValidationException: " + e.getMessage());
			return ImmutableMap.of("success", false, "error", e.getMessage());
		} catch (InvalidInputException e){
			log.debug("There was an InvalidInputException: " + e.getMessage());
			return ImmutableMap.of("success", false, "error", e.getMessage());
		} catch (NullPointerException e){
			log.debug("There was a NullPointerException");
			e.printStackTrace();
			return ImmutableMap.of("success", false, "error", "NullPointerException");
		} catch (Exception e){
			log.debug("There was an exception: " + e.getMessage());
			e.printStackTrace();
			return ImmutableMap.of("success", false, "error", (e.getMessage()==null ? "Unspecified exception of type " + e.getClass() : e.getMessage()));
		}
		
	}

	private void sendQuestionEditedNotification(int oldQuestionID, int newQuestionID, String userID){
		String userName;
		try {
			LDAPUser user = LDAPQueryManager.getUser(userID);
			userName = user.getDisplayName();
		} catch(LDAPObjectNotFoundException e){
			userName = userID;
		}

		String message;
		
		if(oldQuestionID != newQuestionID){
			message = userName + " edited question " + oldQuestionID + ", which you are using in one or more of your sets."
					+ " Click to view the new version of the question.";
		} else {
			message = userName + " made a minor edit to question " + oldQuestionID
					+ ", which you are using in one or more of your sets. The changes have been applied automatically to your sets.";
		}
			
		String link = "q/" + newQuestionID;
		Set<String> userSet = new HashSet<String>();
		
		// NOTE: I don't think this is the best way to do it - therefore the editor
		// won't be notified by default anymore. If people have major problems with
		// this, simply change it again.
		//
		// If this question is not in a question set then the query below won't
		// have any results. Therefore we start off with the assumption that the
		// editor should be in the list of notified people. Thus if the question
		// is not in a question set this will be the owner of the question. If
		// the questions is in a question set then this will be a duplicate of a
		// result returned by the following query.
		//userSet.add(userID);
		
		Question oldQ = QuestionQuery.get(oldQuestionID);
		if(oldQ != null){
			String ownerID = oldQ.getOwner().getId(); 
			userSet.add(ownerID);
		}
		
		
		List<QuestionSet> sets = QuestionSetQuery.all().have(oldQuestionID).list();
		for(QuestionSet set: sets){
			userSet.add(set.getOwner().getId());
		}
		
		// Remove the editor from the list of users to notify, as they should
		// know that they have edited a question.
		if(userSet.contains(userID)) {
			userSet.remove(userID);
		}
		
		if(userSet.size() < 1){
			// Abort if there is no one to send a notification to.
			return;
		}
		
		StringBuffer usersBuf = new StringBuffer();
		for(String s: userSet){
			usersBuf = usersBuf.append(","+s);
		}
		String users = usersBuf.substring(1);
		
		NotificationApiWrapper n = new NotificationApiWrapper(getDashboardURL(), getApiKey());

		//TODO: remove this logging message
		log.debug("Now trying to send notification via supervision-api. \nDashboardURL: " 
				+ getDashboardURL() + ".  \nAPIKey: " + getApiKey() + ".  \nMessage: " + message
				+ ".  \nLink: " + link + ".  \nUsers: " + users + ".  \nsection: questions.");
		try{
			n.createNotification(message, "questions", link, users);
			log.debug("Apparently successfully created notification!");
		} catch(NotificationException e){
			log.error("Could not create notification: \"" + message + "\" for users " + users + ".\nMessage: " + e.getMessage());
		}
	}
	
	@POST
	@Path("/save")
	@Produces("application/json")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Map<String,?> addQuestion(@MultipartForm QuestionAdd qa) {
		User author = getCurrentUser();
		Question q;
		QuestionSet qs;
		
		try {
			qa.validate().parse();
			
			q = new Question(author);
			q.setContent(qa.getContent());
			q.setNotes(qa.getNotes());
			q.setExpectedDuration(qa.getExpectedDuration());
			q.save();
			
			if(qa.getSetId() != -1){
				qs = QuestionSetQuery.get(qa.getSetId());
				if (qs == null) {
					return ImmutableMap.of("success", false, "error", "Question set "+qa.getSetId()+" does not exist");
				}
				if (!qs.getOwner().getId().equals(getCurrentUserID())) {
					return ImmutableMap.of("success", false, "error", "Question set "+qa.getSetId()+" is owned by a different user");
				}
				
				qs.addQuestion(q);
				qs.update();
				
				return ImmutableMap.of("success", true, "question", q, "set", qs);
			}
			
			return ImmutableMap.of("success", true, "question", q);
			
		} catch (FormValidationException e) {
			return ImmutableMap.of("success", false, "error", "Message: " + e.getMessage());
		} catch (IOException e) {
			return ImmutableMap.of("success", false, "error", "Message: Failed to store uploaded file. " + e.getMessage());
		}
	}
	
	@POST
	@Path("/addtags")
	@Produces("application/json")
	public Map<String,?> addTags(@Form QuestionTagAdd addForm) {
		// returns the tags added
		
		try {
			int qid = addForm.validate().getQid();
			String[] newTagsArray = addForm.getNewTags();
			
			if (newTagsArray != null && newTagsArray.length > 0) {
				
				Question question = QuestionQuery.get(qid);
				if (question == null) {
					throw new WebApplicationException(Response.Status.NOT_FOUND);
				}
				List<Tag> result = new ArrayList<Tag>();
				Set<Tag> existingTags = question.getTags();
				Tag tmp;
				
				for (int i = 0; i < newTagsArray.length; i++) {
					tmp = TagQuery.get(newTagsArray[i].trim());
					
					if(!existingTags.contains(tmp) && tmp.getName() != null && tmp.getName() != "") {
						result.add(tmp);
					}
					
					log.debug("Trying to add tag " + tmp.getName() + " to question " + qid + "...");
					question.addTag(tmp);
				}
				
				log.debug("Trying to update question in data base...");
				question.update();
				
				return ImmutableMap.of("success", true, "tags", result, "amount", result.size());
			}
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			return ImmutableMap.of("success", false, "error", e.getMessage());
		}
		return ImmutableMap.of("success", false, "error", "Tag array was null");
		
	}
	
	@POST
	@Path("/deltag")
	@Produces("application/json")
	public Map<String,?> delTag(@Form TagDel delForm){
		String tag = delForm.getTag();
		int qid = delForm.getQid();
		
		try{
			log.debug("Deleting tag '" + tag + "' from question " + qid);
			Question question = QuestionQuery.get(qid);
			if (question == null) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			}
			question.removeTagByString(tag);
			question.update();
			return ImmutableMap.of("success", true, "question", question);
		} catch (WebApplicationException e) {
			throw e;
		} catch(Exception e){
			return ImmutableMap.of("success", false, "error", e.getMessage());
		}
		
	}
	
	@POST
	@Path("/togglestar")
	@Produces("application/json")
	public Map<String,?> toggleStar(@FormParam("id") int id) {
		Question q;
		try{
			q = QuestionQuery.get(id);
			if (q == null) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			}
			if (!q.getOwner().getId().equals(getCurrentUserID())) {
				throw new Exception("You're not the owner of this question");
			}
			q.toggleStarred();
			q.update();
		} catch (WebApplicationException e) {
			throw e;
		} catch(Exception e) {
			return ImmutableMap.of("success", false, "error", e.getMessage());
		}
		return ImmutableMap.of("success", true, "id", id, "starred", q.isStarred());
	}
	
}
