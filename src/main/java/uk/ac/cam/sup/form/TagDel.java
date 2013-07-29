package uk.ac.cam.sup.form;

import javax.ws.rs.FormParam;

public class TagDel {
	@FormParam("qid")
	private int qid;
	
	@FormParam("tag")
	private String tag;
	
	public int getQid(){return qid;}
	public String getTag(){return tag;}
}
