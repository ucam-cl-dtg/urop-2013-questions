package uk.ac.cam.sup;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import uk.ac.cam.sup.form.QuestionAdd;

public class QuestionFormTest extends GenericTest{
	
	@Test (expected = RuntimeException.class)
	public void unvalidatedFormQuestion() throws RuntimeException {
	
		QuestionAdd qa = new QuestionAdd("content", "notes", 0, 0);
		qa.getExpectedDuration();

	}
	
	@Test (expected = Exception.class)
	public void nullIDQuestion() throws Exception {
	
		QuestionAdd qa = new QuestionAdd("content", "notes", null, 0);
		qa.validate();
		
	}
	
	@Test
	public void emptyQuestion(){
		
		QuestionAdd qa = new QuestionAdd("", "", 0, 0);
		
		try {
			qa.validate();
		} catch (Exception e){
			fail();
		}
		
		assertNull("Data object should have null content:", qa.getContent().getData());	
		assertNull("Data object should have null notes:", qa.getNotes().getData());	
		
	}
	
}
