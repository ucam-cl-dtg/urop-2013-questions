package uk.ac.cam.sup.form;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;

import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.queries.QuestionQuery;

public class QuestionSetEdit extends QuestionSetForm {
	
	public QuestionSetEdit() {
		super();
	}
	
	public QuestionSetEdit(String name, String plan_type, String plan_text, String plan_desc) {
		super(name, plan_type, plan_text, plan_desc);
	}

	@FormParam("setid")
	private Integer setId;
	
	@FormParam("questions")
	public String questionlist;
	
	private List<Question> questions;
	
	public int getSetId() {
		return setId;
	}
	
	public List<Question> getQuestions() {
		return questions;
	}
	
	public int getQuestionSetId() {
		return setId;
	}
	
	public QuestionSetEdit validate() throws Exception {
		super.validate();
		
		if (setId == null) {
			throw new Exception("No setId specified");
		}
		
		if (questionlist == null) {
			questionlist = "";
		}
		
		return this;
	}
	
	public QuestionSetEdit parse() throws Exception {
		super.parse();
		
		questions = new ArrayList<Question>();
		String[] split = questionlist.split(",");
		for (String s: split) {
			if (s.equals("")) { break ; }
			int id = Integer.parseInt(s);
			questions.add(QuestionQuery.get(id));
		}
		
		return this;
	}

	@Override
	protected boolean forceLoad() {
		return false;
	}
}
