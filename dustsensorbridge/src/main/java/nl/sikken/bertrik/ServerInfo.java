package nl.sikken.bertrik;

import java.util.Locale;

/**
 * Collection of stuff related to a server we upload to.
 */
public final class ServerInfo {
	
	private final String url;
	private final String user;
	private final String pass;
	private final String id;

	/**
	 * Constructor.
	 * 
	 * @param url the URL
	 * @param user the user name
	 * @param pass the password
	 * @param id the unique sensor id
	 */
	public ServerInfo(String url, String user, String pass, String id) {
		this.url = url;
		this.user = user;
		this.pass = pass;
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public String getPass() {
		return pass;
	}

	public String getId() {
		return id;
	}
	
	public String toString() {
		return String.format(Locale.US, "{url=%s,user=%s,pass=%s,id=%s}", url, user, pass, id);
	}
	
}
