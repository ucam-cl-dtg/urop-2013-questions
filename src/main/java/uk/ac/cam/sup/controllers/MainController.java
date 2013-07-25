package uk.ac.cam.sup.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import uk.ac.cam.sup.util.WorldStrings;

import com.googlecode.htmleasy.RedirectException;

@Path(WorldStrings.URL_PREFIX + "/")
public class MainController extends GeneralController {
	
	@GET
	@Path("/")
	public void indexPage() {
		throw new RedirectException("/app");
	}

}
