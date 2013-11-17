package uk.ac.cam.sup.controllers;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.google.common.collect.ImmutableMap;

@Path("/overview")
public class OverviewController {

	@GET 
	@Path("/")
	@Produces("application/json")
	public Map<String,?> overview() {
		return ImmutableMap.<String,Object>builder().build();
	}
	
}
