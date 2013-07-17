package uk.ac.cam.sup.ppdloader;

public class Topic {
	private String name;
	private String link;
	
	public Topic(String name, String link) {
		this.name = name;
		this.link = link;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getLink() {
		return this.link;
	}
	
	public boolean equals(Object o) {
		return this.name.equals(((Topic)o).name)
			&& this.link.equals(((Topic)o).link);
	}
	
	public int hashCode() {
		return (name+link).hashCode();
	}
}
