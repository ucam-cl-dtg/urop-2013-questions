package uk.ac.cam.sup;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.User;

public class QuestionModelTest extends GenericTest {
	@Test
	public void databaseQueryShouldNotThrowExceptions () {
		session.createQuery("from Question");
	}
	
	@Test
	public void databaseShouldBeEmpty() {
		//assertEquals(0, session.createQuery("from User").list().size());
		assertEquals(0, session.createQuery("from Question").list().size());
	}
	
	@Test
	public void savingAUserToDatabase() {
		User u = new User("abc123");
		session.save(u);
		session.save(new Question(u));
		session.getTransaction().commit();
		assertEquals(1, session.createQuery("from Question").list().size());
	}
}
