package uk.ac.cam.sup.form;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.FormParam;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.cam.sup.exceptions.FormValidationException;
import uk.ac.cam.sup.queries.QuestionQuery;

import com.google.common.collect.ImmutableMap;

public class QuestionsAddRemove {
	
	@FormParam("qid")
	private Integer qid;
	
	@FormParam("sets")
	private String setsString;
	private Set<Map<String,?>> sets;
	
	private boolean validated = false;
	
	public final QuestionsAddRemove validate() throws FormValidationException{
		
		if(qid == null || QuestionQuery.get(qid) == null){
			System.out.println("######################### " + qid);
			throw new FormValidationException("Question does not exist!");
		}
		if(setsString == null || setsString.length() < 1){
			throw new FormValidationException("No sets chosen to add/remove questions in!");
		}
		
		try {
			JSONArray jsonArray = new JSONArray(setsString);
			JSONObject jsonMap;
			
			sets = new HashSet<Map<String,?>>();
			
			for(int i = 0; i < jsonArray.length(); i++){
				jsonMap = jsonArray.getJSONObject(i);
				sets.add(ImmutableMap.of("sid", jsonMap.getInt("sid"), "add", jsonMap.getBoolean("useQuestion")));
			}
		} catch (JSONException e) {
			throw new FormValidationException("A JSONException occurred!");
		}
		
		validated = true;
		return this;
	}
	
	public Set<Map<String, ?>> getSets() throws FormValidationException{
		if(!validated) throw new FormValidationException("Form not yet validated!");
		return sets;
	}
	public int getQid() throws FormValidationException {
		if(!validated) throw new FormValidationException("Form not yet validated!");
		return qid;
	}

}
