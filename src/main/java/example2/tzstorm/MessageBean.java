package example2.tzstorm;

import com.google.gson.Gson;

public class MessageBean {

	private static final Gson gson = new Gson();

	public static MessageBean parse(String line) {
		return gson.fromJson(line, MessageBean.class);
	}

	private String timestamp;
	private String hostname;
	private String client_ip;

	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getHostname() { 
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getClient_ip() {
		return client_ip;
	}
	public void setClient_ip(String client_ip) {
		this.client_ip = client_ip;
	}

}
