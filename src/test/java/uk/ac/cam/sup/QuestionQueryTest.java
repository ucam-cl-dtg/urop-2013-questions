package uk.ac.cam.sup;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.models.User;
import uk.ac.cam.sup.queries.QuestionQuery;

public class QuestionQueryTest extends GenericTest {
	User a = new User("u1");
	User b = new User("u2");
	User c = new User("u3");
	Question p = new Question(a);
	Question q = new Question(a);
	Question r = new Question(b);
	Question s = new Question(c);

	private void fillDB() {

		b.setSupervisor(true);
		p.addTag(new Tag("Algorithms"));
		p.addTag(new Tag("Sorting"));
		p.addTag(new Tag("asdf"));
		q.addTag(new Tag("asdf"));
		r.addTag(new Tag("asdf"));
		r.addTag(new Tag("Sorting"));
		s.addTag(new Tag("Dynamic Programming"));

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
		
		session.saveOrUpdate(a);
		session.saveOrUpdate(b);
		session.saveOrUpdate(c);
		session.saveOrUpdate(p);
		session.saveOrUpdate(q);
		session.saveOrUpdate(r);
		session.saveOrUpdate(s);

		p = (Question) session.createQuery("from Question where owner_id = ?")
				.setString(0, "u1").list().get(0);
		q = (Question) session.createQuery("from Question where owner_id = ?")
				.setString(0, "u1").list().get(1);
		r = (Question) session.createQuery("from Question where owner_id = ?")
				.setString(0, "u2").list().get(0);
		s = (Question) session.createQuery("from Question where owner_id = ?")
				.setString(0, "u3").list().get(0);

		session.getTransaction().commit();
		session = HibernateUtil.getTransaction();
	}

	@SuppressWarnings("unchecked")
	private void cleanDB() {
		List<Question> qList = null;
		List<Question> childList = null;
		try {
			session = HibernateUtil.getTransaction();
			for (int i = 1; i <= 3; i++) {
				// session.beginTransaction();
				qList = session.createQuery("from Question where owner_id = ?")
						.setString(0, "u" + i).list();
				// session.getTransaction().commit();
				for (Question x : qList) {
					// session.beginTransaction();
					childList = session
							.createQuery("from Question where parent_id = ?")
							.setInteger(0, x.getId()).list();
					for (Question y : childList) {
						y.setParent(null);
						session.update(y);
					}
					// session.getTransaction().commit();
					// session.beginTransaction();
					session.delete(x);
					// session.getTransaction().commit();
				}

				// session.beginTransaction();
				session.delete(session.createQuery("from User where id = ?")
						.setString(0, "u" + i).uniqueResult());
				// session.getTransaction().commit();
			}
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().commit();
		}

	}

	@Test
	public void dontFilter() {
		// cleanDB();
		fillDB();

		QuestionQuery qq = QuestionQuery.all();
		List<Question> l = qq.list();

		boolean pExists = false;
		boolean qExists = false;
		boolean rExists = false;
		boolean sExists = false;
		for (Question x : l) {
			if (p.getId() == x.getId())
				pExists = true;
			if (q.getId() == x.getId())
				qExists = true;
			if (r.getId() == x.getId())
				rExists = true;
			if (s.getId() == x.getId())
				sExists = true;
		}

		assertTrue(pExists && qExists && rExists && sExists);

		cleanDB();
	}

	@Test
	public void filterByStarred() {
		fillDB();

		QuestionQuery qq = QuestionQuery.all();
		qq.withStar();
		List<Question> l = qq.list();
		boolean star = true;
		for (Question x : l) {
			if (!x.isStarred())
				star = false;
		}

		qq = QuestionQuery.all();
		qq.withoutStar();
		l = qq.list();
		boolean nostar = true;
		for (Question x : l) {
			if (x.isStarred())
				nostar = false;
		}

		assertTrue(star && nostar);
		cleanDB();
	}

	@Test
	public void filterByTag() {
		fillDB();

		QuestionQuery qq = QuestionQuery.all();
		List<Tag> tags = new LinkedList<Tag>();
		Tag tag1 = new Tag("Algorithms");
		Tag tag2 = new Tag("Sorting");
		tags.add(tag1);
		tags.add(tag2);
		qq.withTags(tags);
		List<Question> l = qq.list();

		boolean works = true;
		for (Question x : l) {
			if (!(x.getTags().contains(tag1) || x.getTags().contains(tag2))) {
				works = false;
			}
		}

		assertTrue(works);

		cleanDB();
	}

	@Test
	public void filterByUser() {
		fillDB();

		QuestionQuery qq = QuestionQuery.all();
		List<User> users = new LinkedList<User>();
		users.add(new User("u1"));
		users.add(new User("u3"));
		qq.withOwners(users);

		List<Question> l = qq.list();

		boolean works = true;
		for (Question x : l) {
			if (!(x.getOwner().equals(new User("u1")))
					&& !(x.getOwner().equals(new User("u3"))))
				works = false;
		}

		assertTrue(works);

		cleanDB();
	}

	@Test
	public void filterByStudent() {
		fillDB();

		QuestionQuery qq = QuestionQuery.all();
		qq.byStudent();

		List<Question> l = qq.list();

		boolean works = true;
		for (Question x : l) {
			if (x.getOwner().getSupervisor())
				works = false;
		}

		assertTrue(works);

		cleanDB();
	}

	@Test
	public void filterBySupervisor() {
		fillDB();

		QuestionQuery qq = QuestionQuery.all();
		qq.bySupervisor();

		List<Question> l = qq.list();

		boolean works = true;
		for (Question x : l) {
			if (!x.getOwner().getSupervisor())
				works = false;
		}

		assertTrue(works);

		cleanDB();
	}

	@Test
	public void filterByDate() {
		fillDB();

		Date begin = new Date(5000L);
		Date end = new Date(10000L);

		QuestionQuery qq = QuestionQuery.all();
		qq.after(begin).before(end);

		List<Question> l = qq.list();

		boolean works = true;
		for (Question x : l) {
			if (x.getTimeStamp().before(new Date(5000L)))
				works = false;
			if (x.getTimeStamp().after(new Date(10000L)))
				works = false;
		}

		assertTrue(works);

		cleanDB();
	}

	@Test
	public void filterByUsageCount() {
		fillDB();

		QuestionQuery qq = QuestionQuery.all();
		qq.minUsages(30).maxUsages(80);

		List<Question> l = qq.list();

		boolean works = true;
		for (Question x : l) {
			if (x.getUsageCount() < 30)
				works = false;
			if (x.getUsageCount() > 80)
				works = false;
		}

		assertTrue(works);

		cleanDB();
	}

	@Test
	public void filterByParent() {
		fillDB();

		QuestionQuery qq = QuestionQuery.all();
		qq.withParent(p.getId());
		List<Question> l = qq.list();

		boolean works = true;
		for (Question x : l) {
			if (!x.getParent().equals(p))
				works = false;
		}

		assertTrue(works);

		cleanDB();
	}
	
	@Test
	public void filterByDuration(){
		fillDB();
		
		QuestionQuery qq = QuestionQuery.all();
		qq.maxDuration(60);
		qq.minDuration(30);
		List<Question> l = qq.list();
		
		boolean works = true;
		for (Question x : l) {
			if (x.getExpectedDuration() > 60 || x.getExpectedDuration() < 30)
				works = false;
		}

		assertTrue(works);
		
		cleanDB();
	}

}
