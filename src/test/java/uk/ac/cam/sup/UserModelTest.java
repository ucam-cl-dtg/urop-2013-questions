package uk.ac.cam.sup;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.cam.sup.models.User;

public class UserModelTest extends GenericTest {

	@Test
	public void databaseQueryDoesNotCauseExceptions() {
		session.beginTransaction();
		session.createQuery("from User");
		session.getTransaction().commit();
	}
	
	@Test
	public void savingAUserToDatabaseIncreasesCount() {
		session.beginTransaction();
		User m = (User) session.createQuery("from User where id = ?")
				.setString(0, "abc123")
				.uniqueResult();
		if (m != null) {
			session.delete(m);
		}
		
		int size = session.createQuery("from User").list().size();
		session.save(m);
		session.getTransaction().commit();
		assertEquals(size+1, session.createQuery("from User").list().size());
	}

}
