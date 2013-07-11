package uk.ac.cam.sup;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

		q.setStarred(true);
		r.setStarred(true);
		
		session.beginTransaction();
		session.saveOrUpdate(a);
		session.saveOrUpdate(b);
		session.saveOrUpdate(c);
		session.saveOrUpdate(p);
		session.saveOrUpdate(q);
		session.saveOrUpdate(r);
		session.saveOrUpdate(s);

		session.getTransaction().commit();
		session.beginTransaction();

		p = (Question) session.createQuery("from Question where owner_id = ?")
				.setString(0, "u1").list().get(0);
		q = (Question) session.createQuery("from Question where owner_id = ?")
				.setString(0, "u1").list().get(1);
		r = (Question) session.createQuery("from Question where owner_id = ?")
				.setString(0, "u2").uniqueResult();
		s = (Question) session.createQuery("from Question where owner_id = ?")
				.setString(0, "u3").uniqueResult();

		session.getTransaction().commit();
	}

	private void cleanDB() {
		session.beginTransaction();
		try {
			for (Object o : session
					.createQuery("from Question where owner_id = ?")
					.setString(0, "u1").list()) {
				session.delete(o);
			}
			for (Object o : session
					.createQuery("from Question where owner_id = ?")
					.setString(0, "u2").list()) {
				session.delete(o);
			}
			for (Object o : session
					.createQuery("from Question where owner_id = ?")
					.setString(0, "u3").list()) {
				session.delete(o);
			}

			session.delete(session.createQuery("from User where id = ?")
					.setString(0, "u1").uniqueResult());
			session.delete(session.createQuery("from User where id = ?")
					.setString(0, "u2").uniqueResult());
			session.delete(session.createQuery("from User where id = ?")
					.setString(0, "u3").uniqueResult());
		} catch (Exception e) {

		}
		session.getTransaction().commit();
	}

	@Test
	public void dontFilter() {
		cleanDB();
		fillDB();

		QuestionQuery qq = QuestionQuery.all();
		List<Question> l = qq.results();

		boolean pExists = false;
		boolean qExists = false;
		boolean rExists = false;
		boolean sExists = false;
		for (Question x : l) {
			System.out.println(";alsdkfj");
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
		List<Question> l = qq.results();
		boolean star = true;
		for(Question x : l){
			if(!x.getStarred()) star = false;
		}
		
		qq = QuestionQuery.all();
		qq.withoutStar();
		l = qq.results();
		boolean nostar = true;
		for(Question x : l) {
			if(x.getStarred()) nostar = false;
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
		List<Question> l = qq.results();
		
		boolean works = true;
		for(Question x : l) {
			if(!(x.getTags().contains(tag1) ||
					x.getTags().contains(tag2))) {
				works = false;
			}
		}
		
		System.out.println("#####################  " + l.size());
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
		qq.withUsers(users);
		
		List<Question> l = qq.results();
		
		boolean works = true;
		for(Question x : l) {
			if(!(x.getOwner().equals(new User("u1"))) 
					&& !(x.getOwner().equals(new User("u3")))) 
				works = false;
		}
		
		assertTrue(works);
		
		cleanDB();
	}
	
	@Test
	public void filterByStudent(){
		fillDB();
		
		QuestionQuery qq = QuestionQuery.all();
		qq.byStudent();
		
		List<Question> l = qq.results();
		
		boolean works = true;
		for(Question x : l) {
			if (x.getOwner().getSupervisor()) works = false;
		}
		
		assertTrue(works);
		
		cleanDB();
	}
	@Test
	public void filterBySupervisor(){
		fillDB();

		QuestionQuery qq = QuestionQuery.all();
		qq.byStudent();
		
		List<Question> l = qq.results();
		
		boolean works = true;
		for(Question x : l) {
			if (!x.getOwner().getSupervisor()) works = false;
		}
		
		assertTrue(works);
		
		cleanDB();
	}
	
	@Test
	public void filterByDate(){
		fillDB();
		
		cleanDB();
	}
}
