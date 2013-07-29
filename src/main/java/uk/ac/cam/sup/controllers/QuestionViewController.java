package uk.ac.cam.sup.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.queries.QuestionQuery;
import uk.ac.cam.sup.queries.TagQuery;
import uk.ac.cam.sup.util.SearchTerm;

import com.google.common.collect.ImmutableMap;

@Path("/q")
public class QuestionViewController extends GeneralController {
	private static Logger log = LoggerFactory.getLogger(QuestionViewController.class);

	/**
	 * Returns all questions which correspond to the search criteria.
	 * 
	 * @param tags
	 * @param owners
	 * @param star Whether the questions to search for should be starred. Null for both with and without star.
	 * @param supervisor Whether only to return questions from supervisors (true) or only students (false). Null for both.
	 * @param after Include all questions after a certain date.
	 * @param before Include all questions before a certain date.
	 * @param usageMin Include all questions used at least x times.
	 * @param usageMax Include all questions used at most x times.
	 * @param parentId
	 * @param durMax All questions with a maximum duration as given.
	 * @param durMin All questions with a minimum duration as given.
	 * @return
	 */
	@GET
	@Path("/search")
	@Produces("application/json")
	public Map<String, ?> produceFilteredQuestions(@QueryParam("tags") String tags,
			@QueryParam("owners") String owners,
			@QueryParam("star") Boolean star,
			@QueryParam("supervisor") Boolean supervisor,
			@QueryParam("after") Long after, @QueryParam("before") Long before,
			@QueryParam("usagemin") Integer usageMin,
			@QueryParam("usagemax") Integer usageMax,
			@QueryParam("parent") Integer parentId,
			@QueryParam("durmax") Integer durMax,
			@QueryParam("durmin") Integer durMin) {

		
		try {
			SearchTerm st = new SearchTerm(tags, owners, star, supervisor, after,
					before, usageMin, usageMax, parentId, durMax, durMin);
			List<?> filteredQuestions = getFilteredQuestions(st);

			return ImmutableMap.of("success", true, "questions", filteredQuestions, "st", st);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return ImmutableMap.of("success", false, "error", e.getMessage());
		}
		// return ImmutableMap.of("st", st);
	}

	/**
	 * Filters questions according to a search term.
	 * 
	 * @param st The search term according to which the questions should be filtered
	 * @return Returns a list of filtered questions according to the search term.
	 */
	private List<?> getFilteredQuestions(SearchTerm st) {
		log.debug("Getting new QuestionQuery");
		QuestionQuery qq = QuestionQuery.all();

		log.debug("Filtering for tags");
		if (st.getTags() != null && !st.getTags().equals("")) {
			List<String> lTags = Arrays.asList(st.getTags().split(","));
			qq.withTagNames(lTags);
		}

		log.debug("Filtering for owners");
		if (st.getOwners() != null && !st.getOwners().equals("")) {
			List<String> lUsers = Arrays.asList(st.getOwners().split(","));
			qq.withUserIDs(lUsers);
		}

		log.debug("Filtering for star, role...");
		if (st.getStar() != null && st.getStar()) {
			qq.withStar();
		}
		if (st.getStar() != null && !st.getStar()) {
			qq.withoutStar();
		}
		if (st.getSupervisor() != null && st.getSupervisor()) {
			qq.bySupervisor();
		}
		if (st.getSupervisor() != null && !st.getSupervisor()) {
			qq.byStudent();
		}

		log.debug("Filtering for date...");
		if (st.getAfter() != null) {
			qq.after(new Date(st.getAfter()));
		}
		if (st.getBefore() != null) {
			qq.before(new Date(st.getBefore()));
		}

		log.debug("Filtering for usages");
		if (st.getUsageMin() != null) {
			qq.minUsages(st.getUsageMin());
		}
		if (st.getUsageMax() != null) {
			qq.maxUsages(st.getUsageMax());
		}

		log.debug("Fileting for parentID");
		if (st.getParentId() != null) {
			qq.withParent(st.getParentId());
		}

		log.debug("Filtering for duration");
		if (st.getDurMax() != null) {
			qq.maxDuration(st.getDurMax());
		}
		if (st.getDurMin() != null) {
			qq.minDuration(st.getDurMin());
		}

		// TODO: check whether current user is a supervisor
		// and shadow the data appropriately
		return qq.maplist(false);
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
			@PathParam("id") int id) {
		return ImmutableMap.of("success", true, "question", QuestionQuery.get(id).toMap(false));
	}
	
