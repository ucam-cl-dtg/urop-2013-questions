package uk.ac.cam.sup.queries;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.sup.exceptions.ModelNotFoundException;
import uk.ac.cam.sup.models.User;
import uk.ac.cam.sup.util.HibernateUtil;

public class UserQuery {
	
	private static Logger log = LoggerFactory.getLogger(UserQuery.class);
	private Criteria criteria;
	
	private UserQuery(Criteria c){
		criteria = c;
	}
	
	public static UserQuery all(){
		UserQuery uq = new UserQuery(HibernateUtil.getTransactionSession()
				.createCriteria(User.class)	
				.addOrder(Order.asc("id"))
				.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY));
		return uq;
	}
	
	
	public static User get(String userID) throws ModelNotFoundException{
		Session session = HibernateUtil.getTransactionSession();
		log.debug("Trying to get user with user ID " + userID);
		User result = (User) session
				.createQuery("from User where id = :id")
				.setParameter("id", userID)
				.uniqueResult();
		if(result == null) {throw new ModelNotFoundException("The User with id " + userID + " was not found in the data base");}
		log.debug("Returning user with user ID " + userID);
		return result;
	}
	
	public UserQuery idStartsWith(String idpart){
		criteria.add(Restrictions.ilike("id", idpart, MatchMode.START));
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public List<User> list() {
		return criteria.list();
	}
	
	public UserQuery maxResults(int amount){
		criteria.setMaxResults(amount);
		return this;
	}
	
	public UserQuery offset(int offset){
		criteria.setFirstResult(offset);
		return this;
	}
	
}
