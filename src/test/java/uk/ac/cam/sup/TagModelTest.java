package uk.ac.cam.sup;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.queries.TagQuery;

public class TagModelTest extends GenericTest {

		@Test
		public void tagCreatedFromStringHasProperName() {
			Tag t = TagQuery.get("abc");
			assertEquals("abc", t.getName());
		}
		
		@Test
		public void twoTagsWithSameNameAreEqual() {
			assertEquals(true, (TagQuery.get(new String("abc")).equals(TagQuery.get(new String("abc")))));
		}
}
