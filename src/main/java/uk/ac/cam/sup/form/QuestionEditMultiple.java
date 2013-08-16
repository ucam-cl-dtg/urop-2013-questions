package uk.ac.cam.sup.form;

import java.util.List;

import javax.ws.rs.FormParam;

import uk.ac.cam.sup.exceptions.FormValidationException;
import uk.ac.cam.sup.models.QuestionSet;

/**
 * This class will allow question edits in multiple sets simultaneously.
 * 
 * @author jocbe
 *
 */
public class QuestionEditMultiple extends QuestionEdit{
	@FormParam("sets")
	private List<QuestionSet> sets;
	
	private boolean validated = false;
	
	@Override
	public QuestionEditMultiple validate() throws FormValidationException{
		super.validate();
		
		if(!super.isMinor() && (sets == null || sets.size() < 1)){
			throw new FormValidationException("The change is neither minor nor were any"
					+ "sets passed to edit the question inside");
		}
		validated = true;
		return this;
	}
	
	public List<QuestionSet> getSets() throws FormValidationException{
		if(!validated){throw new FormValidationException("Form not yet validated!");}
		return sets;
	}
}
