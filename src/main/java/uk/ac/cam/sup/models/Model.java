package uk.ac.cam.sup.models;

import org.hibernate.Session;
import org.hibernate.exception.GenericJDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.sup.HibernateUtil;

public class Model {
	Logger log = LoggerFactory.getLogger(Model.class);
	
	public final void save() {
		Session session = HibernateUtil.getTransactionSession();
		try {
			session.save(this);
		} catch (GenericJDBCException e) {
			session.getTransaction().commit();
			log.warn("Exception on model save: "+e.getMessage());
		}
	}

	public final void update() {
		Session session = HibernateUtil.getTransactionSession();
		try {
			session.update(this);
		} catch (GenericJDBCException e) {
			session.getTransaction().commit();
			log.warn("Exception on model update: "+e.getMessage());
		}
	}

	public final void saveOrUpdate() {
		Session session = HibernateUtil.getTransactionSession();
		try {
			session.saveOrUpdate(this);
		} catch (GenericJDBCException e) {
			session.getTransaction().commit();
			log.warn("Exception on model saveOrUpdate: "+e.getMessage());
		}
	}

	public final void delete() {
		Session session = HibernateUtil.getTransactionSession();
		try {
			session.delete(this);
		} catch (GenericJDBCException e) {
			session.getTransaction().commit();
			log.warn("Exception on model delete: "+e.getMessage());
		}
	}
	
}
