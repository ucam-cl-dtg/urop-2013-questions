package uk.ac.cam.sup.form;

import javax.ws.rs.FormParam;

import uk.ac.cam.sup.exceptions.FormValidationException;
import uk.ac.cam.sup.models.Data;

public abstract class QuestionSetForm {
	protected boolean validated = false;
	
	protected abstract boolean forceLoad();
	
	@FormParam("name")
	private String name;
	
	@FormParam("plan_type")
	private String planType;
	
	@FormParam("plan_text")
	private String planText;
	
	@FormParam("plan_file")
	private byte[] planFile;
	
	@FormParam("plan_desc")
	private String planDescription;
	
	private Data planData;

	public QuestionSetForm(){
	}
	
	public QuestionSetForm(String name, String plan_type, String plan_text, String plan_desc){
		this.name = name;
		this.planType = plan_type;
		this.planText = plan_text;
		this.planDescription = plan_desc;
	}
	
	public final String getName() {
		return name;
	}
	
	public final Data getPlan() {
		return planData;
	}
	
	public final byte[] getFile() {
		return planFile;
	}
	
	public QuestionSetForm validate() throws Exception {
		if (planType == null) {
			throw new FormValidationException("Plan type not specified");
		}
		
		if (planText == null) {
			planText = "";
		}
		
		if (name == null) {
			name = "";
		}
		
		validated = true;
		
		return this;
	}
	
	public QuestionSetForm parse() throws FormValidationException {
		if (!validated) {
			throw new FormValidationException("Form was not validated");
		}
		
		planData = new Data(planType, planText, planFile, planDescription, forceLoad());
		
		return this;
	}
}
