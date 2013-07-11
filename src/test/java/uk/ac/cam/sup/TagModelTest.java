package uk.ac.cam.sup;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.cam.sup.models.Tag;

public class TagModelTest extends GenericTest {

		@Test
		public void tagCreatedFromStringHasProperName() {
			Tag t = Tag.fromString("asdf");
			assertEquals("asdf", t.getName());
		}
		
		@Test
		public void twoTagsWithSameNameAreEqual() {
			assertEquals(true, (new Tag(new String("a")).equals(new Tag(new String("a")))));
		}
}
