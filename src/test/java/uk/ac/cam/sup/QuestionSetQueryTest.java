package uk.ac.cam.sup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import uk.ac.cam.sup.exceptions.ModelNotFoundException;
import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.models.User;
import uk.ac.cam.sup.queries.QuestionQuery;
import uk.ac.cam.sup.queries.QuestionSetQuery;
import uk.ac.cam.sup.queries.TagQuery;
import uk.ac.cam.sup.queries.UserQuery;

public class QuestionSetQueryTest extends GenericTest {
	
	@Test
	public void allReturnsAllQuestionSets() {
		List<?> result = QuestionSetQuery.all().list();
		assertEquals(
				session.createQuery("from QuestionSet").list().size(),
				result.size()
		);
	}
	
	@Test
	public void allElementsReturnedByWithTagsHaveOneOfTheTagsSpecified() {
		List<Tag> tags = new ArrayList<Tag>();
		tags.add(TagQuery.get("Algorithms"));
		tags.add(TagQuery.get("Sorting"));
		tags.add(TagQuery.get("Discrete Mathematics"));
		List<?> result = QuestionSetQuery.all().withTags(tags).list();
		for (Object q: result) {
			boolean contains = false;
			for (Tag t: tags) {
				if (((QuestionSet)q).getTags().contains(t)) {
					contains = true;
					break;
				}
			}
			if (!contains) {
				fail ("One of the results doesn't have any of the tags");
			}
		}
	}
	
	@Test
	public void noElementsAreOmittedByWithTagsFilter () {
		List<Tag> tags = new ArrayList<Tag>();
		tags.add(TagQuery.get("Algorithms"));
		tags.add(TagQuery.get("Discrete Mathematics"));
		List<?> all = QuestionSetQuery.all().list();
		List<?> result = QuestionSetQuery.all().withTags(tags).list();
		for (Object q: all) {
			Set<?> qtags = ((QuestionSet)q).getTags();
			for (Tag t: tags) {
				if (qtags.contains(t) && !result.contains(q)) {
					fail (((QuestionSet)q).getName()+" was omitted");
				}
			}
		}
	}
	
	@Test
	public void allElementsReturnedByWithUsersHaveOneOfTheUsersSpecified() {
		List<User> users = new ArrayList<User>();
		users.add(new User("mr595"));
		users.add(new User("as123"));
		List<?> result = QuestionSetQuery.all().withUsers(users).list();
		for (Object q: result) {
			boolean contains = false;
			for (User u: users) {
				if (((QuestionSet)q).getOwner().getId().equals(u.getId())) {
					contains = true;
					break;
				}
			}
			if (!contains) {
				fail ("One of the results is owned by some other user");
			}
		}
	}
	
	@Test
	public void noElementsAreOmittedByWithUsersFilter () {
		List<User> user = new ArrayList<User>();
		user.add(new User("mr595"));
		user.add(new User("as123"));
		List<?> all = QuestionSetQuery.all().list();
		List<?> result = QuestionSetQuery.all().withUsers(user).list();
		for (Object q: all) {
			User owner = ((QuestionSet)q).getOwner();
			for (User u: user) {
				if (owner.getId().equals(u.getId()) && !result.contains(q)) {
					fail (((QuestionSet)q).getName()+" was ommitted");
				}
			}
		}
	}
	
	@Test
	public void allElementsReturnedByWithStarHaveAStar() {
		List<?> result = QuestionSetQuery.all().withStar().list();
		for (Object o: result) {
			assertTrue(((QuestionSet)o).isStarred());
		}
	}
	
	@Test
	public void noElementsAreOmittedByWithStarFilter() {
		List<?> result = QuestionSetQuery.all().withStar().list();
		List<?> all = QuestionSetQuery.all().list();
		
		for (Object o: all) {
			if (((QuestionSet)o).isStarred() && !result.contains(o)) {
				fail (((QuestionSet)o).getName() + " was omitted");
			}
		}
	}
	
	@Test
	public void allElementsReturnedBySupervisorFilterHaveAStar() {
		List<?> result = QuestionSetQuery.all().bySupervisor().list();
		for (Object o: result) {
			assertTrue(((QuestionSet)o).getOwner().getSupervisor());
		}
	}
	
	@Test
	public void noElementsAreOmittedBySupervisorFilter() {
		List<?> result = QuestionSetQuery.all().bySupervisor().list();
		List<?> all = QuestionSetQuery.all().list();
		
		for (Object o: all) {
			if (((QuestionSet)o).getOwner().getSupervisor() && !result.contains(o)) {
				fail (((QuestionSet)o).getName() + " was omitted");
			}
		}
	}
	
	@Test
	public void allElementsReturnedByStudentFilterHaveAStar() {
		List<?> result = QuestionSetQuery.all().byStudent().list();
		for (Object o: result) {
			assertFalse(((QuestionSet)o).getOwner().getSupervisor());
		}
	}
	
