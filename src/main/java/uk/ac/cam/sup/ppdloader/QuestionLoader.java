package uk.ac.cam.sup.ppdloader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import uk.ac.cam.sup.models.Data;
import uk.ac.cam.sup.models.Question;
import uk.ac.cam.sup.models.User;
import uk.ac.cam.sup.queries.TagQuery;
import uk.ac.cam.sup.util.DataType;

public class QuestionLoader extends Loader<Question> {
	private Topic topic;
	
	public QuestionLoader(Topic t) {
		super (
				"http://www.cl.cam.ac.uk/teaching/exams/pastpapers/" + t.getLink(),
				"<a href=\"([\\w.]*.pdf)\">[\\w\\s]*</a>\\s*(</li>|(=|&#8211;)\\s*[\\s\\w]*<a href=\"([\\w:/.]*.pdf)\">)"
		);
		this.topic = t;
	}
	
	private static String downloadPDF(String source) throws IOException {
		URL pdf = new URL(source);
		ReadableByteChannel rbc = Channels.newChannel(pdf.openStream());
		
		ServletContext context = ResteasyProviderFactory.getContextData(ServletContext.class);
		String rootLocation = context.getInitParameter("storageLocation")+"local/data/questions/";
		
		String filename = "data-"+UUID.randomUUID().toString()+".pdf";
		FileOutputStream fos = new FileOutputStream(rootLocation+filename);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		
		return filename;
	}

	@Override
	protected Question parseGroups(String[] groups) throws IOException {
		Question q = new Question(new User("bot1000"));
		q.addTag(TagQuery.get(topic.getName()));
		q.addTag(TagQuery.get("Past papers"));
		q.setTimeStamp(parseDate(groups[1]));
		q.setExpectedDuration(30);
		
		String contentFilename = downloadPDF("http://www.cl.cam.ac.uk/teaching/exams/pastpapers/"+groups[1]);
		q.setContent(new Data(DataType.FILE, contentFilename, groups[1]));
		
		if (groups[4] != null) {
			String notesFilename = downloadPDF(groups[4]);
			q.setNotes(new Data(DataType.FILE, notesFilename, "Official solution notes"));
		}
		
		return q;
	}
	
	private Date parseDate(String filename) {
		GregorianCalendar c = new GregorianCalendar();
		c.set(Integer.parseInt(filename.substring(1, 5)),Calendar.JUNE,1,13,0,0);
		return c.getTime();
	}
	
}
