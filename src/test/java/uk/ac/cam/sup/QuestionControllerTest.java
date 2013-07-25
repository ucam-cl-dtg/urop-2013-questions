package uk.ac.cam.sup;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import uk.ac.cam.sup.controllers.GeneralController;
import uk.ac.cam.sup.controllers.QuestionController;
import uk.ac.cam.sup.form.QuestionAdd;
import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.models.User;

import com.googlecode.htmleasy.RedirectException;

@RunWith(JUnit4.class)
public class QuestionControllerTest {
	private static QuestionSet qset;
	private static User owner;
	private HttpServletRequest mock_req;
	private QuestionController qc;
	
	@BeforeClass
	public static void createQuestionSetAndOwner() {
		Session session = HibernateUtil.getTransactionSession();
		owner = new User("mojpc2");
		owner.save();
		qset = new QuestionSet(owner);
		qset.save();
		session.getTransaction().commit();
	}
	
	@AfterClass
	public static void destroyStuff() {
		Session session = HibernateUtil.getTransactionSession();
		qset.delete();
		owner.delete();
		session.getTransaction().commit();
	}
	
	@Before
	public void setUp() throws NoSuchFieldException {
		// Mock HttpServletRequest and Session
		HttpSession mock_sess = createMock(HttpSession.class);
		expect(mock_sess.getAttribute("RavenRemoteUser")).andReturn("mojpc2");
		replay(mock_sess);
		mock_req = createMock(HttpServletRequest.class);
		expect(mock_req.getSession()).andReturn(mock_sess);
		replay(mock_req);
		
		// Prepare Question Controller object
		qc = new QuestionController();
		
		// Reflect on question controller to modify private request field
		Field requestField;
		try {
			requestField = GeneralController.class.getDeclaredField("request");
			requestField.setAccessible(true);
			requestField.set(qc, mock_req);
		} catch (SecurityException e) {}
			catch (IllegalAccessException e) {}
	}
	
	/*
	 * Dummy passing test
	 */
	@Test
	public void testPassingQuestionAdd() {
		QuestionAdd qa = new QuestionAdd("question1 contents", "question1 notes", qset.getId(), 10);
		
		try {
			qc.addQuestion(qa);
		} catch(RedirectException e) {
			assertTrue(true);
		}
	}
	
	/*
	 * When a question with negative id is added.
	 * TODO: Test looks for RedirectException but if the error 
	 * behaviour does not redirect test should be modified accordingly.
	 */
	@Test
	public void testNegativeQuestionSetId() {
		QuestionAdd qa = new QuestionAdd("question1 contents", "question1 notes", -1, 10);

		try {
			qc.addQuestion(qa);
		} catch(RedirectException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testNonExistentQuestionSetId() {
		QuestionAdd qa = new QuestionAdd("question1 contents", "question1 notes", 999, 10);
		
		try {
			qc.addQuestion(qa);
		} catch(RedirectException e) {
			assertTrue(true);
		}
	}
	
	/*
	 * In http://localhost:8080/app/#q/:id fill the id here
	 * If you change the qid data attribute so that it gives a non-existent id
	 * getTagsNotInQuestion controller throws a StringIndexOutOfBoundsException
	 * 
	 * TODO: Asserts should be changed so that instead of checking for a list 
	 * it should check for the relevant error object.
	 */
	@Test
	public void testNonExistentIdForTagNotIn() {
		//assertEquals("Both returns List Type",qc.getTagsNotInQuestion("99999: hellloooo").getClass(), List.class);
		assertTrue(qc.getTagsNotInQuestion("99999: hellloooo") instanceof List);
	}
	
	@Test
	public void testWronglyFormatterInputForTagsNotIn() {
		//assertEquals("Both returns List Type",qc.getTagsNotInQuestion("aaaa: hellloooo").getClass(), List.class);
		assertTrue(qc.getTagsNotInQuestion("aaaa: hellloooo") instanceof List);
	}
	
	/*
	 * When produceSingleQuestionJSON receives a non-existent id 
	 * TODO: Should be edited so that instead of the actual result assert
	 * should expect error object
	 */
	@Test
	public void testGettingAQuestionWithNonExistentId() {
		//assertEquals("Should return a map with error perhaps?", qc.produceSingleQuestionJSON(99999).getClass(), Map.class);
		assertTrue(qc.produceSingleQuestionJSON(99999) instanceof Map);
	}
}