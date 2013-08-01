package uk.ac.cam.sup.form;


public class QuestionSetAdd extends QuestionSetForm {

	public QuestionSetAdd() {
		super();
	}
	
	public QuestionSetAdd(String name, String plan_type, String plan_text, String plan_desc) {
		super(name, plan_type, plan_text, plan_desc);
	}

	@Override
	protected boolean forceLoad() {
		return true;
	}

}