	/*
	 What was/is this method for?! Delete if not needed. 
	@GET
	@Path("/add/{setid}")
	@Produces("application/json")
	public Map<String,?> showAddForm(@PathParam("setid") int setId) {
		//Map<String, Object> r = new HashMap<String, Object>();
		//r.put("success", true, "setId", setId);

		return ImmutableMap.of("sucess", true, "setId", setId);
	}*/
	
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
	@Path("/tagsnotin")
	@Produces("application/json")
	public List<Map<String, String>> getTagsNotInQuestion(String strInput) {
		List<Map<String, String>> results = new ArrayList<Map<String, String>>();
		
		int equPos = strInput.indexOf("=");
		if (equPos < 0) {
			return results;
		}
		int qid = Integer.parseInt(strInput.substring(0, equPos));
		String strTagPart = strInput.substring(equPos + 1).replace("+", " ");

		log.debug("Trying to get all tags containing " + strTagPart
				+ " which are NOT in question " + qid);

		List<Tag> tags = TagQuery.all().notContainedIn(QuestionQuery.get(qid))
				.contains(strTagPart).list();
		
		results.add(ImmutableMap.of("name", strTagPart));
		for (Tag tag : tags) {
			results.add(ImmutableMap.of("name", tag.getName()));
		}
		
		// The result needs to remain like this - don't change. (Unless you're gonna fix what you break...)
		return results;
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
		List<Question> historyList = new ArrayList<Question>();
		Question curChild = QuestionQuery.get(qid);
		Question curParent = null;
		
		for(; depth > 0; depth--) {
			
			curParent = curChild.getParent();
			
			if(curParent == null){
				exhausted = true;
				break;
			}
			
			historyList.add(curParent);
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
		List<Question> forks = QuestionQuery.all().withParent(qid).list();
		
		log.debug("There were " + forks.size() + " forks found. There are " + alreadyDisplayed + " already displayed.");
		if(forks.size() <= alreadyDisplayed) {
			log.debug("Number of forks <= forks already displayed.");
			return ImmutableMap.of(
					"success", false,
					"questions", new ArrayList<Question>(),
					"exhausted", true,
					"disp", alreadyDisplayed);
		}else if(forks.size() <= alreadyDisplayed + toDisplay){
			// If the amount of forks still not displayed is less than those requested.
			log.debug("There are still a few forks to display but the forks are now exhausted.");
			return ImmutableMap.of(
					"success", true,
					"questions", forks.subList(alreadyDisplayed, forks.size()),
					"exhausted", true,
					"disp", forks.size()
			);
		} else {
			log.debug("Not all forks returned.");
			return ImmutableMap.of(
					"success", true,
					"questions", forks.subList(alreadyDisplayed, alreadyDisplayed + toDisplay),
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
	 */
	@GET
	@Path("/{id}/edit/{setid}")
	@Produces("application/json")
	public Map<?, ?> produceEditForm(@PathParam("id") int id,
			@PathParam("setid") int setId) {
		Question q = QuestionQuery.get(id);

		Map<String, Object> r = new HashMap<String, Object>();
		r.put("id", q.getId());
		r.put("content", q.getContent().getData());
		r.put("notes", q.getNotes().getData());
		r.put("setId", setId);
		r.put("expectedDuration", q.getExpectedDuration());
		r.put("success", true);

		return r;
	}
	
	
}
