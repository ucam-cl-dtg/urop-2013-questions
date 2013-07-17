package uk.ac.cam.sup.ppdloader;


public class TopicLoader extends Loader<Topic> {
	
	public TopicLoader() {
		super(
				"http://www.cl.cam.ac.uk/teaching/exams/pastpapers/",
				"<a href=\"(t-[\\w.]*)\">([\\w\\s]*)</a>"
		);
	}

	@Override
	protected Topic parseGroups(String[] groups) {
		return new Topic(groups[2], groups[1]);
	}
	
}
