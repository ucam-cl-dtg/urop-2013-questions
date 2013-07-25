package uk.ac.cam.sup;

import javax.ws.rs.FormParam;

import org.junit.Test;

import uk.ac.cam.sup.form.QuestionAdd;
import uk.ac.cam.sup.form.QuestionForm;
import uk.ac.cam.sup.models.Data;

public class QuestionFormTest {
	
	@Test
	public void addEmptyQuestion() {
		
		// Empty content
		QuestionAdd qa = new QuestionAdd("", "", 0, 0);

		
	}
	
}
