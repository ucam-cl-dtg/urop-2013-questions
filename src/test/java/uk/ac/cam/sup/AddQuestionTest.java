package uk.ac.cam.sup;

import javax.ws.rs.FormParam;

import org.junit.Test;

import uk.ac.cam.sup.form.QuestionAdd;
import uk.ac.cam.sup.form.QuestionForm;
import uk.ac.cam.sup.models.Data;

public class AddQuestionTest {
	
	@Test
	public void addEmptyQuestion() {

		String content = "";
		Data dcontent = new Data(true,"");
		
		String notes = "";
		Data dnotes = new Data(true,"");
		
		Integer setId = 0;
		
		Integer expectedDuration = 0;	
		
	}
	
}
