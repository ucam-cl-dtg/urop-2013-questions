package uk.ac.cam.sup.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
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

import uk.ac.cam.sup.exceptions.FormValidationException;
import uk.ac.cam.sup.form.QuestionRemove;
import uk.ac.cam.sup.form.QuestionSetAdd;
import uk.ac.cam.sup.form.QuestionSetEdit;
import uk.ac.cam.sup.form.QuestionSetExport;
import uk.ac.cam.sup.form.QuestionSetFork;
import uk.ac.cam.sup.form.QuestionSetTagForm;
import uk.ac.cam.sup.form.QuestionsAddRemove;
import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.models.User;
import uk.ac.cam.sup.queries.QuestionQuery;
import uk.ac.cam.sup.queries.QuestionSetQuery;

import com.google.common.collect.ImmutableMap;

@Path("/sets")
public class QuestionSetEditController extends GeneralController {
	
	private static Logger log = LoggerFactory.getLogger(QuestionSetEditController.class);
	
	@POST
	@Path("/addremove")
	@Produces("application/json")
	public Map<String,?> addRemoveQuestionToSet(@Form QuestionsAddRemove form) throws IOException{
		disallowGlobal();
		
		String uid = getCurrentUserID();
		
		Set<Map<String,?>> sets;
		int qid;
		
		try {
			form.validate();
			qid = form.getQid();
			sets = form.getSets();
		} catch (FormValidationException e) {
			return ImmutableMap.of("success", false, "error", e.getMessage());
		}
		
		int sid;
		boolean add;
		QuestionSet qs;
		Question q = QuestionQuery.get(qid);
		List<Integer> unsuccessful = new ArrayList<Integer>();
		for(Map<String,?> map: sets){
			try{
				sid = (Integer)map.get("sid");
				
			} catch(Exception e){
				return ImmutableMap.of("success", false, "error", "An error occurred! " + e.getMessage());
			}
			
			try{
				qs = QuestionSetQuery.get(sid);
				add = (Boolean)map.get("add");
				if(add){
					if(!qs.getOwner().getId().equals(uid)) throw new Exception("User is not owner of set!");
					qs.addQuestion(q);
					qs.update();
				}else if(!add){
					if(!qs.getOwner().getId().equals(uid)) throw new Exception("User is not owner of set!");
					qs.removeQuestion(q);
					qs.update();
				}else{
					throw new Exception();
				}
				
			} catch(Exception e){
				unsuccessful.add(sid);
			}
		}
		
		if(unsuccessful.size() < 1){
			return ImmutableMap.of("success", true);
		} else {
			String s = "";
			for(int i: unsuccessful){
				s = s + " " + i;
			}
			s = s + ".";
			return ImmutableMap.of("success", false, "error", "Error while adding/removing question in set(s):" + s);
		}
	}
	
