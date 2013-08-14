package uk.ac.cam.sup.form;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.QueryParam;

import uk.ac.cam.sup.exceptions.FormValidationException;
import uk.ac.cam.sup.exceptions.ModelNotFoundException;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.models.User;
import uk.ac.cam.sup.queries.Query;
import uk.ac.cam.sup.queries.TagQuery;
import uk.ac.cam.sup.queries.UserQuery;
import uk.ac.cam.sup.util.Mappable;
import uk.ac.cam.sup.util.TripleChoice;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public abstract class SearchForm<T extends Mappable> {
	@QueryParam("tags")
	protected String tags;
	protected List<Tag> tagList = new ArrayList<Tag>();
	
	@QueryParam("authors")
	protected String authors;
	protected List<User> authorList = new ArrayList<User>();
	
	@QueryParam("minduration")
	protected String minDuration;
	protected Integer minDurationInt;
	
	@QueryParam("maxduration")
	protected String maxDuration;
	protected Integer maxDurationInt;
	
	@QueryParam("after")
	protected String after;
	protected Date afterDate = new Date();
	
	@QueryParam("before")
	protected String before;
	protected Date beforeDate = new Date();
	
	@QueryParam("supervisor")
	protected String supervisor;
	protected TripleChoice supervisorChoice = TripleChoice.DONT_CARE;
	
	@QueryParam("starred")
	protected String starred;
	protected TripleChoice starredChoice = TripleChoice.DONT_CARE;
	
	protected boolean validated = false;
	
	protected final void checkValidity() throws FormValidationException {
		if ( ! this.validated) {
			throw new FormValidationException("Form was nod validated");
		}
	}
	
	public SearchForm<T> validate() throws FormValidationException {
		if (tags == null) {
			tags = "";
		}

		if (authors == null) {
			authors = "";
		}
		
		if (minDuration == null) {
			minDuration = "";
		}
		
		if (maxDuration == null) {
			maxDuration = "";
		}
		
		if (after == null) {
			after = "";
		}
		
		if (before == null) {
			before = "";
		}
		
		if (supervisor == null || supervisor.equals("")) {
			supervisor = "DONT_CARE";
		}
		
		try {
			TripleChoice.valueOf(supervisor);
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new FormValidationException
				("Illegal value for supervisor filter: "+supervisor);
		}
		
		if (starred == null || starred.equals("")) {
			starred = "DONT_CARE";
		}
		
		try {
			TripleChoice.valueOf(starred);
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new FormValidationException
				("Illegal value for starred filter: "+starred);
		}
		
		this.validated = true;
		return this;
	}
	
	public SearchForm<T> parse() throws FormValidationException {
		checkValidity();
		
		String[] split = tags.split(",");
		for (String s: split) {
			if ( ! s.equals("")) {
				tagList.add(TagQuery.get(s.trim()));
			}
		}
		
		split = authors.split(",");
		for (String s: split) {
			if ( ! s.equals("")) {
				try {
					authorList.add(UserQuery.get(s.trim()));
				} catch (ModelNotFoundException e) {
					continue;
				}
			}
		}
		
		try {
			minDurationInt = Integer.parseInt(minDuration);
		} catch (Exception e) {
			minDurationInt = -2000000001;
		}
		
		try {
			maxDurationInt = Integer.parseInt(maxDuration);
		} catch (Exception e) {
			maxDurationInt = 2000000001;
		}
		
		Calendar c = Calendar.getInstance();
		try {
			split = before.split("/");
			int day = Integer.parseInt(split[0]);
			int month = Integer.parseInt(split[1]);
			int year = Integer.parseInt(split[2]);
			c.set(year, month, day);
			beforeDate = c.getTime();
		} catch(Exception e) {
			beforeDate = new Date();
		}
		
		try {
			split = after.split("/");
			int day = Integer.parseInt(split[0]);
			int month = Integer.parseInt(split[1]);
			int year = Integer.parseInt(split[2]);
			c.set(year, month, day);
			afterDate = c.getTime();
		} catch(Exception e) {
			c.set(1970, 1, 1, 0, 0, 0);
			afterDate = c.getTime();
		}
		
		supervisorChoice = TripleChoice.valueOf(supervisor);
		starredChoice = TripleChoice.valueOf(starred);
		
		return this;
	}
	
	public final List<T> getSearchResults() {
		Query<T> query = getQueryObject();
		
		if (query == null) {
			return new ArrayList<T>();
		}
		
		if (tagList.size() > 0) {
			query.withTags(tagList);
		}
		
		if (authorList.size() > 0) {
			query.withOwners(authorList);
		}
		
		if (supervisorChoice == TripleChoice.YES) {
			query.bySupervisor();
		} else if (supervisorChoice == TripleChoice.NO) {
			query.byStudent();
		}
		
		if (starredChoice == TripleChoice.YES){
			query.withStar();
		} else if (starredChoice == TripleChoice.NO) {
			query.withoutStar();
		}
		
		query.maxDuration(maxDurationInt)
			 .minDuration(minDurationInt)
			 .after(afterDate)
			 .before(beforeDate);
		
		return query.list();
	}
	
	public final Map<String, ?> toMap() {
		Builder<String,Object> b = new ImmutableMap.Builder<String,Object>();
		b.put("tags", tags);
		b.put("authors", authors);
		b.put("minDuration", minDuration);
		b.put("maxDuration", maxDuration);
		b.put("after", after);
		b.put("before", before);
		b.put("supervisor", supervisor);
		b.put("starred", starred);
		
		return b.build();
	}
	
	protected abstract Query<T> getQueryObject();
}
