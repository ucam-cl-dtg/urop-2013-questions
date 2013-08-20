package uk.ac.cam.sup.form;

import javax.ws.rs.FormParam;

import uk.ac.cam.sup.exceptions.FormValidationException;
import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.queries.QuestionSetQuery;

public class QuestionSetFork {
	
	@FormParam("setid") private String setId;
	@FormParam("name")  private String name;
	
	private Integer setIdInteger;
	QuestionSet questionSet;
	
	private boolean validated = false;
	
	public QuestionSetFork validate() throws FormValidationException {
		if (setId == null) {
			throw new FormValidationException("setId is null");
		}
		
		if (setId.equals("")) {
			throw new FormValidationException("No setId provided");
		}
		
		if (name == null) {
			name = "";
		}
		
		validated = true;
		return this;
	}
	
	public QuestionSetFork parse() throws FormValidationException {
		if ( ! this.validated) {
			throw new FormValidationException("Form was not yet validated");
		}
		
		try {
			setIdInteger = Integer.parseInt(setId);
		} catch (Exception e) {
			throw new FormValidationException("Malformed setId");
		}
		
		try {
			questionSet = QuestionSetQuery.get(setIdInteger);
		} catch (Exception e) {
			throw new FormValidationException("Problem retrieving requested set");
		}
		
		if (questionSet == null) {
			throw new FormValidationException("Requested set doesn't exist");
		}
		
		return this;
	}
	
	public QuestionSet getSet() throws FormValidationException {
		if ( ! this.validated) {
			throw new FormValidationException("Form was not yet validated");
		}
		
		return questionSet;
	}

	public String getName() throws FormValidationException {
		if ( ! this.validated) {
			throw new FormValidationException("Form was not yet validated");
		}
		
		return name;
	}
	
}
