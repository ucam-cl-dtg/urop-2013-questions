package uk.ac.cam.sup.ppdloader;

import java.util.HashSet;
import java.util.Set;

import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.Tag;

public class PPDLoader {
	public static Set<Question> loadAllQuestions() throws Exception {
		Set<Question> result = new HashSet<Question>();
		TopicLoader tl = new TopicLoader();
		
		for (Topic t: tl.getResults()) {
			QuestionLoader ql = new QuestionLoader(t);
			for (Question q: ql.getResults()) {
				result.add(q);
			}
		}
		
		return result;
	}
	
	public static void loadTopicsIntoDatabase() throws Exception {
		TopicLoader tl = new TopicLoader();
		Set<Topic> ts = tl.getResults();
		for (Topic t: ts) {
			Tag tag = new Tag(t.getName());
			tag.saveOrUpdate();
		}
	}
	
	public static void main(String[] args) throws Exception {
		TopicLoader tl = new TopicLoader();
		
		for (Topic t: tl.getResults()) {
			QuestionLoader ql = new QuestionLoader(t);
			for (Question q: ql.getResults()) {
				System.out.print(q.getContent().getData()+" - ");
				System.out.print(q.getTimeStamp().toString()+" - ");
				System.out.print(q.getTagsAsString().toArray(new String[0])[0]);
				if (q.getNotes().getData() != null) {
					System.out.print(" - " + q.getNotes().getData());
				}
				System.out.println();
			}
		}
	}
}
