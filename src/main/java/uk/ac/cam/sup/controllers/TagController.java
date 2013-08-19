package uk.ac.cam.sup.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.queries.TagQuery;

import com.google.common.collect.ImmutableMap;

@Path("/tags")
public class TagController extends GeneralController {
	
	@GET
	@Path("/json")
	@Produces("application/json")
	public List<?> produceFilteredJSON(
			@QueryParam("contains") String contains,
			@QueryParam("startswith") String startsWith,
			@QueryParam("endswith") String endsWith
	) {
		TagQuery tq = TagQuery.all();
		
		if (contains != null) {
			tq.contains(contains);
		}
		
		if (startsWith != null) {
			tq.startsWith(startsWith);
		}
		
		if (endsWith != null) {
			tq.endsWith(endsWith);
		}
		
		return tq.list();
	}
	
	@POST
	@Path("/autocomplete/{amount}")
	@Produces("application/json")
	public List<Map<String,String>> produceTagsWith(@FormParam("q") String st, @PathParam("amount") Integer amount){
		if(amount == null){
			amount = 10;
		}
		
		List<Tag> tags = TagQuery.all().contains(st).maxResults(amount).list();
		List<Map<String,String>> results = new ArrayList<Map<String,String>>();

		for(Tag t: tags){
			results.add(ImmutableMap.of("value", t.getName()));
		}
		return results;
	}
	
}
