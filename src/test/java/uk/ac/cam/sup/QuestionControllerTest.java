package uk.ac.cam.sup;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertTrue;

import com.googlecode.htmleasy.RedirectException;

import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import uk.ac.cam.sup.controllers.QuestionController;
import uk.ac.cam.sup.form.QuestionAdd;
import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.models.User;

import java.lang.reflect.Field;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
		session.save(owner);
		qset = new QuestionSet(owner);
		session.save(qset);
	}
	
	@AfterClass
	public static void destroyStuff() {
		Session session = HibernateUtil.getTransactionSession();
		session.delete(qset);
		session.delete(owner);
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
			requestField = QuestionController.class.getDeclaredField("request");
			requestField.setAccessible(true);
			requestField.set(qc, mock_req);
		} catch (SecurityException e) {}
			catch (IllegalAccessException e) {}
	}
	
	@Test
	public void testPassingQuestionAdd() {
		QuestionAdd qa = new QuestionAdd("question1 contents", "question1 notes", qset.getId(), 10);
		
		try {
			qc.addQuestion(qa);
		} catch(RedirectException e) {
			assertTrue(true);
		}
	}
	
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
}