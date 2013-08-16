package uk.ac.cam.sup.controllers;

import java.util.Date;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.sup.exceptions.ModelNotFoundException;
import uk.ac.cam.sup.models.Data;
import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.models.User;
import uk.ac.cam.sup.queries.UserQuery;
import uk.ac.cam.sup.util.DataType;
import uk.ac.cam.sup.util.HibernateUtil;

import com.google.common.collect.ImmutableMap;

@Path("/dev")
public class DevelopmentController extends GeneralController {
	
	private static Logger log = LoggerFactory.getLogger(DevelopmentController.class);
	
	@GET
	@Path("/commit")
	@Produces("application/json")
	public Map<String,String> commitDB(){
		try{
			HibernateUtil.commit();
			return ImmutableMap.of("status", "commited.");
		} catch(Exception e) {
			return ImmutableMap.of("status", "error. Message: " + e.getMessage());
		}
		
	}
	
	@GET
	@Path("/rconf")
	@Produces("application/json")
	public Map<String,String> reconfigerDB(){
		try{
			HibernateUtil.reconfigure();
			return ImmutableMap.of("status", "reconfigured");
		} catch(Exception e) {
			return ImmutableMap.of("status", "error. Message: " + e.getMessage());
		}
	}
	
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
	
	@GET
	@Path("/{uid}/toggle_sup")
	@Produces("application/json")
	public Map<String,?> toggleSup(@PathParam("uid") String uid){
		boolean newUser = false;
		User u;
		
		try {
			u = UserQuery.get(uid);
			if(u.getSupervisor()){
				u.setSupervisor(false);
			} else {
				u.setSupervisor(true);
			}
		} catch (ModelNotFoundException e) {
			newUser = true;
			u = new User(uid);
			u.save();
		}
		
		return ImmutableMap.of("newUser", newUser, "UserID", uid, "isSupervisor", u.getSupervisor());
		
	}
}
