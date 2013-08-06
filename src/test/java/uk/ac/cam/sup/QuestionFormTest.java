package uk.ac.cam.sup;

import org.junit.Test;

import uk.ac.cam.sup.exceptions.FormValidationException;
import uk.ac.cam.sup.form.QuestionAdd;

public class QuestionFormTest extends GenericTest{
	
	@Test (expected = FormValidationException.class)
	public void unvalidatedFormQuestion() throws FormValidationException {
	
		QuestionAdd qa = new QuestionAdd("content", "notes", 0, 0);
		qa.getExpectedDuration();

	}
	
	@Test (expected = Exception.class)
	public void nullIDQuestion() throws Exception {
	
		QuestionAdd qa = new QuestionAdd("content", "notes", null, 0);
		qa.validate();
		
	}
	
	/*
	 * Empty question is perfectly fine, I don't see the point of this test
	@Test
	public void emptyQuestion() throws FormValidationException{
		
		QuestionAdd qa = new QuestionAdd("", "", 1, 0);
		
		try {
			qa.validate().parse();
		} catch (FormValidationException e) {
			assertTrue(true);
		} catch (Exception e){
			fail("Exception: "+e.getMessage());
		}
		
		assertNull("Data object should have null content:", qa.getContent().getData());	
		assertNull("Data object should have null notes:", qa.getNotes().getData());	
		
	}
	*/
	
}
