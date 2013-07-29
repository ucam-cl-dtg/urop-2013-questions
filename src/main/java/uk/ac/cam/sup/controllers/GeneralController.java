package uk.ac.cam.sup.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import uk.ac.cam.sup.exceptions.ModelNotFoundException;
import uk.ac.cam.sup.models.User;
import uk.ac.cam.sup.queries.UserQuery;

public abstract class GeneralController {
	@Context
	private HttpServletRequest request;
	
	protected Object getSessionAttribute(String attr){
		return request.getSession().getAttribute(attr);
	}
	
	/**
	 * Gets the raven ID of the user currently logged in.
	 * 
	 * @return
	 */
	protected String getCurrentUserID(){
		return getCurrentUser().getId();
	}
	
	/**
	 * Gets the currently logged in user as User object.
	 * 
	 * @return
	 */
	protected User getCurrentUser(){
		String uID = (String)getSessionAttribute("RavenRemoteUser");
		User curUser;
		try {
			curUser = UserQuery.get(uID);
		} catch (ModelNotFoundException e) {
			curUser = new User(uID);
			curUser.saveOrUpdate();
		}
		return curUser;
	}
	
}
