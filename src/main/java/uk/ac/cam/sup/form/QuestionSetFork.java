package uk.ac.cam.sup.form;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;

import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.queries.QuestionSetQuery;

public class QuestionSetFork {
	
	@FormParam("sourceSetId")
	private Integer sourceId;
	
	@FormParam("targetSetId")
	private Integer targetId;
	
	@FormParam("questions")
	private String questionList;
	
	private QuestionSet source;
	private QuestionSet target;
	private List<Question> questions;
	
	public QuestionSet getTarget() {
		return this.target;
	}
	
	public List<Question> getQuestions() {
		return this.questions;
	}
	
	public final QuestionSetFork validate() throws Exception {
		if (sourceId == null) {
			throw new Exception("Source not specified");
		}
		
		if (targetId == null) {
			throw new Exception("Target not specified");
		}
		
		if (questionList == null) {
			questionList = "";
		}
		
		return this;
	}
	
	public final QuestionSetFork parse() {
		source = QuestionSetQuery.get(sourceId);
		target = QuestionSetQuery.get(targetId);
		
		questions = new ArrayList<Question>();
		String[] split = questionList.split(",");
		for (String s: split) {
			questions.add(source.getQuestion(Integer.parseInt(s)+1));
		}
		
		return this;
	}
	
}
