package uk.ac.cam.sup.form;

import javax.ws.rs.FormParam;

import uk.ac.cam.sup.models.Data;

public class QuestionSetForm {
	@FormParam("name")
	private String name;
	
	@FormParam("plan")
	private String plan;
	private Data planData;
	
	public QuestionSetForm(String name, String plan){
		this.name = name;
		this.plan = plan;
	}
	
	public final String getName() {
		return name;
	}
	
	public final Data getPlan() {
		return planData;
	}
	
	public QuestionSetForm validate() throws Exception {
		if (name == null) {
			name = "";
		}
		return this;
	}
	
	public QuestionSetForm parse() {
		if (plan == null) {
			planData = new Data(false, null);
		} else {
			planData = new Data(true, plan);
		}
		return this;
	}
}
