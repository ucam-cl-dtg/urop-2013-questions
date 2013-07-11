package uk.ac.cam.sup;

import static org.junit.Assert.assertEquals;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
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
				.setString(0, "mr595")
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
