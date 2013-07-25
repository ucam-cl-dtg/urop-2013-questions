package uk.ac.cam.sup.form;

import javax.ws.rs.FormParam;

import uk.ac.cam.sup.models.Data;
import uk.ac.cam.sup.queries.QuestionQuery;

public class QuestionEdit extends QuestionForm {
	public QuestionEdit(String content, String notes,
			Integer setID, Integer expectedDuration) {
		super(content, notes, setID, expectedDuration);
	}

	private boolean validated = false;
	
	@FormParam("id")
	private Integer id;
	
	@FormParam("minor")
	private Boolean isMinor;
	
	public Integer getId() throws RuntimeException {
		if(!validated) {
			throw new RuntimeException("Form was not yet validated");
		}
		return this.id;
	}
	
	public boolean isMinor() throws RuntimeException {
		if (!validated) {
			throw new RuntimeException("Form was not yet validated");
		}
		return this.isMinor;
	}
	
	public QuestionEdit validate() throws Exception {
		super.validate();
		if (id == null || QuestionQuery.get(id) == null) {
			throw new Exception("Question you're trying to edit does not exist");
		}
		
		if (isMinor == null) {
			isMinor = false;
		}
		
		validated = true;
		
		return this;
	}
}
