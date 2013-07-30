package uk.ac.cam.sup.form;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;

import uk.ac.cam.sup.exceptions.FormValidationException;
import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.queries.QuestionQuery;
import uk.ac.cam.sup.queries.QuestionSetQuery;

public class QuestionEdit extends QuestionForm {
	
	public QuestionEdit() {
		super();
	}
	
	public QuestionEdit(String content, String notes,
			Integer setID, Integer expectedDuration) {
		super(content, notes, setID, expectedDuration);
	}

	private boolean validated = false;
	
	@FormParam("id")
	private Integer id;
	
	@FormParam("minor")
	private Boolean isMinor;
	
	@FormParam("sets")
	private String strSets;
	private List<QuestionSet> sets;
	
	public Integer getId() throws FormValidationException {
		if(!validated) {
			throw new FormValidationException("Form was not yet validated");
		}
		return this.id;
	}
	
	public boolean isMinor() throws FormValidationException {
		if (!validated) {
			throw new FormValidationException("Form was not yet validated");
		}
		return this.isMinor;
	}
	
	public List<QuestionSet> getSets() throws FormValidationException {
		if(!validated){
			throw new FormValidationException("Form was not yet validated");
		}
		if(super.getSetId() != -1){
			throw new FormValidationException("This form doesn't contain multpile sets to edit!");
		}
		return sets;
	}
	
	public QuestionEdit validate() throws FormValidationException {
		super.validate();
		sets = new ArrayList<QuestionSet>();
		
		if(super.getSetId() == -1 && !this.isMinor){

			try{
				String[] arraySets = strSets.split(",");
			
				for(int i = 0; i < arraySets.length; i++){
					sets.add(QuestionSetQuery.get(Integer.parseInt(arraySets[i])));
				}
			}catch(Exception e){
				throw new FormValidationException("Error while trying to parse the set list!\n" + e.getMessage());
			}
		}
		if (id == null || (super.getSetId() == -1 && !isMinor && (sets.size() < 1 || sets==null)) || QuestionQuery.get(id) == null) {
			throw new FormValidationException("Question " + id + " or set(s) " + super.getSetId() + " does not exist");
		}
		
		
		if (isMinor == null) {
			isMinor = false;
		}
		
		validated = true;
		
		return this;
	}
}
