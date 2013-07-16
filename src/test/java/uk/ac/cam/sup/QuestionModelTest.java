package uk.ac.cam.sup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.models.User;

public class QuestionModelTest extends GenericTest {
	@Test
	public void databaseQueryShouldNotThrowExceptions () {
		try {
			session.createQuery("from Question");
		} catch (Exception e) {
			fail("Exception was thrown");
		}
	}
	
	@Test
	public void savingAQuestionToDatabase() {
		int size = session.createQuery("from Question").list().size();
		Question x = new Question(new User("abc123"));
		session.save(x);
		assertEquals(size+1, session.createQuery("from Question").list().size());
	}
	
	@Test
	public void afterAddingATagItIsAnElementOfTagSet () {
		Question q = new Question(new User("abc123"));
		Tag t = new Tag("abc");
		q.addTag(t);
		assertTrue(q.getTags().contains(t));
	}
	
	@Test
	public void afterRemovingATagItIsNoLongerAnElementOfTagSet () {
		Question q = new Question(new User("abc123"));
		Tag t = new Tag("abc");
		q.addTag(t);
		q.removeTag(new Tag("abc"));
		assertFalse(q.getTags().contains(t));
		
	}
	
	@Test
	public void twoDifferentQuestionsAreNotEqual(){
		Question q = (Question)session.createQuery("from Question where id = ?").setInteger(0, 1).uniqueResult();
		Question r = (Question)session.createQuery("from Question where id = ?").setInteger(0, 2).uniqueResult();
		assertFalse(q.equals(r));
	}
	

}
