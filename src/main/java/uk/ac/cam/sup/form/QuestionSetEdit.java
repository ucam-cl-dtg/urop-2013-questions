package uk.ac.cam.sup.form;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;

import uk.ac.cam.sup.models.Data;
import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.queries.QuestionQuery;

public class QuestionSetEdit {
	
	@FormParam("name")
	private String name;
	
	@FormParam("plan")
	private String plan;
	
	@FormParam("setid")
	private Integer setId;
	
	@FormParam("delete")
	private String deleteList;
	
	@FormParam("neworder")
	private String newOrderList;
	
	private Data planData;
	private List<Question> deletedQuestions;
	private List<Question> orderedQuestions;
	
	public String getName() {
		return name;
	}
	
	public int getSetId() {
		return setId;
	}
	
	public Data getPlan() {
		return planData;
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
		if (name == null) {
			name = "";
		}
		
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
	
	public QuestionSetEdit parse() throws Exception {
		if (plan == null) {
			planData = new Data(false, null);
		} else {
			planData = new Data(true, plan);
		}
		
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
