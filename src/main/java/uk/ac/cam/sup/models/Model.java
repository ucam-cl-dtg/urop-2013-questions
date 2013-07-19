package uk.ac.cam.sup.models;

import org.hibernate.Session;

import uk.ac.cam.sup.HibernateUtil;

public class Model {
	public final void save() {
		Session session = HibernateUtil.getTransactionSession();
		session.save(this);
	}
	
	public final void update() {
		Session session = HibernateUtil.getTransactionSession();
		session.update(this);
	}
	
	public final void saveOrUpdate() {
		Session session = HibernateUtil.getTransactionSession();
		session.saveOrUpdate(this);
	}
	
	public final void delete() {
		Session session = HibernateUtil.getTransactionSession();
		session.delete(this);
	}
}
