package uk.ac.cam.sup.ppdloader;

import uk.ac.cam.sup.models.Question;

public class PPDLoader {
	public static void main(String[] args) throws Exception {
		TopicLoader tl = new TopicLoader();
		
		for (Topic t: tl.getResults()) {
			QuestionLoader ql = new QuestionLoader(t);
			for (Question q: ql.getResults()) {
				System.out.print(q.getContent().getData()+" - ");
				System.out.print(q.getTagsAsString().toArray(new String[0])[0]);
				if (q.getNotes().getData() != null) {
					System.out.print(" - " + q.getNotes().getData());
				}
				System.out.println();
			}
		}
	}
}
