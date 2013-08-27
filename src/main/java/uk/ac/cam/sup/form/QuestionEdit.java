package uk.ac.cam.sup.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.sup.exceptions.FormValidationException;
import uk.ac.cam.sup.models.QuestionSet;
import uk.ac.cam.sup.queries.QuestionQuery;
import uk.ac.cam.sup.queries.QuestionSetQuery;

public class QuestionEdit extends QuestionForm {
	
	private static Logger log = LoggerFactory.getLogger(QuestionForm.class); 
	
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
		if(setId != -1){
			throw new FormValidationException("This form doesn't contain multpile sets to edit!");
		}
		return sets;
	}
	
	public QuestionEdit validate() throws FormValidationException {
		super.validate();
		sets = new ArrayList<QuestionSet>();
		
		if (isMinor == null) {
			isMinor = false;
		}
		
		try {
			if(setId == -1 && !this.isMinor && strSets != null && strSets.length() >= 1) {
				
				try {
					String[] arraySets = strSets.split(",");
				
					for(int i = 0; i < arraySets.length; i++){
						sets.add(QuestionSetQuery.get(Integer.parseInt(arraySets[i])));
					}
				} catch(Exception e) {
					throw new FormValidationException("Error while trying to parse the set list!\n" + e.getMessage());
				}
			}
			if (setId != -1 && (id == null || QuestionQuery.get(id) == null)) {
				throw new FormValidationException("Question " + id + " or set(s) " + setId + " does not exist");
			}
			
			validated = true;
			
			return this;
		} catch (NullPointerException e) {
			log.error("Null pointer exception when validating QuestionEdit form!\n"
					+ "Will list if variables are null:"
					+ "\nsetId: " + (setId == null) 
					+ "\nsets: " + (sets == null));
			e.printStackTrace();
			throw new FormValidationException("Null pointer exception when validating the QuestionEdit form!\n" + e.getMessage());
		}
	}
	
	public QuestionEdit parse() throws FormValidationException, IOException {
		super.parse();
		return this;
	}

	@Override
	protected boolean forceLoad() {
		return false;
	}
}
