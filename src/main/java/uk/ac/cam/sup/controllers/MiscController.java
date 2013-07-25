package uk.ac.cam.sup.controllers;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.google.common.collect.ImmutableMap;

@Path("/")
public class MiscController extends GeneralController {
	@GET
	@Path("fairytale")
	@Produces("application/json")
	public Map<String,String> fairytale(){
		return ImmutableMap.of("path", "http://schmoesknow.com/wp-content/uploads/2013/06/SW-8.jpg");
	}
}
