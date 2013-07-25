package uk.ac.cam.sup.controllers;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import uk.ac.cam.sup.queries.TagQuery;
import uk.ac.cam.sup.util.WorldStrings;

@Path(WorldStrings.URL_PREFIX+"/tags")
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
	
}
