package uk.ac.cam.sup.form;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;

import uk.ac.cam.sup.models.Data;
import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.queries.QuestionQuery;

public class QuestionSetEdit extends QuestionSetForm {
	
	public QuestionSetEdit(String name, String plan) {
		super(name, plan);
	}

	@FormParam("setid")
	private Integer setId;
	
	@FormParam("delete")
	public String deleteList;
	
	@FormParam("neworder")
	public String newOrderList;
	
	private List<Question> deletedQuestions;
	private List<Question> orderedQuestions;
	
	public int getSetId() {
		return setId;
	}
	
	public List<Question> getDeletedQuestions() {
		return deletedQuestions;
	}
	
	public List<Question> getOrderedQuestions() {
		return orderedQuestions;
	}
	
	public int getQuestionSetId() {
		return setId;
	}
	
	public QuestionSetEdit validate() throws Exception {
		super.validate();
		
		if (setId == null) {
			throw new Exception("No setId specified");
		}
		
		if (deleteList == null) {
			deleteList = "";
		}
		
		if (newOrderList == null) {
			newOrderList = "";
		}
		
		return this;
	}
	
	public QuestionSetEdit parse() {
		super.parse();
		
		deletedQuestions = new ArrayList<Question>();
		String[] split = deleteList.split(",");
		for (String s: split) {
			if (s.equals("")) { break ; }
			int id = Integer.parseInt(s);
			deletedQuestions.add(QuestionQuery.get(id));
		}
		
		orderedQuestions = new ArrayList<Question>();
		split = newOrderList.split(",");
		for (String s: split) {
			if (s.equals("")) { break ; }
			int id = Integer.parseInt(s);
			orderedQuestions.add(QuestionQuery.get(id));
		}
		
		return this;
	}
}
