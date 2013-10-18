package uk.ac.cam.sup.util;

import java.util.List;

import uk.ac.cam.cl.dtg.teaching.hibernate.HibernateUtil;
import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.queries.QuestionQuery;
import uk.ac.cam.sup.queries.QuestionSetQuery;

public class DataTypeFixer {
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		List<QuestionSet> qslist = QuestionSetQuery.all().list();
		for (QuestionSet qs: qslist) {
			if (qs.getPlan().isString()) {
				qs.getPlan().setType(DataType.PLAIN_TEXT);
			} else {
				qs.getPlan().setType(DataType.EMPTY);
			}
			qs.update();
		}
		HibernateUtil.getInstance().getSession().getTransaction().commit();
		
		List<Question> qlist = QuestionQuery.all().list();
		for (Question q: qlist) {
			if (q.getNotes().isString()) {
				q.getNotes().setType(DataType.PLAIN_TEXT);
			} else {
				q.getNotes().setType(DataType.EMPTY);
			}
			
			if (q.getContent().isString()) {
				q.getContent().setType(DataType.PLAIN_TEXT);
			} else {
				q.getContent().setType(DataType.EMPTY);
			}
			
			q.update();
		}
		HibernateUtil.getInstance().getSession().getTransaction().commit();
	}
	
}
