package uk.ac.cam.sup;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.models.User;
import uk.ac.cam.sup.queries.QuestionSetQuery;

public class QuestionSetQueryTest extends GenericTest {
	
	@Test
	public void allReturnsAllQuestionSets() {
		List result = QuestionSetQuery.all().list();
		assertEquals(
				session.createQuery("from QuestionSet").list().size(),
				result.size()
		);
	}
	
	@Test
	public void allElementsReturnedByWithTagsHaveOneOfTheTagsSpecified() {
		List<Tag> tags = new ArrayList<Tag>();
		tags.add(new Tag("Algorithms"));
		tags.add(new Tag("Sorting"));
		tags.add(new Tag("Discrete Mathematics"));
		List result = QuestionSetQuery.all().withTags(tags).list();
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
	public void noElementsAreOmmitedByWithTagsFilter () {
		List<Tag> tags = new ArrayList<Tag>();
		tags.add(new Tag("Algorithms"));
		tags.add(new Tag("Discrete Mathematics"));
		List all = QuestionSetQuery.all().list();
		List result = QuestionSetQuery.all().withTags(tags).list();
		for (Object q: all) {
			Set qtags = ((QuestionSet)q).getTags();
			for (Tag t: tags) {
				if (qtags.contains(t) && !result.contains(q)) {
					fail (((QuestionSet)q).getName()+" was ommited");
				}
			}
		}
	}
	
	@Test
	public void allElementsReturnedByWithUsersHaveOneOfTheUsersSpecified() {
		List<User> users = new ArrayList<User>();
		users.add(new User("mr595"));
		users.add(new User("as123"));
		List result = QuestionSetQuery.all().withUsers(users).list();
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
	public void noElementsAreOmmitedByWithUsersFilter () {
		List<User> user = new ArrayList<User>();
		user.add(new User("mr595"));
		user.add(new User("as123"));
		List all = QuestionSetQuery.all().list();
		List result = QuestionSetQuery.all().withUsers(user).list();
		for (Object q: all) {
			User owner = ((QuestionSet)q).getOwner();
			for (User u: user) {
				if (owner.getId().equals(u.getId()) && !result.contains(q)) {
					fail (((QuestionSet)q).getName()+" was ommited");
				}
			}
		}
	}
	
	@Test
	public void allElementsReturnedByWithStarHaveAStar() {
		List result = QuestionSetQuery.all().withStar().list();
		for (Object o: result) {
			assertTrue(((QuestionSet)o).isStarred());
		}
	}
	
	@Test
	public void noElementsAreOmmittedByWithStarFilter() {
		List result = QuestionSetQuery.all().withStar().list();
		List all = QuestionSetQuery.all().list();
		
		for (Object o: all) {
			if (((QuestionSet)o).isStarred() && !result.contains(o)) {
				fail (((QuestionSet)o).getName() + " was omitted");
			}
		}
	}
	
}
