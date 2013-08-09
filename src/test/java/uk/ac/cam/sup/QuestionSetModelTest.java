package uk.ac.cam.sup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.models.User;
import uk.ac.cam.sup.queries.TagQuery;

public class QuestionSetModelTest extends GenericTest {
	@Test
	public void databaseQueryShouldNotThrowExceptions () {
		session.createQuery("from QuestionSet");
	}
	
	@Test
	public void savingAQuestionToDatabase() {
		int size = session.createQuery("from QuestionSet").list().size();
		User u = (User) session.createQuery("from User").list().get(0);
		QuestionSet qs = new QuestionSet(u);
		qs.save();
		assertEquals(size+1, session.createQuery("from QuestionSet").list().size());
	}
	
	@Test
	public void afterAddingATagItIsAnElementOfTagSet () {
		QuestionSet q = new QuestionSet(new User("abc123"));
		Tag t = TagQuery.get("abc");
		q.addTag(t);
		assertTrue(q.getTags().contains(t));
	}
	
	@Test
	public void afterRemovingATagItIsNoLongerAnElementOfTagSet () {
		QuestionSet q = new QuestionSet(new User("abc123"));
		Tag t = TagQuery.get("abc");
		q.addTag(t);
		q.removeTag(TagQuery.get("abc"));
		assertFalse(q.getTags().contains(t));
	}
	
	@Test
	public void afterAddingAQuestionItIsAnElementOfSet(){
		QuestionSet qs = new QuestionSet(new User("abc123"));
		Question q = new Question(new User("abc123"));
		qs.addQuestion(q);
		List<Question> questions = qs.getQuestions();
		assertTrue(questions.contains(q));
	}
	
	@Test
	public void afterRemovingAQuestionItIsNotAnElementOfSet(){
		QuestionSet qs = new QuestionSet(new User("abc123"));
		Question q = new Question(new User("abc123"));
		qs.addQuestion(q);
		qs.removeQuestion(q);
		List<Question> questions = qs.getQuestions();
		assertFalse(questions.contains(q));
	}
	
	@Test
	public void expectedDurationIsSumOfQuestionsExpectedDurations () {
		QuestionSet s = (QuestionSet) session
				.createQuery("from QuestionSet where id=1")
				.uniqueResult();
		List<Question> qs = s.getQuestions();
		int sum = 0;
		for (Question q: qs) {
			sum += q.getExpectedDuration();
		}
		
		assertEquals (sum, s.getExpectedDuration());
	}
}
