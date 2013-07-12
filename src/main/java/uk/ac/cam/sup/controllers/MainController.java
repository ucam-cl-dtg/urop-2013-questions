package uk.ac.cam.sup.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import com.google.common.collect.ImmutableMap;
import com.googlecode.htmleasy.ViewWith;

@Path("/")
public class MainController {
	
	@Context
	HttpServletRequest request;
	
	@GET
	@Path("/")
	@ViewWith("/soy/main.index")
	public Map<String,?> indexPage() {
		return ImmutableMap.of();
	}

}
