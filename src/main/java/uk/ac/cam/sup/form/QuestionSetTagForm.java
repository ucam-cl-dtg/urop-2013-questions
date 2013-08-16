package uk.ac.cam.sup.form;

import javax.ws.rs.FormParam;

import uk.ac.cam.sup.exceptions.FormValidationException;
import uk.ac.cam.sup.queries.QuestionSetQuery;

public class QuestionSetTagForm extends TagForm {
	@FormParam("setid")
	private int setid;	
	
	public QuestionSetTagForm validate() throws FormValidationException{
		super.validate();
		
		if(QuestionSetQuery.get(setid) == null){
			throw new FormValidationException("Question set you are trying to edit does not exist!");
		}
		
		validated = true;
		return this;
	}
	
	public int getSetId() throws FormValidationException {
		checkValidity();
		return setid;
	}
}
