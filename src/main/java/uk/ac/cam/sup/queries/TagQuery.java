package uk.ac.cam.sup.queries;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import uk.ac.cam.sup.HibernateUtil;
import uk.ac.cam.sup.models.Tag;

public class TagQuery {
	
	private Criteria criteria;
	private TagQuery(Criteria criteria) {
		this.criteria = criteria;
	}
	
	public List<?> list() {
		return criteria.list();
	}
	
	public static TagQuery all() {
		return new TagQuery (
				HibernateUtil.getSession()
					.createCriteria(Tag.class)
		);
	}
	
	public TagQuery contains(String pattern) {
		criteria.add(Restrictions.ilike("name", pattern, MatchMode.ANYWHERE));
		return this;
	}
	
	public TagQuery startsWith(String pattern) {
		criteria.add(Restrictions.ilike("name", pattern, MatchMode.START));
		return this;
	}
	
	public TagQuery endsWith(String pattern) {
		criteria.add(Restrictions.ilike("name", pattern, MatchMode.END));
		return this;
	}


}
