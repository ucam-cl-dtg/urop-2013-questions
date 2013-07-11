package uk.ac.cam.sup;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.Tag;

public class QuestionModelTest extends GenericTest {
	@Test
	public void databaseQueryShouldNotThrowExceptions () {
		session.createQuery("from Question");
	}
	
	@Test
	public void savingAQuestionToDatabase() {
		int size = session.createQuery("from Question").list().size();
		session.save(new Question());
		session.getTransaction().commit();
		assertEquals(size+1, session.createQuery("from Question").list().size());
	}
	
	@Test
	public void afterAddingATagItIsAnElementOfTagSet () {
		Question q = new Question();
		Tag t = Tag.fromString("a");
		q.addTag(t);
		assertTrue(q.getTags().contains(t));
	}
	
	@Test
	public void afterRemovingATagItIsNoLongerAnElementOfTagSet () {
		Question q = new Question();
		Tag t = Tag.fromString("a");
		q.addTag(t);
		q.removeTag(Tag.fromString("a"));
		assertFalse(q.getTags().contains(t));
	}

}
