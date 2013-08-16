package uk.ac.cam.sup.form;

import javax.ws.rs.FormParam;

import uk.ac.cam.sup.exceptions.FormValidationException;
import uk.ac.cam.sup.queries.QuestionQuery;

public class QuestionTagAdd extends TagForm {
	@FormParam("qid")
	private int qid;	
	
	public QuestionTagAdd validate() throws FormValidationException{
		super.validate();
		
		if(QuestionQuery.get(qid) == null){
			throw new FormValidationException("Question you are trying to edit does not exist!");
		}
		
		validated = true;
		return this;
	}
	
	public int getQid() throws FormValidationException {
		checkValidity();
		return qid;
	}
	
	public String getNewTagsString() throws FormValidationException {
		return getTagsString();
	}
	
	public String[] getNewTags() throws FormValidationException {
		return getTags();
	}
}
