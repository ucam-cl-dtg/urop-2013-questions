package uk.ac.cam.sup.ppdloader;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashSet;
import java.util.Set;

import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.Tag;
import uk.ac.cam.sup.queries.TagQuery;

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
			Tag tag = TagQuery.get(t.getName());
			tag.saveOrUpdate();
		}
	}
	
	public static void downloadQuestionPDF(Question q, String target) throws Exception {
		URL pdf = new URL(q.getContent().getData());
		ReadableByteChannel rbc = Channels.newChannel(pdf.openStream());
		FileOutputStream fos = new FileOutputStream(target);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
	}
	
	public static void downloadAllQuestionPDFs(String targetDir) throws Exception {
		TopicLoader tl = new TopicLoader();
		int i = 1;
		
		for (Topic t: tl.getResults()) {
			QuestionLoader ql = new QuestionLoader(t);
			
			for (Question q: ql.getResults()) {
				String[] split = q.getContent().getData().split("/");
				String filename = split[split.length-1];
				downloadQuestionPDF(q, targetDir+filename);
				System.out.println(i+": "+filename);
				++i;
			}
		}
		
		System.out.println("Downloaded "+(i-1)+" files");
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
