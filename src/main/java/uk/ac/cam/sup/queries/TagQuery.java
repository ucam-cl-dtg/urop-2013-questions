package uk.ac.cam.sup.queries;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.util.HibernateUtil;

public class TagQuery {
	
	private Criteria criteria;
	private TagQuery(Criteria criteria) {
		
		this.criteria = criteria;
	}
	
	@SuppressWarnings("unchecked")
	public List<Tag> list() {
		return criteria.list();
	}
	
	public static TagQuery all() {
		return new TagQuery (
				HibernateUtil.getTransactionSession()
					.createCriteria(Tag.class)
					.addOrder(Order.asc("name"))
		);
	}
	
	public static Tag get(String name) {
		return Tag.get(name);
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
	
	public TagQuery notContainedIn(Set<Tag> tags) {
		for(Tag tag: tags) {
			criteria.add(Restrictions.not(
					Restrictions.eq("name", tag.getName()).ignoreCase()
			));
		}
		return this;
	}
	
	public TagQuery maxResults(int amount){
		criteria.setMaxResults(amount);
		return this;
	}
	
	public TagQuery offset(int offset){
		criteria.setFirstResult(offset);
		return this;
	}

}
