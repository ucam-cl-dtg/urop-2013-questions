package uk.ac.cam.sup.ppdloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Loader<T> {
	
	private String source;
	private String regex;
	private String page;
	
	private URL url;
	private BufferedReader reader;
	private Pattern pattern;
	protected Matcher matcher;
	protected Set<T> results;
	
	public Loader(String source, String regex) {
		this.regex = regex;
		this.source = source;
	}
	
	private String loadPageFromReader(BufferedReader source) throws IOException {
		String result = new String("");
		
		String line;
		while ((line = source.readLine()) != null) {
			result += line;
		}
		
		return result;
	}
	
	private String loadPageFromSource() throws Exception {
		this.url = new URL(source);
		this.reader = new BufferedReader (new InputStreamReader (
				this.url.openStream()
		));
		String page = this.loadPageFromReader(this.reader);
		this.reader.close();
		
		return page;
	}
	
	public Set<T> getResults() throws Exception {
		if (this.results == null) {
			this.loadResults();
		}
		return this.results;
	}

	private void loadResults() throws Exception {
		this.page = this.loadPageFromSource();
		this.pattern = Pattern.compile(this.regex);
		this.matcher = this.pattern.matcher(this.page);
		this.results = new HashSet<T>();
		
		while (this.matcher.find()) {
			String[] groups = new String[this.matcher.groupCount()+1];
			for (int i = 0; i < this.matcher.groupCount()+1; i++) {
				groups[i] = this.matcher.group(i);
			}
			this.results.add(parseGroups(groups));
		}
	}
	
	protected abstract T parseGroups(String[] groups) throws Exception;
}
