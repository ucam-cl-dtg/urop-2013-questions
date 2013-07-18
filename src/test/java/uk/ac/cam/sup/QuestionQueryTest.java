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

	@Test
	public void dontFilter() {

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

	}

	@Test
	public void filterByStarred() {

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
	}

	@Test
	public void filterByTag() {

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

	}

	@Test
	public void filterByUser() {

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

	}

	@Test
	public void filterByStudent() {

		QuestionQuery qq = QuestionQuery.all();
		qq.byStudent();

		List<Question> l = qq.list();

		boolean works = true;
		for (Question x : l) {
			if (x.getOwner().getSupervisor())
				works = false;
		}

		assertTrue(works);

	}

	@Test
	public void filterBySupervisor() {

		QuestionQuery qq = QuestionQuery.all();
		qq.bySupervisor();

		List<Question> l = qq.list();

		boolean works = true;
		for (Question x : l) {
			if (!x.getOwner().getSupervisor())
				works = false;
		}

		assertTrue(works);

	}

	@Test
	public void filterByDate() {

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

	}

	@Test
	public void filterByUsageCount() {

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

	}

	@Test
	public void filterByParent() {

		QuestionQuery qq = QuestionQuery.all();
		qq.withParent(p.getId());
		List<Question> l = qq.list();

		boolean works = true;
		for (Question x : l) {
			if (!x.getParent().equals(p))
				works = false;
		}

		assertTrue(works);

	}
	
	@Test
	public void filterByDuration(){
		
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
		
	}

}
