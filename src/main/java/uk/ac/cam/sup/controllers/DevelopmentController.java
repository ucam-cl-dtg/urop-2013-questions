package uk.ac.cam.sup.controllers;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.sup.HibernateUtil;
import uk.ac.cam.sup.models.Data;
import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.models.User;
import uk.ac.cam.sup.util.DataType;

@Path("/dev")
public class DevelopmentController extends GeneralController {
	
	private static Logger log = LoggerFactory.getLogger(DevelopmentController.class);
	
	@GET
	@Path("/addquestionstosets")
	public void addQuestionsToSets(){
		
		Session session = HibernateUtil.getTransactionSession();
		User jcb = (User)session.createQuery("from User where id = ?").setString(0, "jcb98").uniqueResult();
		
		Question a = new Question(jcb);
		a.setContent(new Data(DataType.PLAIN_TEXT, "Hahaha this is a question"));
		a.setTimeStamp(new Date());
		
		Question b = new Question(jcb);
		b.setContent(new Data(DataType.PLAIN_TEXT, "the most impossiblest question ever"));
		b.setTimeStamp(new Date());
		
		Question c = (Question)session.createQuery("from Question where id = ?").setInteger(0, 1).uniqueResult();
		Question d = (Question)session.createQuery("from Question where id = ?").setInteger(0, 2).uniqueResult();
		Question e = (Question)session.createQuery("from Question where id = ?").setInteger(0, 4).uniqueResult();
		
		QuestionSet x = (QuestionSet)session.createQuery("from QuestionSet where id = ?").setInteger(0, 8).uniqueResult();
		QuestionSet y = (QuestionSet)session.createQuery("from QuestionSet where id = ?").setInteger(0, 6).uniqueResult();
		QuestionSet z = (QuestionSet)session.createQuery("from QuestionSet where id = ?").setInteger(0, 5).uniqueResult();
		
		x.addQuestion(a);
		x.addQuestion(b);
		
		y.addQuestion(a);
		y.addQuestion(b);
		y.addQuestion(c);
		y.addQuestion(d);
		y.addQuestion(e);
		z.addQuestion(d);
		z.addQuestion(c);
		z.addQuestion(a);
		
		session.saveOrUpdate(a);
		session.saveOrUpdate(b);
		session.saveOrUpdate(x);
		session.saveOrUpdate(y);
		session.saveOrUpdate(z);
		
		session.getTransaction().commit();
		
		log.debug("(Hopefully) successfully managed to add stuff to database...");
	}
}
