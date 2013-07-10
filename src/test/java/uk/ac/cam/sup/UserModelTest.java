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

public class UserModelTest {
	
	Session session;

	@Before
	public void setUp() throws Exception {
		Configuration configuration = new Configuration();
		configuration.configure();
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
				.applySettings(configuration.getProperties())
				.buildServiceRegistry();
		SessionFactory sessionFactory = configuration
				.buildSessionFactory(serviceRegistry);
		session = sessionFactory.openSession();
		session.beginTransaction();
	}

	@After
	public void tearDown() throws Exception {
		session.close();
	}

	@Test
	public void databaseShouldBeEmpty() {
		assertEquals(0, session.createQuery("from User").list().size());
	}
	
	@Test
	public void savingAUserToDatabase() {
		session.save(new User("mr595"));
		session.getTransaction().commit();
		assertEquals(1, session.createQuery("from User").list().size());
	}

}
