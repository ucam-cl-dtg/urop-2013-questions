package uk.ac.cam.sup.form;

import javax.ws.rs.FormParam;

public class QuestionRemove {
	@FormParam("qid")
	private int qid;
	
	@FormParam("sid")
	private int sid;
	
	public int getQid(){return qid;}
	public int getSid(){return sid;}
}
