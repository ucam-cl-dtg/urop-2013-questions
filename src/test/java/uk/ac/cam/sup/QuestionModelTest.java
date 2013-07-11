package uk.ac.cam.sup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.models.User;

public class QuestionModelTest extends GenericTest {
	@Test
	public void databaseQueryShouldNotThrowExceptions () {
		session.beginTransaction();
		session.createQuery("from Question");
		session.getTransaction().commit();
	}
	
	@Test
	public void savingAQuestionToDatabase() {
		session.beginTransaction();
		int size = session.createQuery("from Question").list().size();
		Question x = new Question(new User("abc123"));
		session.save(x);
		session.getTransaction().commit();
		assertEquals(size+1, session.createQuery("from Question").list().size());
	}
	
	@Test
	public void afterAddingATagItIsAnElementOfTagSet () {
		Question q = new Question(new User("abc123"));
		Tag t = Tag.fromString("a");
		q.addTag(t);
		assertTrue(q.getTags().contains(t));
	}
	
	@Test
	public void afterRemovingATagItIsNoLongerAnElementOfTagSet () {
		Question q = new Question(new User("abc123"));
		Tag t = Tag.fromString("a");
		q.addTag(t);
		q.removeTag(Tag.fromString("a"));
		assertFalse(q.getTags().contains(t));
		
	}
	
	@Test
	public void twoDifferentQuestionsAreNotEqual(){
		Question q = new Question(new User("abc123"));
		Question r = new Question(new User("abc123"));
		assertFalse(q.equals(r));
	}
	

}
