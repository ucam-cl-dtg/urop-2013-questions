package uk.ac.cam.sup.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.sup.exceptions.ModelNotFoundException;
import uk.ac.cam.sup.models.User;
import uk.ac.cam.sup.queries.UserQuery;


public abstract class GeneralController {
	@Context
	private HttpServletRequest request;
	
	private static Logger log = LoggerFactory.getLogger(GeneralController.class);
	
	/**
	 * Gets the raven ID of the user currently logged in.
	 * 
	 * @return
	 */
	protected String getCurrentUserID(){
		return getCurrentUser().getId();
	}
	
	/**
	 * Gets boolean value of user's supervisor bit
	 * 
	 * @return
	 */
	protected boolean isCurrentUserSupervisor() {
		return getCurrentUser().getSupervisor();
	}
	/**
	 * Gets the currently logged in user as User object.
	 * 
	 * @return
	 */
	protected User getCurrentUser(){
		// FIXME: Should be able to put APIFilter.USER_ATTR in place of "userId"
		//        but for some reason it isn't available on the class path.
		String uID = (String) request.getAttribute("userId");
		//String uID = "test123";
		User curUser;
		try {
			curUser = UserQuery.get(uID);
		} catch (ModelNotFoundException e) {

			log.info("User "+uID+" does not exist in the database. Attempting to save...");

			curUser = new User(uID);
			//curUser.saveOrUpdate();
			curUser.save();
			log.info("Added User " + uID + " to the database.");
		}
		return curUser;
		
	}
	
	protected String getDashboardURL(){
		return (String) request.getSession().getServletContext().getInitParameter("dashboardUrl");
	}
	
	protected String getApiKey(){
		return (String) request.getSession().getServletContext().getInitParameter("apiKey");
	}
	
	protected String getCurrentUrl() {
		return request.getRequestURL().toString();
	}
	
	protected String getCurrentUrlRemoveApi(){
		String curURL = getCurrentUrl();
		curURL = curURL.substring(0, curURL.indexOf("/api/")) + curURL.substring(curURL.indexOf("/api/") + 4);
		return curURL;
	}
	
	protected String getServerName() {
		return request.getServerName();
	}
	
	protected int getServerPort() {
		return request.getServerPort();
	}
	
}
