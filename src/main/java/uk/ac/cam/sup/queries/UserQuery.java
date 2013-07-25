package uk.ac.cam.sup.queries;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.sup.HibernateUtil;
import uk.ac.cam.sup.exceptions.ModelNotFoundException;
import uk.ac.cam.sup.models.User;

public class UserQuery {
	
	private static Logger log = LoggerFactory.getLogger(UserQuery.class);
	
	public static User get(String userID) throws ModelNotFoundException{
		Session session = HibernateUtil.getTransactionSession();
		log.debug("Trying to get user with user ID " + userID);
		User result = (User) session
				.createQuery("from User where id = :id")
				.setParameter("id", userID)
				.uniqueResult();
		if(result == null) {throw new ModelNotFoundException("The User with id " + userID + " was not found in the data base");}
		return result;
	}
}
