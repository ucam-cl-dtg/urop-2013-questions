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
	public void savingAQuestionToDatabase() {
		int size = session.createQuery("from Question").list().size();
		session.save(new Question());
		session.getTransaction().commit();
		assertEquals(size+1, session.createQuery("from Question").list().size());
	}
}
