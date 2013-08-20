package uk.ac.cam.sup.form;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.sup.exceptions.FormValidationException;
import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.queries.QuestionQuery;
import uk.ac.cam.sup.queries.QuestionSetQuery;

public class QuestionSetExport {
	private Logger log = LoggerFactory.getLogger(QuestionSetExport.class);
	
	@FormParam("targetSetId")
	private Integer targetId;
	
	@FormParam("questions")
	private String questionList;
	
	private QuestionSet target;
	private List<Question> questions;
	
	public QuestionSet getTarget() {
		return this.target;
	}
	
	public List<Question> getQuestions() {
		return this.questions;
	}
	
	public final QuestionSetExport validate() throws FormValidationException {
		
		if (targetId == null) {
			throw new FormValidationException("Target not specified");
		}
		
		if (questionList == null) {
			questionList = "";
		}
		
		return this;
	}
	
	public final QuestionSetExport parse() {
		log.debug("Getting target set with ID " + targetId);
		target = QuestionSetQuery.get(targetId);
		
		questions = new ArrayList<Question>();
		String[] split = questionList.split(",");
		log.debug("Split question list into array of length " + split.length + " (list: \"" + questionList + "\")");
		for (String s: split) {
			int id;
			try {
				id = Integer.parseInt(s);
			} catch (Exception e) {
				continue;
			}
			Question q = QuestionQuery.get(id);
			if (q != null) { questions.add(q); }
		}
		
		return this;
	}
	
}
