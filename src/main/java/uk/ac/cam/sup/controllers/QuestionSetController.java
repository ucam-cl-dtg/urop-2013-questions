package uk.ac.cam.sup.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.queries.QuestionSetQuery;

import com.google.common.collect.ImmutableMap;

@Path("/sets")
public class QuestionSetController {
	
	@GET
	@Path("/json")
	@Produces("application/json")
	public List produceFilteredJSON (
			@QueryParam("tags") String tags
	) {
		QuestionSetQuery query = QuestionSetQuery.all();
		
		if (tags != null) {
			String[] tagstrings = tags.split(",");
			List<Tag> tagset = new ArrayList<Tag>();
			for (String t: tagstrings) {
				tagset.add(new Tag(t));
			}
			query.withTags(tagset);
		}
		
		return query.list();
	}
	
}
