package uk.ac.cam.sup.models;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.cl.dtg.teaching.hibernate.HibernateUtil;

public class Model {
	private static Logger log = LoggerFactory.getLogger(Model.class);
	
	public final void save() {
		Session session = HibernateUtil.getInstance().getSession();
		try {
			log.debug("Trying to SAVE model " + this.getClass().toString());
			session.save(this);
			log.debug("     Success on model save!");
		} catch (Exception e) {
			session.getTransaction().rollback();
			log.warn("Exception on model save: "+e.getMessage());
		}
	}

	public final void update() {
		Session session = HibernateUtil.getInstance().getSession();
		try {
			session.update(this);
		} catch (Exception e) {
			session.getTransaction().rollback();
			log.warn("Exception on model update: "+e.getMessage());
		}
	}

	public final void saveOrUpdate() {
		Session session = HibernateUtil.getInstance().getSession();
		try {
			session.saveOrUpdate(this);
		} catch (Exception e) {
			session.getTransaction().rollback();
			log.warn("Exception on model saveOrUpdate: "+e.getMessage());
		}
	}

	public final void delete() {
		Session session = HibernateUtil.getInstance().getSession();
		try {
			session.delete(this);
		} catch (Exception e) {
			session.getTransaction().rollback();
			log.warn("Exception on model delete: "+e.getMessage());
		}
	}
	
}
