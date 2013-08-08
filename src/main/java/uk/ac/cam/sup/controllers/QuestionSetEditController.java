package uk.ac.cam.sup.controllers;

import java.util.List;
import java.util.Map;

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

import uk.ac.cam.sup.form.QuestionRemove;
import uk.ac.cam.sup.form.QuestionSetAdd;
import uk.ac.cam.sup.form.QuestionSetEdit;
import uk.ac.cam.sup.form.QuestionSetFork;
import uk.ac.cam.sup.form.QuestionSetTagForm;
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
	@Path("/remove")
	@Produces("application/json")
	public Map<String,?> removeQuestionFromSet(@Form QuestionRemove form) {
		int qid,sid;
		try {
			qid = form.getQid();
			sid = form.getSid();
		} catch(Exception e) {
			log.warn("Exception when trying to get ints from sid & qid");
			return ImmutableMap.of("success", false, "error", "Bad arguments passed - possibly not ints");
		}
		
		
		log.debug("Trying to remove question " + qid + " from set " + sid + ". (Initiated by user " + getCurrentUserID() + ")");
		try {
			QuestionSet qs = QuestionSetQuery.get(sid);
			if (qs == null) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			}

			qs.removeQuestion(QuestionQuery.get(qid));
			qs.update();
		} catch (WebApplicationException e) {
			throw e;
		} catch(Exception e) {
			log.warn("Error when trying to remove question!\n" + e.getStackTrace());
			return ImmutableMap.of("success", false, "error", e.getMessage());
		}
		return ImmutableMap.of("success", true);
	}
	
	@POST
	@Path("/fork")
	@Produces("application/json")
	public Map<String,?> forkQuestion(@Form QuestionSetFork form) throws Exception {
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
		return ImmutableMap.of("success", true, "set", qs);
	}
	
	@POST
	@Path("/save")
	@Produces("application/json")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Map<String,?> saveSet(@MultipartForm QuestionSetAdd form) throws Exception {
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
		return ImmutableMap.of("success", true, "set", qs);
	}
	
	@POST
	@Path("/update")
	@Produces("application/json")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Map<String,?> updateSet(@MultipartForm QuestionSetEdit form) throws Exception {
		QuestionSet qs;
		
		try {
			form.validate().parse();
			qs = QuestionSetQuery.get(form.getSetId());
			if (qs == null) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			}
			if (!qs.getOwner().getId().equals(getCurrentUserID())) {
				throw new Exception("You're not the owner of this set");
			}
			
			qs.edit(form);
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			return ImmutableMap.of("success", false, "error", e.getMessage());
		}
		
		return ImmutableMap.of("success", true, "set", qs.toMap(false));
	}
	
	@POST
	@Path("/togglestar")
	@Produces("application/json")
	public Map<String,?> toggleStar(@FormParam("id") int id) {
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
			
			for (Tag t: tags) {
				t.saveOrUpdate();
				qs.addTag(t);	
			}
			qs.update();
			
			return ImmutableMap.of("success", true, "set", qs.toMap(!isCurrentUserSupervisor()));
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
