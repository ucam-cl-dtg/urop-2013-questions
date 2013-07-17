package uk.ac.cam.sup;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;

import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.models.User;

public abstract class GenericTest {
	protected Session session;

	@Before
	public void setUp() throws Exception {
		session = HibernateUtil.getTransaction();
		session.saveOrUpdate(new User("abc123"));
		session.saveOrUpdate(new Tag("abc"));
		session.getTransaction().commit();
		session = HibernateUtil.getTransaction();
	}
	
	@After
	public void tearDown() throws Exception {
		cleanup();
		if (session.isOpen()) {
			session.close();
		}
	}
	
	
	private void cleanup(){
		session = HibernateUtil.getTransaction();
		for(Object o : session.createQuery("from Question where owner_id = ?")
				.setString(0, "abc123").list()){
			session.delete(o);
		}
		for(Object o : session.createQuery("from QuestionSet where owner_id = ?")
				.setString(0, "abc123").list()){
			session.delete(o);
		}
		session.delete(session.createQuery("from User where id = ?").setString(0, "abc123").uniqueResult());
		session.delete(session.createQuery("from Tag where id = ?").setString(0, "abc").uniqueResult());
		session.getTransaction().commit();
	}
}
