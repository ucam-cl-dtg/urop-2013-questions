package uk.ac.cam.sup.form;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;

import uk.ac.cam.sup.exceptions.FormValidationException;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.queries.TagQuery;

public abstract class TagForm {
	@FormParam("tags")
	protected String tags;
	
	protected boolean validated = false;
	
	public TagForm validate() throws FormValidationException {
		if(tags == null || tags.trim().length() < 1){
			throw new FormValidationException("You tried to add an empty tag!");
		}
		
		return this;
	}
	
	protected final void checkValidity() throws FormValidationException {
		if(!validated) throw new FormValidationException("Form is not yet validated!");
	}
	
	protected final void parseTags() {
		
	}
	
	public final String getTagsString() throws FormValidationException {
		checkValidity();
		return tags;
	}
	
	public final String[] getTags() throws FormValidationException{
		checkValidity();
		if(tags == null || tags == ""){return null;}
		return tags.split(",");
	}
	
	public final List<Tag> getTagsList() throws FormValidationException {
		checkValidity();
		List<Tag> result = new ArrayList<Tag>();
		
		String[] tagsArray = tags.split(",");
		for (String s: tagsArray) {
			Tag t = TagQuery.get(s.trim());
			result.add(t);
		}
		
		return result;
	}
}
