package uk.ac.cam.sup.form;

import javax.ws.rs.FormParam;

import uk.ac.cam.sup.models.Data;
import uk.ac.cam.sup.queries.QuestionQuery;
import uk.ac.cam.sup.queries.QuestionSetQuery;

public class QuestionEdit {
	@FormParam("id")
	private Integer id;
	
	@FormParam("content")
	private String content;
	private Data dcontent;
	
	@FormParam("notes")
	private String notes;
	private Data dnotes;
	
	@FormParam("minor")
	private Boolean isMinor;
	
	@FormParam("setId")
	private Integer setId;
	
	public Data getContent() throws RuntimeException {
		if (this.dcontent == null) {
			throw new RuntimeException("Form was not yet validated");
		}
		return this.dcontent;
	}
	
	public Data getNotes() throws RuntimeException {
		if (this.dnotes == null) {
			throw new RuntimeException("Form was not yet validated");
		}
		return this.dnotes;
	}
	
	public Integer getId() throws RuntimeException {
		if(this.dnotes == null) {
			throw new RuntimeException("Form was not yet validated");
		}
		return this.id;
	}
	
	public Integer getSetId() throws RuntimeException {
		if(this.dnotes == null) {
			throw new RuntimeException("Form was not yet validated");
		}
		return this.setId;
	}
	
	public Boolean isMinor() {
		return this.isMinor;
	}
	
	public QuestionEdit validate() throws Exception {
		if (id == null || QuestionQuery.get(id) == null) {
			throw new Exception("Question you're trying to edit does not exist");
		}
		
		if (setId == null || QuestionSetQuery.get(setId) == null) {
			throw new Exception("Question you're trying to edit does not belong to any set");
		}
		
		if (content == null || content.equals("")) {
			dcontent = new Data(false, null);
		} else {
			dcontent = new Data(true, content);
		}
		
		if (notes == null || notes.equals("")) {
			dnotes = new Data(false, null);
		} else {
			dnotes = new Data(true, notes);
		}
		
		if (isMinor == null) {
			isMinor = false;
		}
		
		return this;
	}
}
