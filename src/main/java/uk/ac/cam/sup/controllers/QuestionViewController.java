package uk.ac.cam.sup.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.sup.exceptions.QueryAlreadyOrderedException;
import uk.ac.cam.sup.form.QuestionSearchForm;
import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.queries.QuestionQuery;
import uk.ac.cam.sup.queries.TagQuery;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

@Path("/q")
public class QuestionViewController extends GeneralController {
	private static Logger log = LoggerFactory.getLogger(QuestionViewController.class);

	/**
	 * Returns a JSON with search results and a copy of the criteria
	 * 
	 * @param form QuestionSearchForm object with search criteria
	 * @return
	 */
	@GET
	@Path("/search")
	@Produces("application/json")
	public Map<String,?> showSearchPage(@Form QuestionSearchForm form){
		try {
			form.validate().parse();
			
			Builder<String,Object> builder = ImmutableMap.builder();
			builder	.put("questions", form.getSearchResults())
					.put("form", form.toMap())
					.put("success", true);
			
			return builder.build();
		} catch (NullPointerException e) {
			return ImmutableMap.of("success", false, "error", "Null pointer exception");
		} catch (Exception e) {
			return ImmutableMap.of("success", false, "error", e.getMessage());
		}
	}
	
	/**
	 * Returns a single question (as map, potentially shadowed)
	 * 
	 * @param id Question ID to return
	 * @return
	 */
	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Map<String,?> produceSingleQuestion(
			@PathParam("id") int id
	) {
		Question q = QuestionQuery.get(id);
		if (q == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		return ImmutableMap.of(
				"success", true,
				"question", q.toMap(!isCurrentUserSupervisor())
		);
	}
	
	/**
	 * Returns a single question (as map, potentially shadowed)
	 * 
	 * @param id Question ID to return
	 * @param target Tab to show
	 * @return
	 */
	@GET
	@Path("/{id}/{target}")
	@Produces("application/json")
	public Map<String,?> produceSingleQuestion(
			@PathParam("id") int id,
			@PathParam("target") String target
	) {
		Question q = QuestionQuery.get(id);
		if (q == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		return ImmutableMap.of(
				"success", true,
				"question", q.toMap(!isCurrentUserSupervisor()),
				"target", target
		);
	}
	
	/**
	 * To find the tags not in the current question. Intended for auto-completion feature only. 
	 * 
	 * Will return a list of all tags which contain the search term
	 * given in strInput and which are not in the question.
	 * 
	 * @param strInput Format: [questionID]=[tagString]
	 * @return
	 */
	@POST
	@Path("/tagsnotin/{qid}")
	@Produces("application/json")
	public List<Map<String, String>> getTagsNotInQuestion(@FormParam("q") String strInput, @PathParam("qid") Integer qid) {
		List<Map<String, String>> results = new ArrayList<Map<String, String>>();
		log.debug(strInput);
		try {
			if (strInput == null || strInput == "" || qid == null) {
				return results;
			}
			String strTagPart = strInput.replace("+", " ");

			log.debug("Trying to get all tags containing " + strTagPart
					+ " which are NOT in question " + qid);

			List<Tag> tags = TagQuery.all().notContainedIn(QuestionQuery.get(qid).getTags())
					.contains(strTagPart).maxResults(10).list();
			
			boolean contains = false;
			for (Tag tag : tags) {
				results.add(ImmutableMap.of("name", tag.getName()));
				if(tag.getName().equalsIgnoreCase(strTagPart)) contains = true;
			}
			if(!contains){
				results.add(ImmutableMap.of("name", strTagPart));
			}
			
			// The result needs to remain like this - don't change. (Unless you're gonna fix what you break...)
			return results;
		} catch (Exception e) {
			log.warn("There was some invalid input to the tagsNotInQuestion method. Message: " + e.getMessage());
			return results;
		}
	}

	/**
	 * Finds the parents of a question.
	 * 
	 * Finds parents in the order from the most recent (immediate)
	 * parent to the parent further back in history. 
	 * 
	 * @param qid The question ID whose parents to look for.
	 * @param depth The amount of parents (if available) to return.
	 * @return
	 */
	@GET
	@Path("/parents")
	@Produces("application/json")
	public Map<String,?> produceParents(@QueryParam("qid") int qid, @QueryParam("depth") int depth) {
		boolean exhausted = false;
		List<Map<String,?>> historyList = new ArrayList<Map<String,?>>();
		Question curChild = QuestionQuery.get(qid);
		if (curChild == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		Question curParent = null;
		
		for(; depth > 0; depth--) {
			
			curParent = curChild.getParent();
			
			if(curParent == null){
				exhausted = true;
				break;
			}
			
			historyList.add(curParent.toMap());
			curChild = curParent;
		}
		
		if(curParent != null && curParent.getParent() == null) {exhausted = true;}
		 
		int lastID = curChild.getId(); 
		
		return ImmutableMap.of("success", true, "questions", historyList, "exhausted", exhausted, "last", lastID);
	}
	 
	/**
	 * Finds forks of a question.
	 * 
	 * @param qid The question ID whose children are to be displayed.
	 * @param alreadyDisplayed The amount of questions already displayed (will skip those).
	 * @param toDisplay The amount of questions to display (after skipping the alreadyDisplayed ones)
	 * @return
	 */
	@GET
	@Path("/forks")
	@Produces("application/json")
	public Map<String,?> produceForks(
			@QueryParam("qid") int qid, 
			@QueryParam("disp") int alreadyDisplayed, 
			@QueryParam("amount") int toDisplay){
		// disp is the number of forks already displayed. Therefore, if 0 forks are displayed, for ex,
		// the controller will return the 
		List<Map<String, ?>> forks;
		
		QuestionQuery qq = QuestionQuery.all().withParent(qid);
		int size;
		try {
			size = qq.size();
		} catch (QueryAlreadyOrderedException e) {
			return ImmutableMap.of("success", false, "error", "Unexpected error: " + e.getMessage());
		}
		
		forks = qq.maxResults(toDisplay).offset(alreadyDisplayed).maplist();
		
		log.debug("There were " + size + " forks found. There are " + alreadyDisplayed + " already displayed.");
		if(size <= alreadyDisplayed) {
			log.debug("Number of forks <= forks already displayed.");
			return ImmutableMap.of(
					"success", false,
					"questions", new ArrayList<Question>(),
					"exhausted", true,
					"disp", alreadyDisplayed);
		} else if(size <= alreadyDisplayed + toDisplay) {
			// If the amount of forks still not displayed is less than those requested.
			log.debug("There are still a few forks to display but the forks are now exhausted.");
			return ImmutableMap.of(
					"success", true,
					"questions", forks,
					"exhausted", true,
					"disp", size
			);
		} else {
			log.debug("Not all forks returned.");
			return ImmutableMap.of(
					"success", true,
					"questions", forks,
					"exhausted", false,
					"disp", alreadyDisplayed + toDisplay
			);
		}
		
	}
	
	/**
	 * Returns the data needed to show an edit form for questions
	 * in context to a question set.
	 * 
	 * @param id
	 * @param setId
	 * @return
	 * @throws IOException 
	 */
	@GET
	@Path("/{id}/edit/{setid}")
	@Produces("application/json")
	public Map<String, ?> produceEditForm(
			@PathParam("id") int id,
			@PathParam("setid") int setId
	) throws IOException {
		disallowGlobal();
		
		Question q = QuestionQuery.get(id);
		if (q == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		Map<String, Object> r = new HashMap<String, Object>();
		r.put("id", q.getId());
		r.put("content", q.getContent().getData());
		if (!isCurrentUserSupervisor() && q.getOwner().getSupervisor()) {
			r.put("notes", "");
		} else {
			r.put("notes", q.getNotes().getData());
		}
		r.put("setId", setId);
		r.put("expectedDuration", q.getExpectedDuration());
		r.put("success", true);

		return r;
	}
	
	/*
	 * Dummy controllers. Don't return anything. Only there to satisfy the router.
	 */
	@GET
	@Path("/add")
	public Map<String,String> dummy1(){
		return ImmutableMap.of();
	}
}
