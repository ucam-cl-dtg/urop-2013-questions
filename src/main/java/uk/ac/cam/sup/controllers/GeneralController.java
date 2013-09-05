package uk.ac.cam.sup.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.sup.exceptions.ModelNotFoundException;
import uk.ac.cam.sup.models.User;
import uk.ac.cam.sup.queries.UserQuery;


public abstract class GeneralController {
	@Context
	private HttpServletRequest request;
	@Context
	private HttpServletResponse response;
	
	private static Logger log = LoggerFactory.getLogger(GeneralController.class);
	
	/**
	 * Gets the raven ID of the user currently logged in.
	 * 
	 * @return
	 */
	protected String getCurrentUserID(){
		User u = getCurrentUser();
		if(u == null) return null;
		else return u.getId();
	}
	protected String getCurrentUserName(){
		String id = getCurrentUserID();
		if(id == null) return null;
		else {
			User u = new User(id);
			return u.getName();
		}
	}
	
	protected boolean globalUser(){
		if(getCurrentUser() == null) return true;
		else return false;
	}
	/**
	 * Gets boolean value of user's supervisor bit
	 * 
	 * @return
	 */
	protected boolean isCurrentUserSupervisor() {
		User u = getCurrentUser();
		if(u == null) return true;
		else return u.getSupervisor();
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
		
		if(uID == null) return null;
		
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
	
	
	protected void sendUnauthorized() throws IOException{
		try {
			response.sendError(401, "You are not authorized to perform this action.");
		} catch (IOException e) {
			log.error("IOException occurred while trying to send 401 due to global key. Message: " + e.getMessage());
			throw e;
		}
	}
	
	protected void disallowGlobal() throws IOException{
		if(globalUser()) sendUnauthorized();
	}
}
