package uk.ac.cam.sup.form;

import javax.ws.rs.FormParam;

public class TagAdd {
	@FormParam("qid")
	private int qid;
	
	@FormParam("newTags")
	private String newTags;
	
	public int getQid(){return qid;}
	
	public String getNewTagsString(){return newTags;}
	public String[] getNewTags(){
		if(newTags == null || newTags == ""){return null;}
		return newTags.split(",");
	}
}
