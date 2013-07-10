package uk.ac.cam.sup;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.cam.sup.models.QuestionSet;

public class QuestionSetModelTest extends GenericTest {
	@Test
	public void databaseQueryShouldNotThrowExceptions () {
		session.createQuery("from QuestionSet");
	}
	
	@Test
	public void savingAQuestionToDatabase() {
		int size = session.createQuery("from QuestionSet").list().size();
		session.save(new QuestionSet());
		session.getTransaction().commit();
		assertEquals(size+1, session.createQuery("from QuestionSet").list().size());
	}
}
