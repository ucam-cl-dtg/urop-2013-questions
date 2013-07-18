package uk.ac.cam.sup;

import java.util.Date;

import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.models.User;

public abstract class GenericTest {
	protected static Session session;
	
	static User a;
	static User b;
	static User c;
	static Question p;
	static Question q;
	static Question r;
	static Question s;

	private static void fillDB() {
		session = HibernateUtil.getTransactionSession();
		
		a = new User("u1");
		b = new User("u2");
		c = new User("u3");
		p = new Question(a);
		q = new Question(a);
		r = new Question(b);
		s = new Question(c);
		
		Tag t = new Tag("Algorithms");
		Tag u = new Tag("Sorting");
		Tag v = new Tag("asdf");
		Tag w = new Tag("Dynamic programming");
		
		QuestionSet pq = new QuestionSet(a);
		pq.addQuestion(p);
		pq.addQuestion(q);
		pq.setName("Set p,q");
		
		QuestionSet rs = new QuestionSet(a);
		rs.addQuestion(r);
		rs.addQuestion(s);
		rs.setName("Set r,s");

		b.setSupervisor(true);
		p.addTag(t);
		p.addTag(u);
		p.addTag(v);
		q.addTag(v);
		r.addTag(v);
		r.addTag(u);
		s.addTag(w);

		q.star(true);
		r.star(true);
		p.setTimeStamp(new Date(6000));
		q.setTimeStamp(new Date(2000));
		r.setTimeStamp(new Date(9999));
		s.setTimeStamp(new Date(10000));

		p.setUsageCount(123);
		q.setUsageCount(50);
		r.setUsageCount(75);
		s.setUsageCount(4);

		q.setParent(p);
		s.setParent(p);
		r.setParent(s);

		p.setExpectedDuration(90);
		q.setExpectedDuration(5);
		r.setExpectedDuration(45);
		s.setExpectedDuration(50);
		
		session.save(t);
		session.save(u);
		session.save(v);
		session.save(w);
		session.save(a);
		session.save(b);
		session.save(c);
		session.save(p);
		session.save(q);
		session.save(r);
		session.save(s);
		session.save(rs);
		session.getTransaction().commit();
		
		session = HibernateUtil.getTransactionSession();

		p = (Question) session.createQuery("from Question where owner_id = ?")
				.setString(0, "u1").list().get(0);
		q = (Question) session.createQuery("from Question where owner_id = ?")
				.setString(0, "u1").list().get(1);
		r = (Question) session.createQuery("from Question where owner_id = ?")
				.setString(0, "u2").list().get(0);
		s = (Question) session.createQuery("from Question where owner_id = ?")
				.setString(0, "u3").list().get(0);


	}
	
	private static void cleanDB() {
		
	}
	
	@BeforeClass
	public static void setUp() throws Exception {
		session = HibernateUtil.getTransactionSession();
		session.getTransaction().commit();
		
		HibernateUtil.reconfigure();
		fillDB();
		session = HibernateUtil.getTransactionSession();
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		cleanDB();
		if (session.isOpen()) {
			session.close();
		}
	}
	
}
