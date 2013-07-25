package uk.ac.cam.sup.controllers;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import uk.ac.cam.sup.util.WorldStrings;

import com.google.common.collect.ImmutableMap;
import com.googlecode.htmleasy.ViewWith;

@Path(WorldStrings.URL_PREFIX + "/")
public class MainController extends GeneralController {
	
	@GET
	@Path("/")
	@ViewWith("/soy/main.index")
	public Map<String,?> indexPage() {
		return ImmutableMap.of();
	}

}
