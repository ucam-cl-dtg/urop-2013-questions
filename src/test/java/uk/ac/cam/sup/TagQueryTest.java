package uk.ac.cam.sup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.queries.TagQuery;

public class TagQueryTest extends GenericTest {

	@Test
	public void allReturnsAllTheTags() {
		assertEquals(
				session.createQuery("from Tag").list().size(),
				TagQuery.all().list().size()
		);
	}
	
	@Test
	public void allTagsReturnedByContainsFilterContainGivenPattern() {
		List<?> result = TagQuery.all().contains("ma").list();
		
		for (Object o: result) {
			Tag t = (Tag) o;
			assertTrue(t.getName().toLowerCase().contains("ma"));
		}
	}
	
	@Test
	public void noTagsAreOmittedByContainsFilter() {
		List<?> result = TagQuery.all().contains("ma").list();
		List<?> all = TagQuery.all().list();
		
		for (Object o: all) {
			Tag t = (Tag) o;
			if (t.getName().toLowerCase().contains("ma")
					&& !result.contains(t)) {
				fail(t.getName() + " was omitted");
			}
		}
	}
	
	@Test
	public void allTagsReturnedByStartsWithFilterContainGivenPattern() {
		List<?> result = TagQuery.all().startsWith("f").list();
		
		for (Object o: result) {
			Tag t = (Tag) o;
			assertTrue(t.getName().toLowerCase().startsWith("f"));
		}
	}
	
	@Test
	public void noTagsAreOmittedByStartsWithFilter() {
		List<?> result = TagQuery.all().startsWith("f").list();
		List<?> all = TagQuery.all().list();
		
		for (Object o: all) {
			Tag t = (Tag) o;
			if (t.getName().toLowerCase().startsWith("f")
					&& !result.contains(t)) {
				fail(t.getName() + " was omitted");
			}
		}
	}
	
	@Test
	public void allTagsReturnedByEndsWithFilterContainGivenPattern() {
		List<?> result = TagQuery.all().endsWith("s").list();
		
		for (Object o: result) {
			Tag t = (Tag) o;
			assertTrue(t.getName().toLowerCase().endsWith("s"));
		}
	}
	
	@Test
	public void noTagsAreOmittedByEndsWithFilter() {
		List<?> result = TagQuery.all().endsWith("s").list();
		List<?> all = TagQuery.all().list();
		
		for (Object o: all) {
			Tag t = (Tag) o;
			if (t.getName().toLowerCase().endsWith("s")
					&& !result.contains(t)) {
				fail(t.getName() + " was omitted");
			}
		}
	}

}