	@POST
	@Path("/remove")
	@Produces("application/json")
	public Map<String,?> removeQuestionFromSet(@Form QuestionRemove form) throws IOException {
		disallowGlobal();
		
		String uid = getCurrentUserID();
		
		int qid,sid;
		try {
			qid = form.getQid();
			sid = form.getSid();
		} catch(Exception e) {
			log.warn("Exception when trying to get ints from sid & qid");
			return ImmutableMap.of("success", false, "error", "Bad arguments passed - possibly not ints");
		}
		
		
		log.debug("Trying to remove question " + qid + " from set " + sid + ". (Initiated by user " + uid + ")");
		try {
			QuestionSet qs = QuestionSetQuery.get(sid);
			if (qs == null) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			}
			if (!qs.getOwner().getId().equals(uid)) throw new Exception("User " + uid + " not authorized to remove "
					+ "question from set " + qs.getId() + ", which belongs to user " + qs.getOwner());
			qs.removeQuestion(QuestionQuery.get(qid));
			qs.update();
		} catch (WebApplicationException e) {
			throw e;
		} catch(Exception e) {
			log.warn("Error when trying to remove question!\nMessage: " + e.getMessage());
			return ImmutableMap.of("success", false, "error", e.getMessage());
		}
		return ImmutableMap.of("success", true);
	}
	
	@POST
	@Path("/fork")
	@Produces("application/json")
	public Map<String,?> forkSet(@Form QuestionSetFork form) throws IOException {
		disallowGlobal();
		
		try {
			form.validate().parse();
			QuestionSet fork = form.getSet().fork(getCurrentUser(), form.getName());
			fork.save();
			
			return ImmutableMap.of("success", true, "set", fork.toMap(!isCurrentUserSupervisor()));
		} catch (CloneNotSupportedException | FormValidationException e) {
			return ImmutableMap.of("success", false, "error", e.getMessage());
		}
	}
	
	@POST
	@Path("/delete")
	@Produces("application/json")
	public Map<String,?> deleteSet(@FormParam("setid") String setId) throws IOException {
		disallowGlobal();
		
		int id;
		try {
			id = Integer.parseInt(setId);
		} catch (Exception e) {
			return ImmutableMap.of("success", false, "error", "setid provided is not a number");
		}
		
		QuestionSet qs = QuestionSetQuery.get(id);
		if (qs == null) {
			return ImmutableMap.of("success", false, "error", "Set with this ID doesn't exist");
		}
		
		if ( ! qs.getOwner().getId().equals(getCurrentUserID())) {
			return ImmutableMap.of("success", false, "error", "You're not the owner of this set");
		}
		
		try {
			qs.delete();
		} catch (Exception e) {
			return ImmutableMap.of("success", false, "error", e.getMessage());
		}
		
		return ImmutableMap.of("success", true);
	}
	
	@POST
	@Path("/export")
	@Produces("application/json")
	public Map<String,?> exportQuestions(@Form QuestionSetExport form) throws Exception {
		disallowGlobal();
		
		QuestionSet qs;
		
		try {
			form.validate().parse();
			qs = form.getTarget();
			if (qs == null) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			}
			if (!qs.getOwner().getId().equals(getCurrentUserID())) {
				return ImmutableMap.of("success", false, "error", "You're not the owner of the target set");
			}
			log.debug("Trying to fork or add question(s) to set " + qs.getId() + "...");
			for (Question q: form.getQuestions()) {
				log.debug("Trying to add question " + q.getId() + " to set " + qs.getId());
				qs.addQuestion(q);
			}
			qs.update();
			//HibernateUtil.commit();
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			log.warn("Could not add question(s) to set!\n" + e.getStackTrace());
			return ImmutableMap.of("success", false, "error", e.getMessage());
		}
		log.debug("Apparently successfully added question(s) to set " + qs.getId());
		return ImmutableMap.of("success", true, "set", qs.toMap(!isCurrentUserSupervisor()));
	}
	
	@POST
	@Path("/save")
	@Produces("application/json")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Map<String,?> saveSet(@MultipartForm QuestionSetAdd form) throws IOException {
		disallowGlobal();
		
		log.debug("Trying to add new set for user '" + getCurrentUserID() + "..."); 
		QuestionSet qs;
		
		try {
			form.validate().parse();
			User author = getCurrentUser();
			qs = new QuestionSet(author);
			qs.setName(form.getName());
			qs.setPlan(form.getPlan());
			qs.save();
		} catch (Exception e) {
			log.debug("Failed to add set for user " + getCurrentUserID() + "!");
			return ImmutableMap.of("success", false, "error", e.getMessage());
		}
		
		log.debug("Successfully added set " + form.getName() + " for user " + getCurrentUserID());
		return ImmutableMap.of("success", true, "set", qs.toMap(!isCurrentUserSupervisor()));
	}
	
	@POST
	@Path("/update")
	@Produces("application/json")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Map<String,?> updateSet(@MultipartForm QuestionSetEdit form) throws WebApplicationException, IOException {
		disallowGlobal();
		
		QuestionSet qs;
		
		try {
			form.validate().parse();
			qs = QuestionSetQuery.get(form.getSetId());
			if (qs == null) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			}
			if (!qs.getOwner().getId().equals(getCurrentUserID())) {
				sendUnauthorized();
				return null;
			}
			
			qs.edit(form);
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			return ImmutableMap.of("success", false, "error", e.getMessage());
		}
		
		String dlLink;
		try{
			dlLink = "http://" + getServerName() + ":" + getServerPort()
					+ "/dashboard/deadlines/manage?url=" + URLEncoder.encode(getCurrentUrlRemoveApi(), "UTF-8")
					+ "&title=" + URLEncoder.encode(qs.getName(), "UTF-8");
		} catch (UnsupportedEncodingException e){
			log.error("Problems encoding url (UnsupportedEncodingException)! Message: " + e.getMessage());
			dlLink = "";
		}
		
		System.err.println("Str: " + dlLink);
		
		return ImmutableMap.of("success", true, "set", qs.toMap(false), "deadlineLink", dlLink);
	}
	
	@POST
	@Path("/togglestar")
	@Produces("application/json")
	public Map<String,?> toggleStar(@FormParam("id") int id) throws IOException {
		disallowGlobal();
		
		QuestionSet qs;
		
		try {
			qs = QuestionSetQuery.get(id);
			if (qs == null) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			}
			if (!qs.getOwner().getId().equals(getCurrentUserID())) {
				throw new Exception("You're not the owner of this set");
			}
			qs.toggleStarred();
			qs.update();
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			return ImmutableMap.of("success", false, "error", e.getMessage());
		}
		
		return ImmutableMap.of("success", true, "setid", id, "starred", qs.isStarred());
	}
	
	@POST
	@Path("/addtags")
	@Produces("application/json")
	public Map<String,?> addTags(@Form QuestionSetTagForm form) {
		try {
			form.validate();
			QuestionSet qs = QuestionSetQuery.get(form.getSetId());
			if (qs == null) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			}
			List<Tag> tags = form.getTagsList();
			Set<Tag> alltags = qs.getTags();
			Set<Tag> addedTags = new HashSet<Tag>();
			
			for (Tag t: tags) {
				if ( ! alltags.contains(t)) {
					qs.addTag(t);
					addedTags.add(t);
				}
			}
			qs.update();
			
			return ImmutableMap.of("success", true, "tags", addedTags, "amount", addedTags.size());
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			return ImmutableMap.of("success", false, "error", e.getMessage());
		}
	}
	
	@POST
	@Path("/removetags")
	@Produces("application/json")
	public Map<String,?> removeTags(@Form QuestionSetTagForm form) {
		try {
			form.validate();
			QuestionSet qs = QuestionSetQuery.get(form.getSetId());
			if (qs == null) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			}
			List<Tag> tags = form.getTagsList();
			
			for (Tag t: tags) {
				qs.removeTag(t);	
			}
			qs.update();
			
			return ImmutableMap.of("success", true, "set", qs.toMap(!isCurrentUserSupervisor()));
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			return ImmutableMap.of("success", false, "error", e.getMessage());
		}
	}
}
