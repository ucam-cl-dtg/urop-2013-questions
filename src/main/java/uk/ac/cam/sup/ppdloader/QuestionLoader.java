package uk.ac.cam.sup.ppdloader;

import uk.ac.cam.sup.models.Data;
import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.models.User;

public class QuestionLoader extends Loader<Question> {
	private Topic topic;
	
	public QuestionLoader(Topic t) {
		super (
				"http://www.cl.cam.ac.uk/teaching/exams/pastpapers/" + t.getLink(),
				"<a href=\"([\\w.]*.pdf)\">([\\w\\s]*)</a>"
		);
		this.topic = t;
	}

	@Override
	protected Question parseGroups(String[] groups) {
		Question q = new Question(new User("mr595"));
		
		q.setContent(new Data(false, groups[1]));
		q.addTag(new Tag(topic.getName()));
		
		return q;
	}
	
}
