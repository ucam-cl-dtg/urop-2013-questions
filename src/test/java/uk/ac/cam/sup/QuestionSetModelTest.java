package uk.ac.cam.sup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import org.junit.Test;

import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.models.User;

public class QuestionSetModelTest extends GenericTest {
	@Test
	public void databaseQueryShouldNotThrowExceptions () {
		session.createQuery("from QuestionSet");
	}
	
	@Test
	public void savingAQuestionToDatabase() {
		int size = session.createQuery("from QuestionSet").list().size();
		User a = new User("abc123");
		session.save(new QuestionSet(a));
		assertEquals(size+1, session.createQuery("from QuestionSet").list().size());
	}
	
	@Test
	public void afterAddingATagItIsAnElementOfTagSet () {
		QuestionSet q = new QuestionSet(new User("abc123"));
		Tag t = new Tag("abc");
		q.addTag(t);
		assertTrue(q.getTags().contains(t));
	}
	
	@Test
	public void afterRemovingATagItIsNoLongerAnElementOfTagSet () {
		QuestionSet q = new QuestionSet(new User("abc123"));
		Tag t = new Tag("abc");
		q.addTag(t);
		q.removeTag(new Tag("abc"));
		assertFalse(q.getTags().contains(t));
	}
	
	@Test
	public void afterAddingAQuestionItIsElementOfSet(){
		QuestionSet qs = new QuestionSet(new User("abc123"));
		Question q = new Question(new User("abc123"));
		qs.addQuestion(q);
		assertTrue(qs.getQuestions().contains(q));
	}
	
	@Test
	public void afterRemovingAQuestionItIsElementOfSet(){
		QuestionSet qs = new QuestionSet(new User("abc123"));
		Question q = new Question(new User("abc123"));
		qs.addQuestion(q);
		qs.removeQuestion(q);
		assertFalse(qs.getQuestions().contains(q));
	}
	
	@Test
	public void expectedDurationIsSumOfQuestionsExpectedDurations () {
		QuestionSet s = (QuestionSet) session
				.createQuery("from QuestionSet where id=1")
				.uniqueResult();
		Set<Question> qs = s.getQuestions();
		int sum = 0;
		for (Question q: qs) {
			sum += q.getExpectedDuration();
		}
		
		assertEquals (sum, s.getExpectedDuration());
	}
}
