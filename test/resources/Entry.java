package resources;

import java.util.Date;

public class Entry {
	
	private String title;
	private Date posted;
	private String body;
	
	public Entry (String title, Date posted, String body) {
		this.title = title;
		this.posted = posted;
		this.body = body;
	}

	public String getTitle() {
		return title;
	}

	public Date getPosted() {
		return posted;
	}

	public String getBody() {
		return body;
	}

}
