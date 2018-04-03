package nl.sikken.bertrik;

import java.util.Locale;

/**
 * Collection of stuff related to a server we upload to.
 */
public final class ServerInfo {
	
	private final String url;
	private final String user;
	private final String pass;

	/**
	 * Constructor.
	 * 
	 * @param url the URL
	 * @param user the user name
	 * @param pass the password
	 */
	public ServerInfo(String url, String user, String pass) {
		this.url = url;
		this.user = user;
		this.pass = pass;
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

	public String toString() {
		return String.format(Locale.US, "{url=%s,user=%s,pass=%s}", url, user, pass);
	}
	
}
