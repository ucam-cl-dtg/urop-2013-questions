package uk.ac.cam.sup.form;

import javax.ws.rs.FormParam;

import uk.ac.cam.sup.exceptions.FormValidationException;
import uk.ac.cam.sup.queries.QuestionQuery;

public class TagAdd {
	@FormParam("qid")
	private int qid;
	
	@FormParam("newTags")
	private String newTags;
	
	private boolean validated = false;
	
	public TagAdd validate() throws FormValidationException{
		if(newTags == null || newTags.trim().length() < 1){
			throw new FormValidationException("You tried to add an empty tag!");
		}
		if(QuestionQuery.get(qid) == null){
			throw new FormValidationException("Question you are trying to edit does not exist!");
		}
		
		validated = true;
		return this;
	}
	
	private void checkValidity() throws FormValidationException {
		if(!validated) throw new FormValidationException("Form is not yet validated!");
	}
	
	public int getQid() throws FormValidationException {
		checkValidity();
		return qid;
	}
	
	public String getNewTagsString() throws FormValidationException {
		checkValidity();
		return newTags;
	}
	public String[] getNewTags() throws FormValidationException{
		checkValidity();
		if(newTags == null || newTags == ""){return null;}
		return newTags.split(",");
	}
}