	@Test
	public void noElementsAreOmittedByStudentFilter() {
		List<?> result = QuestionSetQuery.all().byStudent().list();
		List<?> all = QuestionSetQuery.all().list();
		
		for (Object o: all) {
			if (!((QuestionSet)o).getOwner().getSupervisor() && !result.contains(o)) {
				fail (((QuestionSet)o).getName() + " was omitted");
			}
		}
	}
	
	@Test
	public void allElementsReturnedByAfterFilterHaveAppropriateTimestamp () {
		Date d = new Date(1373497200000L);
		List<?> result = QuestionSetQuery.all().after(d).list();
		for (Object o: result) {
			if (!((QuestionSet)o).getTimeStamp().after(d)) {
				fail (((QuestionSet)o).getName() + " is wrong");
			}
		}
	}
	
	@Test
	public void noElementsAreOmittedByAfterFilter () {
		Date d = new Date(1373497200000L);
		List<?> result = QuestionSetQuery.all().after(d).list();
		List<?> all = QuestionSetQuery.all().list();
		
		for (Object o: all) {
			Date ts = ((QuestionSet)o).getTimeStamp();
			if (ts.after(d)	&& !result.contains(o)) {
				fail (((QuestionSet)o).getName() + " was omitted");
			}
		}
	}
	
	@Test
	public void allElementsReturnedByBeforeFilterHaveAppropriateTimestamp () {
		Date d = new Date(1373497200000L);
		List<?> result = QuestionSetQuery.all().before(d).list();
		for (Object o: result) {
			if (!((QuestionSet)o).getTimeStamp().before(d)) {
				fail (((QuestionSet)o).getName() + " is wrong");
			}
		}
	}
	
	@Test
	public void noElementsAreOmittedByBeforeFilter () {
		Date d = new Date(1373497200000L);
		List<?> result = QuestionSetQuery.all().before(d).list();
		List<?> all = QuestionSetQuery.all().list();
		
		for (Object o: all) {
			Date ts = ((QuestionSet)o).getTimeStamp();
			if (ts.before(d) && !result.contains(o)) {
				fail (((QuestionSet)o).getName() + " was omitted");
			}
		}
	}
	
	@Test
	public void allElementsReturnedByMaxDurationFilterHaveAppropriateLength() {
		List<?> result = QuestionSetQuery.all().maxDuration(10).list();
		for (Object o: result) {
			if (((QuestionSet)o).getExpectedDuration() > 10) {
				fail(((QuestionSet)o).getName() + " is too long");
			}
		}
	}
	
	@Test
	public void noElementsAreOmittedByMaxDurationFilter() {
		List<?> result = QuestionSetQuery.all().maxDuration(10).list();
		List<?> all = QuestionSetQuery.all().list();

		for (Object o: all) {
			if (((QuestionSet)o).getExpectedDuration() <= 10
					&& !result.contains(o)) {
				fail(((QuestionSet)o).getName() + "(" + ((QuestionSet)o).getExpectedDuration()+") was omitted");
			}
		}
	}
	
	@Test
	public void allElementsReturnedByMinDurationFilterHaveAppropriateLength() {
		List<?> result = QuestionSetQuery.all().minDuration(10).list();
		for (Object o: result) {
			if (((QuestionSet)o).getExpectedDuration() < 10) {
				fail(((QuestionSet)o).getName() + " is too long");
			}
		}
	}
	
	@Test
	public void noElementsAreOmittedByMinDurationFilter() {
		List<?> result = QuestionSetQuery.all().minDuration(10).list();
		List<?> all = QuestionSetQuery.all().list();

		for (Object o: all) {
			if (((QuestionSet)o).getExpectedDuration() >= 10
					&& !result.contains(o)) {
				fail(((QuestionSet)o).getName() + " was omitted");
			}
		}
	}
	
	@Test
	public void allElementsReturnedByHaveFilterHaveGivenQuestion() {
		Question q = QuestionQuery.get(2);
		List<?> result = QuestionSetQuery.all().have(q).list();
		
		for(Object o: result) {
			if (!((QuestionSet)o).getQuestions().contains(q)) {
				fail(((QuestionSet)o).getName() + " doesn't have given question");
			}
		}
	}
	
	@Test
	public void noElementsAreOmittedByHaveFilter() {
		Question q = QuestionQuery.get(2);
		List<?> result = QuestionSetQuery.all().have(q).list();
		List<?> all = QuestionSetQuery.all().list();
		
		for(Object o: all) {
			if (((QuestionSet)o).getQuestions().contains(q)
					&& !result.contains(o)) {
				fail(((QuestionSet)o).getName() + " was omitted");
			}
		}
	}
	
	@Test
	public void countingRowsWorks() throws ModelNotFoundException{
		QuestionSetQuery qsq = QuestionSetQuery.all().withUser(UserQuery.get("jcb98"));
		assertTrue(qsq.size() > 0);
	}
}
