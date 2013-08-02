package uk.ac.cam.sup.form;


public class QuestionAdd extends QuestionForm {

	public QuestionAdd() {
		super();
	}
	
	public QuestionAdd(String content, String notes,
			Integer setID, Integer expectedDuration) {
		super(content, notes, setID, expectedDuration);
	}

	@Override
	protected boolean forceLoad() {
		return true;
	}

}
